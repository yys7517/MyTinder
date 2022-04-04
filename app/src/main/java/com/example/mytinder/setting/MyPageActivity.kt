package com.example.mytinder.setting

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.example.mytinder.R
import com.example.mytinder.auth.IntroActivity
import com.example.mytinder.auth.UserInfoModel
import com.example.mytinder.databinding.ActivityMyPageBinding
import com.example.mytinder.utils.FirebaseAuthUtils
import com.example.mytinder.utils.FirebaseRef
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase

class MyPageActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMyPageBinding
    private lateinit var auth : FirebaseAuth

    private val TAG = MyPageActivity::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView( this, R.layout.activity_my_page )

        auth = Firebase.auth

        // 마이페이지 정보 불러오기
        getMyUserData()

        // 로그아웃 버튼
        binding.btnLogout.setOnClickListener {

            auth.signOut()
            val intent = Intent( this, IntroActivity::class.java )
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK

            Toast.makeText(this, "로그아웃 되었습니다.",Toast.LENGTH_SHORT).show()
            startActivity(intent)
        }


    }

    // 내 UID를 통해 내 User 정보를 가져오자.
    private fun getMyUserData() {

        // 가져올 경로
        val myUserInfoRef = FirebaseRef.userInfoRef.child( FirebaseAuthUtils.getUid() )

        // Read from the database
        myUserInfoRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                Log.d( TAG, dataSnapshot.toString() )
                val myUserInfo = dataSnapshot.getValue(UserInfoModel::class.java)

                binding.myUid.text = myUserInfo?.uid.toString()
                binding.myNickname.text = myUserInfo?.nickname.toString()
                binding.myAge.text = myUserInfo?.age.toString()
                binding.myCity.text = myUserInfo?.city.toString()
                binding.myGender.text = myUserInfo?.gender.toString()

                val profileImageRef = FirebaseRef.storageRef.child("${myUserInfo?.uid}.png")

                profileImageRef.downloadUrl.addOnCompleteListener(OnCompleteListener { task ->
                    if( task.isSuccessful ) {
                        Glide.with( baseContext )
                            .load(task.result)
                            .into( binding.myImage )
                    }
                })
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value

            }
        })
    }
}