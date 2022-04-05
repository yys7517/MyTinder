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


// 매칭된 사람 - 리스트 보여주기
class MatchedListActivity : AppCompatActivity() {

    private val TAG = MatchedListActivity::class.java.simpleName

    private val myMatchedList = ArrayList<UserInfoModel>()
    private lateinit var userListRVAdapter : UserListRVAdapter

    private val myUid = FirebaseAuthUtils.getUid()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.user_list)

        val txtTitle : TextView = findViewById( R.id.txtTitle )
        val userListRV : RecyclerView = findViewById( R.id.userListRV )

        txtTitle.text = "매칭되었어요!"

        userListRVAdapter = UserListRVAdapter( baseContext, myMatchedList)
        userListRV.adapter = userListRVAdapter
        userListRV.layoutManager = LinearLayoutManager( this )

        // 내가 좋아하는 사람들을 리스트에 담는다.
       getMatchedList( myUid )

    }



    private fun getMatchedList( uid : String ) {
        // 내가 좋아요한 유저(otherUid)가
        // 나(myUid)를 좋아할 때

        // 1. 내가 좋아하는 유저들의 uid를 먼저 구하자.
        FirebaseRef.userLikeRef.child(myUid).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                myMatchedList.clear()

                for( dataModel in dataSnapshot.children ) {
                    Log.e( "내가 좋아요한 UID", dataModel.key.toString() )
                    val myLikeUserUID = dataModel.key.toString()    // 내가 좋아요한 UID

                    // 2. 내가 좋아하는 유저의 uid가 나의 uid를 좋아할 때 == > 매칭 !!
                    isUserLikeMe( myLikeUserUID )

                }

            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value

            }
        })
    }

    private fun isUserLikeMe( uid : String )  {

        FirebaseRef.userLikeRef.child( uid ).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                // Log.d( "나를 좋아하는가", dataSnapshot.toString() )

                for( dataModel in dataSnapshot.children ) {
                    Log.e("나를 좋아하는가", dataModel.toString() )

                    val likeUserUid = dataModel.key.toString()      // 내가 좋아하는 유저가 -> 좋아하는 유저

                    // 내가 좋아하는 유저가 나를 좋아할 때
                    if( likeUserUid == myUid  ){
                        getUserDataList( uid )
                    }
                }

            }

            override fun onCancelled(error: DatabaseError) {
                Log.e( TAG, error.toString() )
            }
        })
    }


    // 매칭된 사람의 uid를 매개변수로 넘겨줘서 그 유저들을 리스트에 담는다.
    private fun getUserDataList( uid : String ) {

        FirebaseRef.userInfoRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                Log.d( TAG, dataSnapshot.toString() )

                for( dataModel in dataSnapshot.children ) {

                    val user = dataModel.getValue(UserInfoModel::class.java)

                    // 전체 유저 중, 내가 찾고 있는 유저의 uid와 같을 때만 가져와 담는다.
                    if( uid == user?.uid ) {
                        myMatchedList.add( user!! )
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