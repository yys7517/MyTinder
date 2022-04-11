package com.example.mytinder.message.matchedList

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mytinder.R
import com.example.mytinder.auth.UserInfoModel
import com.example.mytinder.databinding.MatchedUserListBinding
import com.example.mytinder.utils.FirebaseAuthUtils
import com.example.mytinder.utils.FirebaseRef
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener


// 매칭된 사람 - 리스트 보여주기
class MatchedListActivity : AppCompatActivity() {

    private lateinit var binding : MatchedUserListBinding

    private val TAG = MatchedListActivity::class.java.simpleName

    private val myMatchedList = ArrayList<UserInfoModel>()
    private lateinit var matchedListRVAdapter : MatchedListRVAdapter

    private val myUid = FirebaseAuthUtils.getMyUid()

    private var matchedCount : Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView( this, R.layout.matched_user_list )


        val txtTitle : TextView = binding.txtTitle
        val userListRV: RecyclerView = binding.userListRV

        txtTitle.text = "매칭되었어요!"

        matchedListRVAdapter = MatchedListRVAdapter( this , myMatchedList)
        userListRV.adapter = matchedListRVAdapter
        userListRV.layoutManager = LinearLayoutManager(this)

        // 내가 좋아하는 사람들을 리스트에 담는다.
        getMatchedList(myUid)

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
            // 어떤 특정 uid 가 좋아하는 uid 들을 구할 수 있다.
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                // Log.d( "나를 좋아하는가", dataSnapshot.toString() )

                for( dataModel in dataSnapshot.children ) {
                    Log.e("나를 좋아하는가", dataModel.toString() )

                    val likeUserUid = dataModel.key.toString()      // uid 가 좋아하는 uid

                    // uid 가 좋아하는 유저가 나일 때 => 매칭된 것.
                    // 왜냐하면, 내가 uid를 좋아할 때 실행되는 메서드.
                    if( likeUserUid == myUid  ){

                        // 매칭된 사람인 uid 를 매칭 리스트에 추가한다.
                        addToMatchedList( uid )
                        matchedCount++

                        if( matchedCount == 1 ) {
                            Toast.makeText( baseContext, "매칭을 축하드려요 !", Toast.LENGTH_SHORT).show()
                            binding.textview.visibility = View.VISIBLE
                        }

                    }
                }

            }

            override fun onCancelled(error: DatabaseError) {
                Log.e( TAG, error.toString() )
            }
        })
    }



    // 매칭 리스트에 uid 를 담는다.
    private fun addToMatchedList( matchedUid : String ) {

        FirebaseRef.userInfoRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                Log.d( TAG, dataSnapshot.toString() )

                for( dataModel in dataSnapshot.children ) {

                    val user = dataModel.getValue(UserInfoModel::class.java)

                    // 전체 유저 중, 내가 찾고 있는 유저의 uid와 같을 때만 가져와 담는다.
                    if( matchedUid == user?.uid ) {
                        myMatchedList.add( user!! )

                    }
                }

                matchedListRVAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value

            }
        })

    }


}