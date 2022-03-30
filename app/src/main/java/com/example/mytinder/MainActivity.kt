package com.example.mytinder

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import com.example.mytinder.databinding.ActivityMainBinding
import com.example.mytinder.slider.CardStackAdapter
import com.yuyakaido.android.cardstackview.CardStackLayoutManager
import com.yuyakaido.android.cardstackview.CardStackListener
import com.yuyakaido.android.cardstackview.CardStackView
import com.yuyakaido.android.cardstackview.Direction

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var cardStackAdapter: CardStackAdapter
    private lateinit var manager : CardStackLayoutManager

    private val items = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView( this, R.layout.activity_main )

        val cardStackView : CardStackView = binding.cardStackView


        manager = CardStackLayoutManager( baseContext, object : CardStackListener {

            override fun onCardDragging(direction: Direction?, ratio: Float) {

            }

            override fun onCardSwiped(direction: Direction?) {

            }

            override fun onCardRewound() {

            }

            override fun onCardCanceled() {

            }

            override fun onCardAppeared(view: View?, position: Int) {

            }

            override fun onCardDisappeared(view: View?, position: Int) {

            }
        })

        items.add( "a" )
        items.add( "b" )
        items.add( "c" )

        cardStackAdapter = CardStackAdapter( baseContext, items )

        cardStackView.adapter = cardStackAdapter
        cardStackView.layoutManager = manager
    }
}