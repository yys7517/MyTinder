package com.example.mytinder.message

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mytinder.R
import com.example.mytinder.auth.UserInfoModel
import com.example.mytinder.utils.FirebaseAuthUtils
import com.example.mytinder.utils.FirebaseRef
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener


// 나를 좋아해요 - 리스트 보여주기
class LikeMeListActivity : AppCompatActivity() {

    private val TAG = LikeMeListActivity::class.java.simpleName

    private val likeMeUserList = ArrayList<UserInfoModel>()
    private lateinit var userListRVAdapter: UserListRVAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.user_list)

        val myUid = FirebaseAuthUtils.getUid()

        val txtTitle: TextView = findViewById(R.id.txtTitle)
        val userListRV: RecyclerView = findViewById(R.id.userListRV)

        txtTitle.text = "나를 좋아해요"

        userListRVAdapter = UserListRVAdapter(baseContext, likeMeUserList)
        userListRV.adapter = userListRVAdapter
        userListRV.layoutManager = LinearLayoutManager(this)

        // 내가 좋아하는 사람들을 리스트에 담는다.
        getLikeMeList(myUid)

    }


    private fun getLikeMeList(myUid: String) {
        // 내 uid를 value로 갖고있는 key를 찾을 수 없을까?

        // 전체 좋아요 목록에서 value 에 내 uid를 갖고 있으면.
        // key 는 나를 좋아하는 사람이 된다.
        FirebaseRef.userLikeRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Log.e( "이게 뭘 찾아주는걸까", dataSnapshot.toString() )

                likeMeUserList.clear()

                for (dataModel in dataSnapshot.children) {
                    // Log.e( "나를 좋아요한 UID", dataModel.key.toString() )
                    // Log.e( "내 uid 찾아보자", dataModel.value.toString())

                    if ( dataModel.value.toString().contains(myUid) ) {
                        val likeMeUserID = dataModel.key.toString()    // 나를 좋아하는 사람의 UID
                        Log.e("나를 좋아하는 사람의 UID", likeMeUserID )
                        getUserDataList( likeMeUserID )     // 나를 좋아하는 사람의 Uid로 유저를 찾아 리스트에 담는다.
                    }
                }

            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value

            }
        })
    }

    // 나를 좋아하는 사람의 Uid로 유저를 찾아 리스트에 담는다.
    private fun getUserDataList(uid: String) {

        // Read from the database
        FirebaseRef.userInfoRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                Log.d(TAG, dataSnapshot.toString())

                for (dataModel in dataSnapshot.children) {

                    val user = dataModel.getValue(UserInfoModel::class.java)

                    // 전체 유저 중, 내가 찾고 있는 유저의 uid와 같을 때만 가져와 담는다.
                    if (uid == user?.uid) {
                        likeMeUserList.add(user!!)
                    }
                }

                userListRVAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value

            }
        })
    }

}