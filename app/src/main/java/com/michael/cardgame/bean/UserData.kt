package com.michael.cardgame.bean

data class UserData(
    val email:String,
    val name:String,
    val photoId:Int,
    var isRoomBoss: Boolean = false,
    var isReady : Boolean = false
)
