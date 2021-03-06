package com.example.mytinder

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.example.mytinder.auth.IntroActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SplashActivity : AppCompatActivity() {

    private val TAG = SplashActivity::class.java.simpleName

    private lateinit var auth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        auth = Firebase.auth

        if( auth.currentUser?.uid == null ) {
            Handler().postDelayed({
                val intent = Intent( this, IntroActivity::class.java )
                startActivity(intent)
                finish()
            },2000)
        }

        else {
            Handler().postDelayed({
                val intent = Intent( this, MainActivity::class.java )
                startActivity(intent)
                finish()
            },2000)
        }

    }
}