package com.example.mytinder.slider

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mytinder.R
import com.example.mytinder.auth.UserInfoModel
import com.example.mytinder.utils.FirebaseRef
import com.google.android.gms.tasks.OnCompleteListener

class CardStackAdapter( val context : Context, val items : List<UserInfoModel> ) : RecyclerView.Adapter< CardStackAdapter.ViewHolder > () {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardStackAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate( R.layout.item_card, parent, false )
        return ViewHolder( view )
    }

    override fun onBindViewHolder(holder: CardStackAdapter.ViewHolder, position: Int) {
        holder.binding( items[position] )
    }

    override fun getItemCount(): Int {
        return items.size
    }

    inner class ViewHolder( itemView : View) : RecyclerView.ViewHolder(itemView) {

        val profileImageArea : ImageView = itemView.findViewById( R.id.profileImageArea )
        val nicknameArea : TextView = itemView.findViewById( R.id.itemNickname )
        val ageArea : TextView = itemView.findViewById( R.id.itemAge )
        val cityArea : TextView = itemView.findViewById( R.id.itemCity )

        fun binding( user : UserInfoModel ) {

            val profileImageRef = FirebaseRef.storageRef.child("${user.uid}.png")
            profileImageRef.downloadUrl.addOnCompleteListener(OnCompleteListener { task ->
                if( task.isSuccessful ) {
                    Glide.with(context)
                        .load(task.result)
                        .into(profileImageArea)
                }
            })
            nicknameArea.text = user.nickname
            ageArea.text = user.age
            cityArea.text = user.city


        }
    }
}