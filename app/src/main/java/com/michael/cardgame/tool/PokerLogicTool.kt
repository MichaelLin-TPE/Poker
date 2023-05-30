package com.michael.cardgame.tool

import android.util.Log
import com.google.gson.Gson
import com.michael.cardgame.bean.CardData

object PokerLogicTool {

    /**
     * 尋找同花順
     */
    fun searchForStraightFlush(cardList:MutableList<CardData>):MutableList<CardData>{
        val straightFlushList = mutableListOf<CardData>()
        for (card in cardList){
            if (straightFlushList.isEmpty()){
                straightFlushList.add(card)
            }
            val straightData = straightFlushList[straightFlushList.size - 1]
            var isAddData = false
            for (card2 in cardList){
                if (straightData.cardValue + 1 == card2.cardValue && straightData.cardType == card2.cardType){
                    straightFlushList.add(card2)
                    isAddData = true
                }
            }
            if (!isAddData){
                straightFlushList.clear()
            }
            if (straightFlushList.size == 5){
                break
            }
        }
        return straightFlushList
    }

    /**
     * 尋找鐵支
     */
    fun searchForFourOfKind(cardList: MutableList<CardData>):MutableList<CardData>{
        val fourOfKindList = mutableListOf<CardData>()
        val numberExistList = mutableListOf<Int>()
        for (card in cardList){
            val number = card.cardValue
            val type = card.cardType
            val numberList = mutableListOf<CardData>()
            for (card2 in cardList){
                if (numberExistList.isEmpty()){
                    if (number == card2.cardValue && type != card2.cardValue){
                        numberList.add(card2)
                    }
                }
                if (numberExistList.isNotEmpty()){
                    var isFoundSameNumber = false
                    for (existNum in numberExistList){
                        if (number == existNum){
                            isFoundSameNumber = true
                        }
                    }
                    if (number == card2.cardValue && type != card2.cardValue && !isFoundSameNumber){
                        numberList.add(card2)
                    }
                }
            }
            if (numberList.size == 4){
                numberExistList.add(numberList[0].cardValue)
                fourOfKindList.addAll(numberList)
            }
        }
        return fourOfKindList
    }

    /**
     * 尋找葫蘆
     */
    fun searchForFullHouse(cardList: MutableList<CardData>) : MutableList<CardData>{
        val copyList = cardList.toMutableList()
        val fullHouseList = mutableListOf<CardData>()
        val threePairList = searchForSpecificCard(copyList,3)
        val twoPairList = searchForSpecificCard(copyList,2)
        for (index in 0 until threePairList.size){
            if (index >= twoPairList.size){
                continue
            }
            fullHouseList.addAll(twoPairList[index])
            fullHouseList.addAll(threePairList[index])
        }
        return fullHouseList
    }

    fun searchTwoPair(cardList: MutableList<CardData>) : MutableList<CardData>{
        val copyList = cardList.toMutableList()
        val twoPairList = searchForSpecificCard(copyList,2)
        val twoPair = mutableListOf<CardData>()
        for (index in 0 until twoPairList.size){
            twoPair.addAll(twoPairList[index])
        }
        return twoPair
    }


    private fun searchForSpecificCard(cardList: MutableList<CardData>, numberOfPair:Int):MutableList<MutableList<CardData>>{
        val specificList = mutableListOf<MutableList<CardData>>()
        val numberExistList = mutableListOf<Int>()
        for (card in cardList){
            val number = card.cardValue
            val type = card.cardType
            val numberList = mutableListOf<CardData>()
            for (card2 in cardList){
                if (numberExistList.isEmpty()){
                    if (number == card2.cardValue && type != card2.cardValue){
                        numberList.add(card2)
                    }
                }
                if (numberExistList.isNotEmpty()){
                    var isFoundSameNumber = false
                    for (existNum in numberExistList){
                        if (number == existNum){
                            isFoundSameNumber = true
                        }
                    }
                    if (number == card2.cardValue && type != card2.cardValue && !isFoundSameNumber){
                        numberList.add(card2)
                    }
                }
            }
            if (numberList.size == numberOfPair){
                numberExistList.add(numberList[0].cardValue)
                specificList.add(numberList)
            }
        }

        val iterator = cardList.iterator()
        while (iterator.hasNext()){
            val data = iterator.next()
            for (card in specificList){
                for (card2 in card){
                    if (data.cardValue == card2.cardValue && data.cardType == card2.cardType){
                        iterator.remove()
                    }
                }
            }
        }
        return specificList

    }


    //查看剩餘卡牌
    fun countLeftCards(
        cardList: MutableList<CardData>,
        currentList: MutableList<CardData>
    ) {
        val iterator = cardList.iterator()
        while (iterator.hasNext()){
            val data = iterator.next()
            for (straight in currentList){
                if (data.cardValue == straight.cardValue && data.cardType == straight.cardType){
                    iterator.remove()
                    break
                }
            }
        }
        Log.i("Poker","cardList剩餘 ${cardList.size} 張")
    }

}