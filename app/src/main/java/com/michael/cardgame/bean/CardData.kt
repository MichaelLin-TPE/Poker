package com.michael.cardgame.bean

import android.view.View
import java.io.Serializable

data class CardData(
    val cardNumber: String,
    val cardImage: Int,
    val cardValue: Int,
    val cardType: Int,
    val centerImage: Int,
    var targetX: Float,
    var targetY: Float,
    @Transient var cardView: View?,
    var sid:Int,
    var isSelected:Boolean = false
) : Serializable
