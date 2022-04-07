package com.example.mytinder.message

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mytinder.R
import com.example.mytinder.auth.UserInfoModel
import com.example.mytinder.message.fcm.NotiModel
import com.example.mytinder.message.fcm.PushNotification
import com.example.mytinder.message.fcm.RetrofitInstance
import com.example.mytinder.utils.FirebaseRef
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MatchedListRVAdapter(
    val context: Context,
    val items : ArrayList<UserInfoModel> ) : RecyclerView.Adapter< MatchedListRVAdapter.ViewHolder > () {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MatchedListRVAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.rv_user_list, parent,false )

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: MatchedListRVAdapter.ViewHolder, position: Int) {
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
            // 메시지 보내기.
            itemView.setOnClickListener {
                val notiModel = NotiModel("a","b")
                val pushModel = PushNotification( notiModel, user?.token.toString() )
                Log.e("클릭 유저 token", user?.token.toString() )

                testPush( pushModel )
            }
        }

        private fun testPush( notification : PushNotification ) = CoroutineScope( Dispatchers.IO).launch {

            RetrofitInstance.api.postNotification( notification )
        }

    }
}