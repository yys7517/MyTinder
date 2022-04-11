package com.example.mytinder.message.messageList

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
import com.example.mytinder.utils.FirebaseRef
import com.example.mytinder.utils.MyInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MessageListRVAdapter(
    val context: Context,
    val items : ArrayList<MessageModel> ) : RecyclerView.Adapter<MessageListRVAdapter.ViewHolder> () {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.rv_message_list, parent,false )

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
        val messageArea : TextView = itemView.findViewById( R.id.RV_messageArea )

        fun bindItems( msgModel : MessageModel ) {

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

            }

            // 길게 누를 시 - 메시지 삭제 창 띄우기 ?
            itemView.setOnLongClickListener {
                // Toast.makeText(context, "long_clicked", Toast.LENGTH_SHORT).show()


                return@setOnLongClickListener(true)
            }
        }


    }
}