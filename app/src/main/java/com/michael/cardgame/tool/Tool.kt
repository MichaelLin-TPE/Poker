package com.michael.cardgame.tool

import android.app.Activity
import android.content.Context
import android.graphics.Rect
import android.os.Build
import android.util.DisplayMetrics
import android.view.WindowInsets
import android.view.WindowMetrics
import android.widget.Toast
import com.michael.cardgame.R
import com.michael.cardgame.bean.CardData
import com.michael.cardgame.constants.Constants.FOUR_OF_KIND
import com.michael.cardgame.constants.Constants.FULL_HOUSE
import com.michael.cardgame.constants.Constants.POKER_10
import com.michael.cardgame.constants.Constants.POKER_11
import com.michael.cardgame.constants.Constants.POKER_12
import com.michael.cardgame.constants.Constants.POKER_13
import com.michael.cardgame.constants.Constants.POKER_2
import com.michael.cardgame.constants.Constants.POKER_3
import com.michael.cardgame.constants.Constants.POKER_4
import com.michael.cardgame.constants.Constants.POKER_5
import com.michael.cardgame.constants.Constants.POKER_6
import com.michael.cardgame.constants.Constants.POKER_7
import com.michael.cardgame.constants.Constants.POKER_8
import com.michael.cardgame.constants.Constants.POKER_9
import com.michael.cardgame.constants.Constants.POKER_A
import com.michael.cardgame.constants.Constants.POKER_CLUBS
import com.michael.cardgame.constants.Constants.POKER_DIAMOND
import com.michael.cardgame.constants.Constants.POKER_HEART
import com.michael.cardgame.constants.Constants.POKER_SPADES
import com.michael.cardgame.constants.Constants.STRAIGHT
import com.michael.cardgame.constants.Constants.STRAIGHT_FLUSH
import com.michael.cardgame.constants.Constants.TWO_PAIR
import com.weather.sunny.application.MyApplication
import java.text.DecimalFormat
import java.util.UUID

object Tool {

    fun Int.convertDp(): Int {
        val scale = getContext().resources.displayMetrics.density
        return (this * scale + 0.5f).toInt()
    }
    
    fun getCardWidth():Int{
        return 40.convertDp()
    }
    fun getCardHeight():Int{
        return 75.convertDp()
    }

    fun getContext(): Context = MyApplication.instance?.applicationContext!!

    fun formatThousand(value: Int): String {
        val decimalFormat = DecimalFormat("#,###")
        return decimalFormat.format(value)
    }

    fun getNameArray() : MutableList<String>{
        val dataList = mutableListOf<String>()
        dataList.add("飛飛")
        dataList.add("賭神")
        dataList.add("小刀")
        dataList.add("無花果")
        dataList.add("涼糖")
        dataList.add("五月花")
        dataList.add("赤司")
        dataList.add("火神")
        dataList.add("黃賴")
        dataList.add("影子")
        return dataList
    }

    fun getAllCardList(): MutableList<CardData> {
        val list = mutableListOf<CardData>()
        list.add(CardData("A", R.drawable.ic_clubs, POKER_A, POKER_CLUBS,R.drawable.ic_clubs,0f,0f,null,0))
        list.add(CardData("A", R.drawable.ic_diamond, POKER_A, POKER_DIAMOND,R.drawable.ic_diamond,0f,0f,null,0))
        list.add(CardData("A", R.drawable.ic_heart, POKER_A, POKER_HEART,R.drawable.ic_heart,0f,0f,null,0))
        list.add(CardData("A", R.drawable.ic_spades, POKER_A, POKER_SPADES,R.drawable.ic_spades,0f,0f,null,0))

        list.add(CardData("2", R.drawable.ic_clubs, POKER_2, POKER_CLUBS,R.drawable.ic_clubs,0f,0f,null,0))
        list.add(CardData("2", R.drawable.ic_diamond, POKER_2, POKER_DIAMOND,R.drawable.ic_diamond,0f,0f,null,0))
        list.add(CardData("2", R.drawable.ic_heart, POKER_2, POKER_HEART,R.drawable.ic_heart,0f,0f,null,0))
        list.add(CardData("2", R.drawable.ic_spades, POKER_2, POKER_SPADES,R.drawable.ic_spades,0f,0f,null,0))

        list.add(CardData("3", R.drawable.ic_clubs, POKER_3, POKER_CLUBS,R.drawable.ic_clubs,0f,0f,null,0))
        list.add(CardData("3", R.drawable.ic_diamond, POKER_3, POKER_DIAMOND,R.drawable.ic_diamond,0f,0f,null,0))
        list.add(CardData("3", R.drawable.ic_heart, POKER_3, POKER_HEART,R.drawable.ic_heart,0f,0f,null,0))
        list.add(CardData("3", R.drawable.ic_spades, POKER_3, POKER_SPADES,R.drawable.ic_spades,0f,0f,null,0))

        list.add(CardData("4", R.drawable.ic_clubs, POKER_4, POKER_CLUBS,R.drawable.ic_clubs,0f,0f,null,0))
        list.add(CardData("4", R.drawable.ic_diamond, POKER_4, POKER_DIAMOND,R.drawable.ic_diamond,0f,0f,null,0))
        list.add(CardData("4", R.drawable.ic_heart, POKER_4, POKER_HEART,R.drawable.ic_heart,0f,0f,null,0))
        list.add(CardData("4", R.drawable.ic_spades, POKER_4, POKER_SPADES,R.drawable.ic_spades,0f,0f,null,0))

        list.add(CardData("5", R.drawable.ic_clubs, POKER_5, POKER_CLUBS,R.drawable.ic_clubs,0f,0f,null,0))
        list.add(CardData("5", R.drawable.ic_diamond, POKER_5, POKER_DIAMOND,R.drawable.ic_diamond,0f,0f,null,0))
        list.add(CardData("5", R.drawable.ic_heart, POKER_5, POKER_HEART,R.drawable.ic_heart,0f,0f,null,0))
        list.add(CardData("5", R.drawable.ic_spades, POKER_5, POKER_SPADES,R.drawable.ic_spades,0f,0f,null,0))

        list.add(CardData("6", R.drawable.ic_clubs, POKER_6, POKER_CLUBS,R.drawable.ic_clubs,0f,0f,null,0))
        list.add(CardData("6", R.drawable.ic_diamond, POKER_6, POKER_DIAMOND,R.drawable.ic_diamond,0f,0f,null,0))
        list.add(CardData("6", R.drawable.ic_heart, POKER_6, POKER_HEART,R.drawable.ic_heart,0f,0f,null,0))
        list.add(CardData("6", R.drawable.ic_spades, POKER_6, POKER_SPADES,R.drawable.ic_spades,0f,0f,null,0))

        list.add(CardData("7", R.drawable.ic_clubs, POKER_7, POKER_CLUBS,R.drawable.ic_clubs,0f,0f,null,0))
        list.add(CardData("7", R.drawable.ic_diamond, POKER_7, POKER_DIAMOND,R.drawable.ic_diamond,0f,0f,null,0))
        list.add(CardData("7", R.drawable.ic_heart, POKER_7, POKER_HEART,R.drawable.ic_heart,0f,0f,null,0))
        list.add(CardData("7", R.drawable.ic_spades, POKER_7, POKER_SPADES,R.drawable.ic_spades,0f,0f,null,0))

        list.add(CardData("8", R.drawable.ic_clubs, POKER_8, POKER_CLUBS,R.drawable.ic_clubs,0f,0f,null,0))
        list.add(CardData("8", R.drawable.ic_diamond, POKER_8, POKER_DIAMOND,R.drawable.ic_diamond,0f,0f,null,0))
        list.add(CardData("8", R.drawable.ic_heart, POKER_8, POKER_HEART,R.drawable.ic_heart,0f,0f,null,0))
        list.add(CardData("8", R.drawable.ic_spades, POKER_8, POKER_SPADES,R.drawable.ic_spades,0f,0f,null,0))

        list.add(CardData("9", R.drawable.ic_clubs, POKER_9, POKER_CLUBS,R.drawable.ic_clubs,0f,0f,null,0))
        list.add(CardData("9", R.drawable.ic_diamond, POKER_9, POKER_DIAMOND,R.drawable.ic_diamond,0f,0f,null,0))
        list.add(CardData("9", R.drawable.ic_heart, POKER_9, POKER_HEART,R.drawable.ic_heart,0f,0f,null,0))
        list.add(CardData("9", R.drawable.ic_spades, POKER_9, POKER_SPADES,R.drawable.ic_spades,0f,0f,null,0))

        list.add(CardData("10", R.drawable.ic_clubs, POKER_10, POKER_CLUBS,R.drawable.ic_clubs,0f,0f,null,0))
        list.add(CardData("10", R.drawable.ic_diamond, POKER_10, POKER_DIAMOND,R.drawable.ic_diamond,0f,0f,null,0))
        list.add(CardData("10", R.drawable.ic_heart, POKER_10, POKER_HEART,R.drawable.ic_heart,0f,0f,null,0))
        list.add(CardData("10", R.drawable.ic_spades, POKER_10, POKER_SPADES,R.drawable.ic_spades,0f,0f,null,0))

        list.add(CardData("J", R.drawable.ic_clubs, POKER_11, POKER_CLUBS,R.drawable.ic_bodyguard,0f,0f,null,0))
        list.add(CardData("J", R.drawable.ic_diamond, POKER_11, POKER_DIAMOND,R.drawable.ic_bodyguard,0f,0f,null,0))
        list.add(CardData("J", R.drawable.ic_heart, POKER_11, POKER_HEART,R.drawable.ic_bodyguard,0f,0f,null,0))
        list.add(CardData("J", R.drawable.ic_spades, POKER_11, POKER_SPADES,R.drawable.ic_bodyguard,0f,0f,null,0))

        list.add(CardData("Q", R.drawable.ic_clubs, POKER_12, POKER_CLUBS,R.drawable.ic_queen,0f,0f,null,0))
        list.add(CardData("Q", R.drawable.ic_diamond, POKER_12, POKER_DIAMOND,R.drawable.ic_queen,0f,0f,null,0))
        list.add(CardData("Q", R.drawable.ic_heart, POKER_12, POKER_HEART,R.drawable.ic_queen,0f,0f,null,0))
        list.add(CardData("Q", R.drawable.ic_spades, POKER_12, POKER_SPADES,R.drawable.ic_queen,0f,0f,null,0))

        list.add(CardData("K", R.drawable.ic_clubs, POKER_13, POKER_CLUBS,R.drawable.ic_king,0f,0f,null,0))
        list.add(CardData("K", R.drawable.ic_diamond, POKER_13, POKER_DIAMOND,R.drawable.ic_king,0f,0f,null,0))
        list.add(CardData("K", R.drawable.ic_heart, POKER_13, POKER_HEART,R.drawable.ic_king,0f,0f,null,0))
        list.add(CardData("K", R.drawable.ic_spades, POKER_13, POKER_SPADES,R.drawable.ic_king,0f,0f,null,0))

        return list
    }

    fun showToast(msg: String) {
        Toast.makeText(getContext(),msg,Toast.LENGTH_SHORT).show()
    }

    fun getScreenWidth(activity:Activity) : Int{
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val windowMetrics: WindowMetrics = activity.windowManager.currentWindowMetrics
            val bounds: Rect = windowMetrics.bounds
            bounds.width()
        } else {
            val displayMetrics = DisplayMetrics()
            activity.windowManager.defaultDisplay.getMetrics(displayMetrics)
            displayMetrics.widthPixels
        }
    }

    fun getScreenHeight(activity:Activity) : Int{
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val windowMetrics: WindowMetrics = activity.windowManager.currentWindowMetrics
            val insets = windowMetrics.windowInsets
                .getInsetsIgnoringVisibility(WindowInsets.Type.systemBars())
            val insetsTotal = insets.top + insets.bottom
            val bounds: Rect = windowMetrics.bounds
            bounds.height() - insetsTotal
        } else {
            val displayMetrics = DisplayMetrics()
            activity.windowManager.defaultDisplay.getMetrics(displayMetrics)
            displayMetrics.heightPixels
        }
    }

    fun getFlavor(cardType: Int): String {
        return when(cardType){
            POKER_CLUBS-> "梅花"
            POKER_DIAMOND -> "方塊"
            POKER_HEART -> "愛心"
            POKER_SPADES -> "黑桃"
            else -> ""
        }
    }

    fun getCardType(currentCardType: Int): String {
        return when(currentCardType){
            STRAIGHT_FLUSH-> "同花順"
            FOUR_OF_KIND -> "鐵支"
            FULL_HOUSE -> "葫蘆"
            STRAIGHT -> "順子"
            TWO_PAIR -> "兔胚"
            else -> "單張"
        }
    }

    fun getRandomEmail(): String {
        val randId = UUID.randomUUID().toString()
        return "$randId@customer.com"
    }

    fun getRandomPassword(): String {
        return UUID.randomUUID().toString()
    }


}