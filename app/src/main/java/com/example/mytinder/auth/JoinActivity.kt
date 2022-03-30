package com.example.mytinder.auth

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.example.mytinder.R
import com.example.mytinder.databinding.ActivityJoinBinding

class JoinActivity : AppCompatActivity() {
    private lateinit var binding : ActivityJoinBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView( this, R.layout.activity_join )

        // 회원가입 완료 버튼을 눌렀을 때
        binding.btnJoin.setOnClickListener {

            val email = binding.emaliArea.text.toString()
            val password = binding.passArea.text.toString()
            val passwordCheck = binding.passCheckArea.text.toString()


        }
    }
}