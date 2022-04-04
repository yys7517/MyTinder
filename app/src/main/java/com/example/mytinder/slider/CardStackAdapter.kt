package com.example.mytinder.slider

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mytinder.R
import com.example.mytinder.auth.UserInfoModel

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

            nicknameArea.text = user.nickname
            ageArea.text = user.age
            cityArea.text = user.city


        }
    }
}