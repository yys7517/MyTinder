package com.example.mytinder

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.databinding.DataBindingUtil
import com.example.mytinder.auth.IntroActivity
import com.example.mytinder.auth.UserInfoModel
import com.example.mytinder.databinding.ActivityMainBinding
import com.example.mytinder.slider.CardStackAdapter
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

    private val TAG = MainActivity::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView( this, R.layout.activity_main )

        auth = Firebase.auth

        val cardStackView : CardStackView = binding.cardStackView
        val settingIcon : ImageView = binding.settingIcon

        settingIcon.setOnClickListener {
            auth.signOut()
            val intent = Intent( this, IntroActivity::class.java )
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity( intent )
        }

        manager = CardStackLayoutManager( baseContext, object : CardStackListener {

            override fun onCardDragging(direction: Direction?, ratio: Float) {

            }

            override fun onCardSwiped(direction: Direction?) {

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

        // 유저 데이터 리스트에 담아서 가져오기.
        getUserDataList()

    }

    // 유저 데이터 카드 뷰에 가져오기
    private fun getUserDataList() {

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

                    userDataList.add( user!! )
                }

                cardStackAdapter.notifyDataSetChanged()

            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value

            }
        })
    }
}