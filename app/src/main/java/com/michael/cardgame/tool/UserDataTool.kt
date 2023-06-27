package com.michael.cardgame.tool

import com.michael.cardgame.R

object UserDataTool {

    fun getUserName(): String {
        return CacheTool.getString("user_name", "")
    }

    fun getUserCashAmount(): Int {
        return CacheTool.getInt("user_cash_amount", 150000)
    }

    fun getBot2CashAmount():Int{
        return CacheTool.getInt("bot2_cash_amount",100000)
    }
    fun getBot3CashAmount():Int{
        return CacheTool.getInt("bot3_cash_amount",100000)
    }
    fun getBot4CashAmount():Int{
        return CacheTool.getInt("bot4_cash_amount",100000)
    }

    fun saveBot2CashAmount(amount:Int){
        CacheTool.putInt("bot2_cash_amount",amount)
    }
    fun saveBot3CashAmount(amount:Int){
        CacheTool.putInt("bot3_cash_amount",amount)
    }
    fun saveBot4CashAmount(amount:Int){
        CacheTool.putInt("bot4_cash_amount",amount)
    }

    fun saveUserCashAmount(amount:Int){
        CacheTool.putInt("user_cash_amount",amount)
    }

    fun getUserDiamondCount():Int{
        return CacheTool.getInt("user_diamond_count",100000)
    }

    fun getUserPhoto():Int{
        return CacheTool.getInt("user_photo_id",0)
    }
    fun getBot2Photo():Int{
        return R.drawable.ic_boy2
    }
    fun getBot3Photo():Int{
        return R.drawable.ic_boy1
    }
    fun getBot4Photo():Int{
        return R.drawable.ic_girl
    }

    fun saveUserData(name:String,photoId:Int){
        CacheTool.putString("user_name",name)
        CacheTool.putInt("user_photo_id",photoId)
    }

    fun saveUserToken(token:String){
        CacheTool.putString("token",token)
    }
    fun getUserToken():String = CacheTool.getString("token","")



}