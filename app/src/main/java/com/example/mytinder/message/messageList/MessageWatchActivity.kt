package com.example.mytinder.message.messageList

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.example.mytinder.R
import com.example.mytinder.databinding.ActivityMessageWatchBinding
import com.example.mytinder.utils.FirebaseRef

// 메세지 보기 Activity

class MessageWatchActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMessageWatchBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_message_watch)

        // 메세지 보낸사람 uid
        val senderUid = intent.getStringExtra("uid")

        // 프로필 이미지 불러오기
        FirebaseRef.storageRef.child("${senderUid}.png").downloadUrl.addOnSuccessListener {
            Glide.with( this )
                .load( it )
                .into( binding.profileImageArea )
        }

        // 닉네임과 메세지 불러오기
        binding.nicknameArea.text = intent.getStringExtra("nickname")
        binding.messageArea.text = intent.getStringExtra("message")


    }


}