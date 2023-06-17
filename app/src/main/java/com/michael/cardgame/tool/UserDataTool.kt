package com.michael.cardgame.tool

object UserDataTool {

    fun getUserName(): String {
        return CacheTool.getString("user_name", "")
    }

    fun getUserCashAmount(): Int {
        return CacheTool.getInt("user_cash_amount", 2000000)
    }

    fun getUserDiamondCount():Int{
        return CacheTool.getInt("user_diamond_count",100000)
    }

    fun getUserPhoto():Int{
        return CacheTool.getInt("user_photo_id",0)
    }

    fun saveUserData(name:String,photoId:Int){
        CacheTool.putString("user_name",name)
        CacheTool.putInt("user_photo_id",photoId)
    }

}