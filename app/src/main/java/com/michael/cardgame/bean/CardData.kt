package com.michael.cardgame.bean

import android.view.View

data class CardData(
    val cardNumber: String,
    val cardImage: Int,
    val cardValue: Int,
    val cardType: Int,
    val centerImage: Int,
    var targetX: Float,
    var targetY: Float,
    var cardView: View?
)
