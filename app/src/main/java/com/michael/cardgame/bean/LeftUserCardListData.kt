package com.michael.cardgame.bean

import java.io.Serializable

data class LeftUserCardListData(
    val cardList : MutableList<CardData>,
    val userNum : Int,
    val userName:String
) : Serializable
