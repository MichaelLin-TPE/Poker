package com.michael.cardgame.bean

data class RoomData(
    val name:String,
    val bettingValue:Int,
    val key:String,
    val userCount:Int,
    val userList:MutableList<UserData>
)
