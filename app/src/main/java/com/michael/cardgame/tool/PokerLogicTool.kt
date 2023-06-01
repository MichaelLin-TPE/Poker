package com.michael.cardgame.tool

import android.util.Log
import com.michael.cardgame.bean.CardData
import com.michael.cardgame.constants.Constants.POKER_2
import com.michael.cardgame.constants.Constants.POKER_A
import com.michael.cardgame.constants.Constants.POKER_CLUBS
import java.util.Collections
import java.util.Random
import kotlin.math.min
import kotlin.math.sin

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
            Log.i("Poker", "同花順清單為空")
            return false
        }

        var isFound3Clubs = false
        for (card in straightFlushList) {
            Log.i(
                "Poker",
                "找到同花順 cardValue : ${card.cardValue} , cardType : ${Tool.getFlavor(card.cardType)}"
            )
            if (card.cardValue == 3 && card.cardType == POKER_CLUBS) {
                isFound3Clubs = true
            }
        }
        return isFound3Clubs
    }

    fun isFourOfKindWith3Clubs(userCardList: MutableList<CardData>): Boolean {
        val fourOfKindList = searchForFourOfKind(userCardList)
        if (fourOfKindList.isEmpty()) {
            Log.i("Poker", "鐵枝清單為空")
            return false
        }
        var isFoundFourOfKind = false
        for (card in fourOfKindList) {
            Log.i(
                "Poker",
                "找到鐵支 cardValue : ${card.cardValue} , cardType : ${Tool.getFlavor(card.cardType)}"
            )
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

    fun getMaxSingleCard(
        userCardList: MutableList<CardData>
    ): CardData? {
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

        if (cardList.isEmpty()) {
            Log.i("Poker", "沒有單張卡牌")
            return null
        }
        var biggestCardNum = 0
        var biggestIndex = 0
        for ((index, card) in cardList.withIndex()) {
            if (biggestCardNum == 0) {
                biggestCardNum = card.cardValue
                biggestIndex = index
                continue
            }
            if (biggestCardNum < card.cardValue) {
                biggestCardNum = card.cardValue
                biggestIndex = index
            }
        }
        return cardList[biggestIndex]
    }


    fun getMinSingleCardOnlyThreeClub(
        userCardList: MutableList<CardData>
    ): CardData {
        var cardData: CardData? = null
        for (card in userCardList) {
            if (card.cardValue == 3 && card.cardType == POKER_CLUBS) {
                cardData = card
            }
        }
        if (cardData == null) {
            Log.i("Poker", "cardData is null")
        }
        return cardData!!
    }


    fun isFullHouseWith3Clubs(userCardList: MutableList<CardData>): Boolean {
        val fullHouseList = searchForFullHouse(userCardList, true)
        if (fullHouseList.isEmpty()) {
            Log.i("Poker", "葫蘆清單為空")
            return false
        }
        var isFoundFullHouse = false
        for (card in fullHouseList) {
            Log.i(
                "Poker",
                "找到葫蘆 cardValue : ${card.cardValue} , cardType : ${Tool.getFlavor(card.cardType)}"
            )
            if (card.cardValue == 3 && card.cardType == POKER_CLUBS) {
                isFoundFullHouse = true
            }
        }
        return isFoundFullHouse
    }

    fun isTwoPairWith3Clubs(userCardList: MutableList<CardData>): Boolean {
        val twoPairList = searchTwoPair(userCardList, true)
        if (twoPairList.isEmpty()) {
            Log.i("Poker", "找到兔胚")
            return false
        }
        var isFountTwoPair = false
        for (card in twoPairList) {
            if (card.cardValue == 3 && card.cardType == POKER_CLUBS) {
                Log.i(
                    "Poker",
                    "找到兔胚 cardValue : ${card.cardValue} , cardType : ${Tool.getFlavor(card.cardType)}"
                )
                isFountTwoPair = true
            }
        }
        return isFountTwoPair
    }

    /**
     * 同花順比較
     */
    fun searchForStraightFlushCompare(
        userCardList: MutableList<CardData>,
        currentPlayCardDataList: MutableList<CardData>
    ): MutableList<CardData> {
        val straightFlushList = searchForStraightFlush(userCardList)
        val userCardData = straightFlushList[0]
        val currentCard = currentPlayCardDataList[0]
        var isUserCardWin = false
        if (currentCard.cardValue == POKER_2 && userCardData.cardValue != POKER_2) {
            isUserCardWin = false
        } else if (currentCard.cardValue == POKER_2) {
            isUserCardWin = userCardData.cardType > currentCard.cardType
        } else if (currentCard.cardValue > userCardData.cardValue) {
            isUserCardWin = false
        } else if (currentCard.cardValue == userCardData.cardValue) {
            isUserCardWin = userCardData.cardType > currentCard.cardType
        }
        return if (isUserCardWin) straightFlushList else mutableListOf()
    }

    fun searchForFourOfKindCompare(
        userCardList: MutableList<CardData>,
        currentPlayCardDataList: MutableList<CardData>
    ): MutableList<CardData> {
        val fourOfKindList = searchForFourOfKind(userCardList)
        val userCardData = fourOfKindList[0]
        val currentCard = currentPlayCardDataList[0]

        var isUserCardWin = false
        //當現有卡牌組合為都是老二的時候,用戶的卡牌不是為老二時,現有的牌組贏
        if (currentCard.cardValue == POKER_2 && userCardData.cardValue != POKER_2) {
            return mutableListOf()
        }
        //當現有卡牌組合不為老二時 用戶勝
        if (userCardData.cardValue == POKER_2) {
            return fourOfKindList
        }
        //當現有卡牌組合為都是A的時候,用戶的卡牌不是為A時,現有的牌組贏
        if (currentCard.cardValue == POKER_A && userCardData.cardValue != POKER_A) {
            return mutableListOf()
        }
        //當現有卡牌組合不為A時 用戶勝
        if (userCardData.cardValue == POKER_A) {
            return fourOfKindList
        }

        //雙方卡牌比大小
        return if (currentCard.cardValue > userCardData.cardValue) {
            mutableListOf()
        } else {
            fourOfKindList
        }
    }

    fun searchForFullHouseCompare(
        userCardList: MutableList<CardData>,
        currentPlayCardDataList: MutableList<CardData>
    ): MutableList<CardData> {
        val fullHouseList = findBiggestFullHouse(userCardList)
        if (fullHouseList.size != 5 || currentPlayCardDataList.size != 5) {
            Log.i("Poker", "有問題 葫蘆並沒有滿五張")
            return mutableListOf()
        }
        val lastUserCardNum = fullHouseList[fullHouseList.size - 1].cardValue
        val lastCurrentCardNum = currentPlayCardDataList[currentPlayCardDataList.size - 1].cardValue

        if (lastCurrentCardNum == POKER_2) {
            return mutableListOf()
        }
        if (lastUserCardNum == POKER_2) {
            return fullHouseList
        }
        if (lastCurrentCardNum == POKER_A) {
            return mutableListOf()
        }
        if (lastUserCardNum == POKER_A) {
            return fullHouseList
        }
        if (lastUserCardNum > lastCurrentCardNum) {
            return fullHouseList
        }
        return mutableListOf()
    }

    private fun findBiggestFullHouse(userCardList: MutableList<CardData>): MutableList<CardData> {
        val copyList = userCardList.toMutableList()
        val fullHouseList = mutableListOf<CardData>()
        val threePairList = searchForSpecificCard(copyList, 3)
        val twoPairList = searchForSpecificCard(copyList, 2)

        if (threePairList.isEmpty() || twoPairList.isEmpty()) {
            return mutableListOf()
        }

        var biggestThreePairNum = 0
        var threePairIndex = 0
        var smallestTwoPairNum = 0
        var twoPairIndex = 0
        for ((index, list) in threePairList.withIndex()) {
            if (biggestThreePairNum == 0) {
                biggestThreePairNum = list[0].cardValue
                threePairIndex = index
                continue
            }
            if (biggestThreePairNum < list[0].cardValue) {
                biggestThreePairNum = list[0].cardValue
                threePairIndex = index
            }

        }
        for ((index, list) in twoPairList.withIndex()) {
            if (smallestTwoPairNum == 0) {
                smallestTwoPairNum = list[0].cardValue
                twoPairIndex = index
                continue
            }
            if (smallestTwoPairNum > list[0].cardValue) {
                smallestTwoPairNum = list[0].cardValue
                twoPairIndex = index
            }
        }
        for (data in twoPairList[twoPairIndex]) {
            fullHouseList.add(data)
        }
        for (data in threePairList[threePairIndex]) {
            fullHouseList.add(data)
        }
        return fullHouseList
    }

    fun searchTwoPairCompare(
        userCardList: MutableList<CardData>,
        currentPlayCardDataList: MutableList<CardData>
    ): MutableList<CardData> {
        val twoPairList = findBiggestTwoPair(userCardList)
        if (twoPairList.size != 2 && currentPlayCardDataList.size != 2) {
            Log.i("Michael", "錯誤兔胚並沒有兩張")
            return mutableListOf()
        }
        val lastUserCardNum = twoPairList[twoPairList.size - 1].cardValue
        val lastCurrentCardNum = currentPlayCardDataList[currentPlayCardDataList.size - 1].cardValue

        if (lastCurrentCardNum == POKER_2) {
            return mutableListOf()
        }
        if (lastUserCardNum == POKER_2) {
            return twoPairList
        }
        if (lastCurrentCardNum == POKER_A) {
            return mutableListOf()
        }
        if (lastUserCardNum == POKER_A) {
            return twoPairList
        }
        if (lastUserCardNum > lastCurrentCardNum) {
            return twoPairList
        }
        return mutableListOf()
    }

    private fun findBiggestTwoPair(userCardList: MutableList<CardData>): MutableList<CardData> {
        val copyList = userCardList.toMutableList()
        val twoPairList = searchForSpecificCard(copyList, 2)
        val twoPair = mutableListOf<CardData>()
        if (twoPairList.isEmpty()) {
            return mutableListOf()
        }
        var biggestTwoPairNum = 0
        var twoPairIndex = 0
        for ((index, list) in twoPairList.withIndex()) {
            if (biggestTwoPairNum == 0) {
                biggestTwoPairNum = list[0].cardValue
                twoPairIndex = index
                continue
            }
            if (biggestTwoPairNum < list[0].cardValue) {
                biggestTwoPairNum = list[0].cardValue
                twoPairIndex = index
            }
        }
        twoPair.addAll(twoPairList[twoPairIndex])
        return twoPair
    }

    fun getMinSingleCardCompare(
        userCardList: MutableList<CardData>,
        currentPlayCardDataList: MutableList<CardData>
    ): CardData? {
        val singleCard = getMaxSingleCard(userCardList) ?: return null

        val lastUserCardNum = singleCard.cardValue
        val lastCurrentCardNum = currentPlayCardDataList[currentPlayCardDataList.size - 1].cardValue

        if (lastCurrentCardNum == POKER_2) {
            return null
        }
        if (lastUserCardNum == POKER_2) {
            return singleCard
        }
        if (lastCurrentCardNum == POKER_A) {
            return null
        }
        if (lastUserCardNum == POKER_A) {
            return singleCard
        }
        if (lastUserCardNum > lastCurrentCardNum) {
            return singleCard
        }
        return null

    }

    fun compareMyCardAndCurrentCard(
        mineSelectedCardList: MutableList<CardData>,
        currentPlayCardDataList: MutableList<CardData>
    ): Boolean {

        mineSelectedCardList.sortWith(Comparator { o1, o2 ->
            o1.cardValue - o2.cardValue
        })

        //如果雙方都是同花順
        if (isCheckStraightFlush(mineSelectedCardList) && isCheckStraightFlush(currentPlayCardDataList)) {
            val mineLastCardNum = mineSelectedCardList[mineSelectedCardList.size - 1].cardValue
            val userLastCardNum = currentPlayCardDataList[currentPlayCardDataList.size - 1].cardValue
            val mineLastCardType = mineSelectedCardList[mineSelectedCardList.size - 1].cardType
            val userLastCardType = currentPlayCardDataList[currentPlayCardDataList.size - 1].cardType
            if (mineLastCardNum == userLastCardNum){
                return mineLastCardType > userLastCardType
            }
            return mineLastCardNum > userLastCardNum
        }
        //如果雙方都是鐵支 那摩只會筆數字
        if (isCheckFourOfKind(mineSelectedCardList) && isCheckFourOfKind(currentPlayCardDataList)) {
            val mineCardNum = checkFourOfKindNum(mineSelectedCardList)
            val userCardNum = checkFourOfKindNum(currentPlayCardDataList)
            return mineCardNum > userCardNum
        }
        if (mineSelectedCardList.size != currentPlayCardDataList.size){
            return false
        }

        return false
    }

    /**
     * 判斷是否為同花順
     */
    private fun isCheckStraightFlush(cardList: MutableList<CardData>): Boolean {
        if (cardList.size != 5) {
            return false
        }
        var collectCount = 0
        for ((index, card) in cardList.withIndex()) {
            for (position in index + 1 until cardList.size) {
                val cardData = cardList[position]
                if (cardData.cardValue > card.cardValue && cardData.cardValue - card.cardValue == 1 && cardData.cardType == card.cardType) {
                    collectCount++
                }
            }
        }
        return collectCount == 4
    }

    /**
     * 尋找鐵支的卡牌數字
     */
    private fun checkFourOfKindNum(cardList: MutableList<CardData>): Int {
        var num = 0

        for (card in cardList) {
            if (num == 0) {
                num = card.cardValue
                continue
            }
            if (num != card.cardValue) {
                num = card.cardValue
            }
        }
        return num
    }

    /**
     * 判斷是否為鐵支
     */
    private fun isCheckFourOfKind(cardList: MutableList<CardData>): Boolean {
        if (cardList.size != 5) {
            return false
        }
        var num = 0
        var sameCount = 0
        for (card in cardList) {
            if (num == 0) {
                num = card.cardValue
                sameCount++
                continue
            }
            if (num != card.cardValue) {
                num = card.cardValue
                sameCount = 1
            } else {
                sameCount++
            }
        }
        return sameCount == 4
    }

}