package com.example.mytinder.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.example.mytinder.MainActivity
import com.example.mytinder.R
import com.example.mytinder.databinding.ActivityIntroBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class IntroActivity : AppCompatActivity() {
    private lateinit var binding: ActivityIntroBinding
    private lateinit var auth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_intro )

        auth = Firebase.auth

        if( auth.currentUser?.uid == null ) {
            Toast.makeText(this, "로그인 후 이용해주세요.", Toast.LENGTH_SHORT ).show()
        }

        // 로그인버튼
        binding.btnLogin.setOnClickListener {
            val intent = Intent( this, LoginActivity::class.java )
            startActivity(intent)
        }

        // 회원가입 버튼
        binding.btnJoin.setOnClickListener {
            val intent = Intent( this, JoinActivity::class.java )
            startActivity(intent)
        }

    }
}