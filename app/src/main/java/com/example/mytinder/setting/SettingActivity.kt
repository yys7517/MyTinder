package com.example.mytinder.setting

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.example.mytinder.R
import com.example.mytinder.message.LikeMeListActivity
import com.example.mytinder.message.MatchedListActivity
import com.example.mytinder.message.MyLikeListActivity

class SettingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)

        val btnMyPage : Button = findViewById(R.id.btnMyPage)
        val btnMyLike : Button = findViewById(R.id.btnMyLike)
        val btnLikeMe : Button = findViewById(R.id.btnLikeMe)
        val btnMatched : Button = findViewById(R.id.btnMatched)

        btnMyPage.setOnClickListener {
            val intent = Intent( this, MyPageActivity::class.java )
            startActivity(intent)

        }

        btnMyLike.setOnClickListener {
            val intent = Intent( this, MyLikeListActivity::class.java )
            startActivity(intent)

        }

        btnLikeMe.setOnClickListener {
            val intent = Intent( this, LikeMeListActivity::class.java )
            startActivity(intent)
        }

        btnMatched.setOnClickListener {
            val intent = Intent( this, MatchedListActivity::class.java )
            startActivity(intent)
        }
    }
}