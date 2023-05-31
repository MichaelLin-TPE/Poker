package com.michael.cardgame.tool

import android.util.Log
import com.michael.cardgame.bean.CardData
import com.michael.cardgame.constants.Constants.POKER_CLUBS
import java.util.Random

object PokerLogicTool {

    /**
     * 尋找同花順
     */
    fun searchForStraightFlush(cardList: MutableList<CardData>): MutableList<CardData> {
        val straightFlushList = mutableListOf<CardData>()
        for (card in cardList) {
            if (straightFlushList.isEmpty()) {
                straightFlushList.add(card)
            }
            val straightData = straightFlushList[straightFlushList.size - 1]
            var isAddData = false
            for (card2 in cardList) {
                if (straightData.cardValue + 1 == card2.cardValue && straightData.cardType == card2.cardType) {
                    straightFlushList.add(card2)
                    isAddData = true
                }
            }
            if (!isAddData) {
                straightFlushList.clear()
            }
            if (straightFlushList.size == 5) {
                break
            }
        }
        return straightFlushList
    }

    /**
     * 尋找鐵支
     */
    fun searchForFourOfKind(cardList: MutableList<CardData>): MutableList<CardData> {
        val fourOfKindList = mutableListOf<CardData>()
        val numberExistList = mutableListOf<Int>()
        for (card in cardList) {
            val number = card.cardValue
            val type = card.cardType
            val numberList = mutableListOf<CardData>()
            for (card2 in cardList) {
                if (numberExistList.isEmpty()) {
                    if (number == card2.cardValue && type != card2.cardValue) {
                        numberList.add(card2)
                    }
                }
                if (numberExistList.isNotEmpty()) {
                    var isFoundSameNumber = false
                    for (existNum in numberExistList) {
                        if (number == existNum) {
                            isFoundSameNumber = true
                        }
                    }
                    if (number == card2.cardValue && type != card2.cardValue && !isFoundSameNumber) {
                        numberList.add(card2)
                    }
                }
            }
            if (numberList.size == 4) {
                numberExistList.add(numberList[0].cardValue)
                fourOfKindList.addAll(numberList)
            }
        }
        return fourOfKindList
    }

    /**
     * 尋找葫蘆
     */
    fun searchForFullHouse(
        cardList: MutableList<CardData>,
        isNeedThreeOfClubs: Boolean
    ): MutableList<CardData> {
        val copyList = cardList.toMutableList()
        val fullHouseList = mutableListOf<CardData>()
        val threePairList = searchForSpecificCard(copyList, 3)
        val twoPairList = searchForSpecificCard(copyList, 2)
        for (index in 0 until threePairList.size) {
            if (index >= twoPairList.size) {
                continue
            }
            if (isNeedThreeOfClubs) { //只有在找梅花三時需要用到
                var isFoundThree = false
                for (card in twoPairList[index]) {
                    if (card.cardValue == 3 && card.cardType == POKER_CLUBS) {
                        isFoundThree = true
                    }
                }
                for (card in threePairList[index]) {
                    if (card.cardValue == 3 && card.cardType == POKER_CLUBS) {
                        isFoundThree = true
                    }
                }
                if (isFoundThree) {
                    fullHouseList.addAll(twoPairList[index])
                    fullHouseList.addAll(threePairList[index])
                }
                continue
            }
            fullHouseList.addAll(twoPairList[index])
            fullHouseList.addAll(threePairList[index])
        }
        return fullHouseList
    }

    fun searchTwoPair(
        cardList: MutableList<CardData>,
        isNeedThreeOfClubs: Boolean
    ): MutableList<CardData> {
        val copyList = cardList.toMutableList()
        val twoPairList = searchForSpecificCard(copyList, 2)
        val twoPair = mutableListOf<CardData>()
        for (index in 0 until twoPairList.size) {
            if (isNeedThreeOfClubs) {
                var isFoundThree = false
                for (card in twoPairList[index]) {
                    if (card.cardValue == 3 && card.cardType == POKER_CLUBS) {
                        isFoundThree = true
                        break
                    }
                }
                if (isFoundThree) {
                    twoPair.addAll(twoPairList[index])
                }
                continue
            }
            twoPair.addAll(twoPairList[index])
        }
        return twoPair
    }


    private fun searchForSpecificCard(
        cardList: MutableList<CardData>,
        numberOfPair: Int
    ): MutableList<MutableList<CardData>> {
        val specificList = mutableListOf<MutableList<CardData>>()
        val numberExistList = mutableListOf<Int>()
        for (card in cardList) {
            val number = card.cardValue
            val type = card.cardType
            val numberList = mutableListOf<CardData>()
            for (card2 in cardList) {
                if (numberExistList.isEmpty()) {
                    if (number == card2.cardValue && type != card2.cardValue) {
                        numberList.add(card2)
                    }
                }
                if (numberExistList.isNotEmpty()) {
                    var isFoundSameNumber = false
                    for (existNum in numberExistList) {
                        if (number == existNum) {
                            isFoundSameNumber = true
                        }
                    }
                    if (number == card2.cardValue && type != card2.cardValue && !isFoundSameNumber) {
                        numberList.add(card2)
                    }
                }
            }
            if (numberList.size == numberOfPair) {
                numberExistList.add(numberList[0].cardValue)
                specificList.add(numberList)
            }
        }

        val iterator = cardList.iterator()
        while (iterator.hasNext()) {
            val data = iterator.next()
            for (card in specificList) {
                for (card2 in card) {
                    if (data.cardValue == card2.cardValue && data.cardType == card2.cardType) {
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
        while (iterator.hasNext()) {
            val data = iterator.next()
            for (straight in currentList) {
                if (data.cardValue == straight.cardValue && data.cardType == straight.cardType) {
                    iterator.remove()
                    break
                }
            }
        }
    }

    /**
     * 尋找同花順且跟者梅花三
     */
    fun isStraightFlushWith3Clubs(userCardList: MutableList<CardData>): Boolean {
        val straightFlushList = searchForStraightFlush(userCardList)
        if (straightFlushList.isEmpty()) {
            Log.i("Poker","同花順清單為空")
            return false
        }

        var isFound3Clubs = false
        for (card in straightFlushList) {
            Log.i("Poker","找到同花順 cardValue : ${card.cardValue} , cardType : ${Tool.getFlavor(card.cardType)}")
            if (card.cardValue == 3 && card.cardType == POKER_CLUBS) {
                isFound3Clubs = true
            }
        }
        return isFound3Clubs
    }

    fun isFourOfKindWith3Clubs(userCardList: MutableList<CardData>): Boolean {
        val fourOfKindList = searchForFourOfKind(userCardList)
        if (fourOfKindList.isEmpty()) {
            Log.i("Poker","鐵枝清單為空")
            return false
        }
        var isFoundFourOfKind = false
        for (card in userCardList) {
            Log.i("Poker","找到鐵支 cardValue : ${card.cardValue} , cardType : ${Tool.getFlavor(card.cardType)}")
            if (card.cardValue == 3 && card.cardType == POKER_CLUBS) {
                isFoundFourOfKind = true
            }
        }
        return isFoundFourOfKind
    }

    fun getMinSingleCard(
        userCardList: MutableList<CardData>
    ): CardData {
        val cardList = userCardList.toMutableList()
        //尋找同花順
        val straightFlushList = searchForStraightFlush(cardList)
        //剩餘卡片張數
        countLeftCards(cardList, straightFlushList)
        //尋找鐵支
        val fourOfKindList = searchForFourOfKind(cardList)
        //剩餘卡片張數
        countLeftCards(cardList, fourOfKindList)
        //尋找葫蘆
        val fullHouseList = searchForFullHouse(cardList, true)
        //剩餘卡片張數
        countLeftCards(cardList, fullHouseList)
        //尋找兔胚
        val twoPairList = searchTwoPair(cardList, true)
        //剩餘卡片張數
        countLeftCards(cardList, twoPairList)
        val iterator = cardList.iterator()
        while (iterator.hasNext()) {
            val data = iterator.next()
            if (data.cardValue == 3 && data.cardType == POKER_CLUBS) {
                iterator.remove()
            }
        }
        if (cardList.isEmpty()) {
            return twoPairList[Random().nextInt(twoPairList.size)]
        }
        val randomIndex = Random().nextInt(cardList.size)
        return cardList[randomIndex]
    }

    fun getMinSingleCardOnlyThreeClub(
        userCardList: MutableList<CardData>
    ): CardData {
        var cardData : CardData? = null
        for (card in userCardList){
            if (card.cardValue == 3 && card.cardType == POKER_CLUBS){
                cardData = card
            }
        }
        return cardData!!
    }


    fun isFullHouseWith3Clubs(userCardList: MutableList<CardData>): Boolean {
        val fullHouseList = searchForFullHouse(userCardList, true)
        if (fullHouseList.isEmpty()) {
            Log.i("Poker","葫蘆清單為空")
            return false
        }
        var isFoundFullHouse = false
        for (card in fullHouseList) {
            Log.i("Poker","找到葫蘆 cardValue : ${card.cardValue} , cardType : ${Tool.getFlavor(card.cardType)}")
            if (card.cardValue == 3 && card.cardType == POKER_CLUBS) {
                isFoundFullHouse = true
            }
        }
        return isFoundFullHouse
    }

    fun isTwoPairWith3Clubs(userCardList: MutableList<CardData>): Boolean {
        val twoPairList = searchTwoPair(userCardList, true)
        if (twoPairList.isEmpty()) {
            Log.i("Poker","找到兔胚")
            return false
        }
        var isFountTwoPair = false
        for (card in twoPairList) {
            if (card.cardValue == 3 && card.cardType == POKER_CLUBS) {
                Log.i("Poker","找到兔胚 cardValue : ${card.cardValue} , cardType : ${Tool.getFlavor(card.cardType)}")
                isFountTwoPair = true
            }
        }
        return isFountTwoPair
    }

}