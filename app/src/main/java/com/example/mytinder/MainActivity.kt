package com.example.mytinder

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.example.mytinder.auth.IntroActivity
import com.example.mytinder.auth.UserInfoModel
import com.example.mytinder.databinding.ActivityMainBinding
import com.example.mytinder.setting.MyPageActivity
import com.example.mytinder.setting.SettingActivity
import com.example.mytinder.slider.CardStackAdapter
import com.example.mytinder.utils.FirebaseAuthUtils
import com.example.mytinder.utils.FirebaseRef
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.yuyakaido.android.cardstackview.CardStackLayoutManager
import com.yuyakaido.android.cardstackview.CardStackListener
import com.yuyakaido.android.cardstackview.CardStackView
import com.yuyakaido.android.cardstackview.Direction

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var cardStackAdapter: CardStackAdapter
    private lateinit var manager : CardStackLayoutManager

    private lateinit var auth: FirebaseAuth

    private val userDataList = ArrayList< UserInfoModel >()

    private var userCount = 0
    private lateinit var myGender : String


    private val TAG = MainActivity::class.java.simpleName


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView( this, R.layout.activity_main )

        auth = Firebase.auth

        val cardStackView : CardStackView = binding.cardStackView
        val settingIcon : ImageView = binding.settingIcon

        settingIcon.setOnClickListener {
            // 마이페이지로 이동.
            val intent = Intent( this, SettingActivity::class.java )
            startActivity( intent )
        }

        manager = CardStackLayoutManager( baseContext, object : CardStackListener {

            override fun onCardDragging(direction: Direction?, ratio: Float) {

            }

            override fun onCardSwiped(direction: Direction?) {
                when(direction) {
                    Direction.Left -> {
                        // 왼쪽 스와이프
                        Log.d( TAG, userDataList[userCount].uid.toString() )

                        val myUid = FirebaseAuthUtils.getUid()
                        val otherUid = userDataList[userCount].uid.toString()

                        disLike(myUid , otherUid )
                    }

                    Direction.Right -> {
                        // 오른쪽 스와이프

                        Log.d( TAG, userDataList[userCount].uid.toString() )

                        val myUid = FirebaseAuthUtils.getUid()
                        val otherUid = userDataList[userCount].uid.toString()

                        Like( myUid , otherUid)

                        // 내가 좋아하는 사람의 LIKE 리스트
                        getOtherUserLikeList( otherUid )
                    }

                }
                userCount += 1

                if( userCount == userDataList.count() ) {
                    getUserDataList( myGender )
                    Toast.makeText(baseContext, "유저 새롭게 받아옵니다.", Toast.LENGTH_SHORT).show()


                    userCount = 0
                }

            }

            override fun onCardRewound() {

            }

            override fun onCardCanceled() {

            }

            override fun onCardAppeared(view: View?, position: Int) {

            }

            override fun onCardDisappeared(view: View?, position: Int) {

            }
        })

        // 카드 스택 뷰에 연결하기.
        cardStackAdapter = CardStackAdapter( baseContext, userDataList )

        cardStackView.adapter = cardStackAdapter
        cardStackView.layoutManager = manager

        // 내 정보를 먼저 가져와서, 나와 다른 성별을 가진 사람을 가져오도록 하자.
        getMyUserData()

        // 유저 데이터 리스트에 담아서 가져오기.
        // getUserDataList()

    }

    // 유저 데이터 카드 뷰에 가져오기

    private fun getUserDataList( currentUserGender : String ) {

        // Read from the database
        FirebaseRef.userInfoRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                // 리스트에 값을 채우기 전에 리스트를 비워놓기
                userDataList.clear()

                // Log.d( "유저 리스트 가져오기", dataSnapshot.toString() )
                /*
                D/유저 리스트 가져오기: DataSnapshot { key = userInfo,
                value = {
                            H2fQDmUpIRZm2WxgarrnXc09TtZ2={uid=H2fQDmUpIRZm2WxgarrnXc09TtZ2, gender=M, city=1, nickname=123, age=2},
                            9krD6iF8oNV2NLcNQYxB1QfI9Xl2={uid=9krD6iF8oNV2NLcNQYxB1QfI9Xl2, gender=m, city=1, nickname=1232, age=2},
                            VceKMNlMFQgqSGaE7eO4anySg7y1={uid=VceKMNlMFQgqSGaE7eO4anySg7y1, gender=m, city=1, nickname=12, age=2}}
                         }
                 */

                for( dataModel in dataSnapshot.children ) {

                    val user = dataModel.getValue(UserInfoModel::class.java)

                    /*
                    val uid = user?.uid.toString()
                    val nickname = user?.nickname.toString()
                    val gender = user?.gender.toString()
                    val city = user?.city.toString()
                    val age = user?.age.toString()
                     */

                    // 나와 다른 성별의 유저일 때 리스트에 담는다.
                    if( user?.gender.toString() != currentUserGender) {
                        userDataList.add( user!! )
                    }

                }

                cardStackAdapter.notifyDataSetChanged()

            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value

            }
        })
    }

    // 내 UID를 통해 내 User 정보를 가져오자.
    private fun getMyUserData() {

        val myUid = FirebaseAuthUtils.getUid()

        // 가져올 경로
        val myUserInfoRef = FirebaseRef.userInfoRef.child( myUid )

        myUserInfoRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                Log.d( TAG, dataSnapshot.toString() )
                val myUserInfo = dataSnapshot.getValue(UserInfoModel::class.java)
                myGender = myUserInfo?.gender.toString()

                // 나와 다른 성별의 유저를 불러오기
                getUserDataList( myGender )

            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value

            }
        })
    }

    // 유저의 좋아요를 표시하는 부분
    // 나의 uid, 내가 좋아하는 사람의 uid
    private fun Like( myUid : String, otherUid : String ) {
        FirebaseRef.userLikeRef.child( myUid ).child(otherUid).setValue(true)
    }

    private fun disLike( myUid : String, otherUid : String ) {
        FirebaseRef.userLikeRef.child( myUid ).child(otherUid).removeValue()
    }

    // 내가 좋아요한 사람 -> key
    // 내가 좋아요한 사람이 좋아하는 사람들 -> value [...]
    private fun getOtherUserLikeList( otherUid: String ) {
        // Read from the database
        FirebaseRef.userLikeRef.child(otherUid).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Log.e( TAG, dataSnapshot.toString() )   // { key = DBQGJbpsZ5YEXrmiSN5Xb38PVOe2, value = null }

                dataSnapshot.key.toString()
                // key 가 value 를 좋아하는 구성.

                for( dataModel in dataSnapshot.children ) {
                    Log.e( TAG, dataModel.toString() )
                    // { key = eS2syMwP92f0fMG67VdwrCP4s4F2, value = false }
                    // dataModel 의 key 값이 그 사람이 좋아하는 사람의 uid가 된다.
                    val otherLikeUser = dataModel.key.toString()

                    val myUid = FirebaseAuthUtils.getUid()

                    if( otherLikeUser == myUid ) {
                        Toast.makeText(this@MainActivity, "매칭 완료", Toast.LENGTH_SHORT ).show()
                        createNotificationChannel()
                        sendNotification()
                    }
                }

            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value

            }
        })
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "name"
            val descriptionText = "description"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("Test_Channel", name, importance).apply {
                description = descriptionText
            }
            // Register the channel with` the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun sendNotification() {
        var builder = NotificationCompat.Builder(this, "Test_Channel")
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setContentTitle("매칭완료")
            .setContentText("매칭이 완료되었습니다. 저 사람도 나를 좋아해요 !")
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText("매칭이 완료되었습니다. 저 사람도 나를 좋아해요 !"))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        with(NotificationManagerCompat.from(this)) {
            // notificationId is a unique int for each notification that you must define
            notify(123, builder.build())
        }

    }


}