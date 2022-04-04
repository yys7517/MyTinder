package com.example.mytinder.utils

import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class FirebaseRef {
    companion object {

        val database = Firebase.database
        val userInfoRef = database.getReference("userInfo")

        val storage = Firebase.storage
        val storageRef = storage.reference
    }
}