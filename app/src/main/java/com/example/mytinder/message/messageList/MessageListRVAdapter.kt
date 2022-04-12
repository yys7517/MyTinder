package com.example.mytinder.message.messageList

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
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

class MessageListRVAdapter(
    val context: Context,
    val messages : ArrayList<MessageModel>,
    val messageKeys : ArrayList<String>
    ) : RecyclerView.Adapter<MessageListRVAdapter.ViewHolder> () {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.rv_message_list, parent,false )

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItems( messages[position] , messageKeys[position] )
    }

    override fun getItemCount(): Int {
       return messages.size
    }

    inner class ViewHolder( itemView : View ) : RecyclerView.ViewHolder(itemView) {
        val profileImage : ImageView = itemView.findViewById( R.id.userRVImage )
        val txtNick : TextView = itemView.findViewById( R.id.userRVNick )
        val messageArea : TextView = itemView.findViewById( R.id.RV_messageArea )

        fun bindItems( msgModel : MessageModel , msgKey : String ) {

            // 이미지 로드
            FirebaseRef.storageRef.child("${msgModel.senderUid}.png").downloadUrl
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


            txtNick.text = msgModel.senderNickname
            messageArea.text = msgModel.messageText


            // 리스트 클릭 시 - 메시지 전체 보기
            itemView.setOnClickListener {
                val intent = Intent( itemView.context, MessageWatchActivity::class.java )
                intent.putExtra("uid", msgModel.senderUid )
                intent.putExtra("message",msgModel.messageText)
                intent.putExtra("nickname",msgModel.senderNickname)
                itemView.context.startActivity( intent )
            }

            // 길게 누를 시 - 메시지 삭제 창 띄우기
            itemView.setOnLongClickListener {
                // Toast.makeText(context, "long_clicked", Toast.LENGTH_SHORT).show()

                val mDialogView = LayoutInflater.from( context ).inflate( R.layout.custom_yes_no_dialog , null )
                val mBuilder = AlertDialog.Builder( context )
                    .setView( mDialogView )


                val mAlertDialog = mBuilder.show()

                mAlertDialog.findViewById< TextView >( R.id.txtQuestion ).text = "받은 메세지를 삭제하시겠습니까?"

                mAlertDialog.findViewById<Button>(R.id.btnSubmit).setOnClickListener {

                    // 메세지 삭제
                    FirebaseRef.userMsgRef.child( FirebaseAuthUtils.getMyUid() ).child(msgKey).removeValue()
                        .addOnSuccessListener {
                            Toast.makeText(context, "해당 메세지가 삭제되었습니다.", Toast.LENGTH_SHORT).show()
                            mAlertDialog.dismiss()
                        }
                        .addOnFailureListener {
                            Toast.makeText(context, "해당 메세지 삭제에 실패하였습니다.", Toast.LENGTH_SHORT).show()
                            mAlertDialog.dismiss()
                        }


                }

                mAlertDialog.findViewById<Button>(R.id.btnCancel).setOnClickListener {

                    mAlertDialog.dismiss()
                }


                return@setOnLongClickListener(true)
            }
        }


    }
}