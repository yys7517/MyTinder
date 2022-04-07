package com.example.mytinder.message.fcm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.mytinder.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import retrofit2.http.Body

// 유저의 토큰 정보를 받아옴
// Firebase 서버로 메시지 보내라고 명령
// Firebase 서버에서 앱으로 메시지 보내주고
// 앱에서는 메시지를 받아서
// 알림을 띄워줌

class FirebaseService  : FirebaseMessagingService() {

    private val TAG = "FCM_Service"

    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

//        Log.d(TAG, message.notification?.title.toString())
//        Log.d(TAG, message.notification?.body.toString())
//
//        val title = message.notification?.title.toString()
//        val body = message.notification?.body.toString()

        val title = message.data["title"].toString()
        val body = message.data["content"].toString()

        createNotificationChannel()
        sendNotification( title, body )
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

    private fun sendNotification( title : String, body: String ) {
        var builder = NotificationCompat.Builder(this, "Test_Channel")
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setContentTitle( title )
            .setContentText( body )
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        with(NotificationManagerCompat.from(this)) {
            // notificationId is a unique int for each notification that you must define
            notify(123, builder.build())
        }

    }
}