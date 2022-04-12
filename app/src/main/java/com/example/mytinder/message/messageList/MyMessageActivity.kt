package com.example.mytinder.message.messageList

import android.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mytinder.R
import com.example.mytinder.message.MessageModel
import com.example.mytinder.utils.FirebaseAuthUtils
import com.example.mytinder.utils.FirebaseRef
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class MyMessageActivity : AppCompatActivity() {

    private lateinit var messages : ArrayList<MessageModel>
    private lateinit var messageKeys : ArrayList<String>
    private lateinit var messageRVAdapter: MessageListRVAdapter

    private var messageCount : Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_message)

        val messageRV = findViewById< RecyclerView >( R.id.messageListRV )

        messages = ArrayList()
        messageKeys = ArrayList()

        messageRVAdapter = MessageListRVAdapter( this, messages, messageKeys )

        messageRV.adapter = messageRVAdapter
        messageRV.layoutManager = LinearLayoutManager( this )

        getMyMsg()

    }

    // 내가 받은 메세지 가져오기
    fun getMyMsg() {

        val mDialogView = LayoutInflater.from( this ).inflate( R.layout.progressbar_dialog , null )
        val mBuilder = AlertDialog.Builder( this )
            .setView( mDialogView )

        val mAlertDialog = mBuilder.show()

        mAlertDialog.findViewById<TextView>( R.id.txtProgress ).text = "메세지 불러오는 중.."

        FirebaseRef.userMsgRef.child( FirebaseAuthUtils.getMyUid() ).addValueEventListener( object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                messageCount = 0
                messages.clear()
                messageKeys.clear()

                for( dataModel in snapshot.children ) {

                    messageCount++

                    if( messageCount == 0 ) {
                        findViewById<TextView>(R.id.textview).text = "받은 메세지가 없습니다."
                    }

                    if( messageCount == 1 ) {
                        findViewById<TextView>(R.id.textview).text = "길게 눌러서 메세지를 삭제할 수 있습니다 !"
                    }


                    // msgModel( 닉네임, 메세지 내용 )
                    val msgModel =  dataModel.getValue( MessageModel::class.java )

                    messages.add( msgModel!! )
                    messageKeys.add( dataModel.key.toString() )
                }

                // 메세지는 받은 반대 순으로 가져와야 한다.
                // 최신 메세지가 제일 위에 오도록
                messages.reverse()
                messageKeys.reverse()

                messageRVAdapter.notifyDataSetChanged()


                Handler().postDelayed( {
                    mAlertDialog.dismiss()
                }, 2000 )


            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

    }


}