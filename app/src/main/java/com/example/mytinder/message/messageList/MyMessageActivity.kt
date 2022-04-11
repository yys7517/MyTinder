package com.example.mytinder.message.messageList

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mytinder.R
import com.example.mytinder.message.MessageModel
import com.example.mytinder.message.matchedList.MatchedListRVAdapter
import com.example.mytinder.utils.FirebaseAuthUtils
import com.example.mytinder.utils.FirebaseRef
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class MyMessageActivity : AppCompatActivity() {

    private lateinit var items : ArrayList<MessageModel>
    private lateinit var messageRVAdapter: MessageListRVAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_message)

        val messageRV = findViewById< RecyclerView >( R.id.messageListRV )

        items = ArrayList<MessageModel>()
        messageRVAdapter = MessageListRVAdapter( this, items )

        messageRV.adapter = messageRVAdapter
        messageRV.layoutManager = LinearLayoutManager( this )

        getMyMsg()

    }

    // 내가 받은 메세지 가져오기
    private fun getMyMsg() {

        FirebaseRef.userMsgRef.child( FirebaseAuthUtils.getMyUid() ).addValueEventListener( object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                items.clear()

                for( dataModel in snapshot.children ) {

                    // msgModel( 닉네임, 메세지 내용 )
                    val msgModel =  dataModel.getValue( MessageModel::class.java )

                    items.add( msgModel!! )
                }
                
                // 메세지는 받은 반대 순으로 가져와야 한다.
                // 최신 메세지가 제일 위에 오도록
                items.reverse()

                messageRVAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

    }
}