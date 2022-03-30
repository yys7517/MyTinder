package com.example.mytinder.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.example.mytinder.R
import com.example.mytinder.databinding.ActivityIntroBinding

class IntroActivity : AppCompatActivity() {
    private lateinit var binding: ActivityIntroBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_intro )

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