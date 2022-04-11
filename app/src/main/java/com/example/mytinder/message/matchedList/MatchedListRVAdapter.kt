package com.example.mytinder.message.matchedList

import android.app.AlertDialog
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mytinder.R
import com.example.mytinder.auth.UserInfoModel
import com.example.mytinder.message.MessageModel
import com.example.mytinder.message.fcm.NotiModel
import com.example.mytinder.message.fcm.PushNotification
import com.example.mytinder.message.fcm.RetrofitInstance
import com.example.mytinder.utils.FirebaseAuthUtils
import com.example.mytinder.utils.FirebaseRef
import com.example.mytinder.utils.MyInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MatchedListRVAdapter(
    val context: Context,
    val items : ArrayList<UserInfoModel> ) : RecyclerView.Adapter<MatchedListRVAdapter.ViewHolder> () {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.rv_user_list, parent,false )

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItems( items[position] )
    }

    override fun getItemCount(): Int {
       return items.size
    }

    inner class ViewHolder( itemView : View ) : RecyclerView.ViewHolder(itemView) {
        val profileImage : ImageView = itemView.findViewById( R.id.userRVImage )
        val txtNick : TextView = itemView.findViewById( R.id.userRVNick )
        val txtAge : TextView = itemView.findViewById( R.id.userRVAge )
        val txtCity : TextView = itemView.findViewById( R.id.userRVCity )
        val txtGender : TextView = itemView.findViewById( R.id.userRVGender )

        fun bindItems( user : UserInfoModel ) {

            // 이미지 로드
            FirebaseRef.storageRef.child("${user.uid}.png").downloadUrl
                .addOnSuccessListener {
                    // 성공 시
                    Glide.with(context)
                        .load(it)
                        .into(profileImage)
                }
                .addOnFailureListener{
                    // 실패 시
                    Log.e("리사이클러 리스트", "이미지 불러오기 실패")
                }


            txtNick.text = user.nickname
            txtAge.text = "${user.age}세, "
            txtCity.text = "${user.city} 거주"
            txtGender.text ="${user.gender}성"

            // 리스트 클릭 시 - 매칭된 유저
            itemView.setOnClickListener {
//                val notiModel = NotiModel("a","b")
//                val pushModel = PushNotification( notiModel, user?.token.toString() )
//                Log.e("클릭 유저 token", user?.token.toString() )
//
//                testPush( pushModel )
            }

            // 길게 누를 시 - 메시지 보내기
            itemView.setOnLongClickListener {
                // Toast.makeText(context, "long_clicked", Toast.LENGTH_SHORT).show()

                // 메세지 창 띄우기
                MessageDialog( user )

                return@setOnLongClickListener(true)
            }
        }

        private fun testPush( notification : PushNotification ) = CoroutineScope( Dispatchers.IO).launch {

            RetrofitInstance.api.postNotification( notification )
        }

        // user -> 메세지 보내는 대상.
        private fun MessageDialog( user : UserInfoModel ) {

            val mDialogView = LayoutInflater.from( context ).inflate( R.layout.message_dialog , null )
            val mBuilder = AlertDialog.Builder( context )
                .setView( mDialogView )
                .setTitle("메세지 보내기")

            val mAlertDialog = mBuilder.show()

            // 보내기 버튼 클릭 시
            mAlertDialog.findViewById<Button>( R.id.btnSubmit ).setOnClickListener {

                val messageArea = mAlertDialog.findViewById<EditText>( R.id.messageArea )
                val messageText = messageArea.text.toString()    // 메세지 내용

                // message
                    // 받는 사람 uid
                        // 보낸 사람 uid
                        // message 내용

                val uid = user.uid.toString()               // 받는 사람 uid
                val myuid = FirebaseAuthUtils.getMyUid()

                val msgModel = MessageModel( myuid , MyInfo.myNickname , messageText )
//                Log.e("닉네임 가져오기", MyInfo.myNickname )

                // 메세지 전송.
                FirebaseRef.userMsgRef.child( uid )
                    .push()
                    .setValue( msgModel )

                Toast.makeText( context, "메세지를 전송했습니다.", Toast.LENGTH_SHORT).show()
                // 메세지를 보낸 후 다이얼로그 창을 닫는다.
                mAlertDialog.dismiss()

                // 상대에게 PUSH 알림 전송
                val notiModel = NotiModel( MyInfo.myNickname,messageText )
                val pushModel = PushNotification( notiModel, user?.token.toString() )
                Log.e("클릭 유저 token", user?.token.toString() )

                testPush( pushModel )

            }
        }
    }
}