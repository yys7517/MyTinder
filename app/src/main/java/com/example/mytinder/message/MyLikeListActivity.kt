package com.example.mytinder.message

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mytinder.R
import com.example.mytinder.auth.UserInfoModel
import com.example.mytinder.utils.FirebaseAuthUtils
import com.example.mytinder.utils.FirebaseRef
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

// 내가 좋아요
// 나를 좋아해요 -> 나를 좋아하는 사람이 있는지 ?
// 매칭된 사람 -> 내가 좋아요한 사람이 나를 좋아하는지 ?


// 내가 좋아요 - 리스트 보여주기
class MyLikeListActivity : AppCompatActivity() {

    private val TAG = MyLikeListActivity::class.java.simpleName

    private val myLikeUserList = ArrayList<UserInfoModel>()
    private lateinit var userListRVAdapter : UserListRVAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.user_list)

        val myUid = FirebaseAuthUtils.getUid()

        val txtTitle : TextView = findViewById( R.id.txtTitle )
        val userListRV : RecyclerView = findViewById( R.id.userListRV )

        txtTitle.text = "내가 좋아해요"

        userListRVAdapter = UserListRVAdapter( baseContext, myLikeUserList)
        userListRV.adapter = userListRVAdapter
        userListRV.layoutManager = LinearLayoutManager( this )

        // 내가 좋아하는 사람들을 리스트에 담는다.
       getMyLikeList( myUid )

    }



    private fun getMyLikeList( myUid : String ) {
        // Read from the database
        FirebaseRef.userLikeRef.child(myUid).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                myLikeUserList.clear()

                for( dataModel in dataSnapshot.children ) {
                    Log.e( "내가 좋아요한 UID", dataModel.key.toString() )

                    val myLikeUserUID = dataModel.key.toString()    // 내가 좋아요한 UID
                    getUserDataList( myLikeUserUID )
                }

            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value

            }
        })
    }
    private fun getUserDataList( uid : String ) {

        // Read from the database
        FirebaseRef.userInfoRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                Log.d( TAG, dataSnapshot.toString() )

                for( dataModel in dataSnapshot.children ) {

                    val user = dataModel.getValue(UserInfoModel::class.java)

                    // 전체 유저 중, 내가 찾고 있는 유저의 uid와 같을 때만 가져와 담는다.
                    if( uid == user?.uid ) {
                        myLikeUserList.add( user!! )
                    }
                }

                userListRVAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

}