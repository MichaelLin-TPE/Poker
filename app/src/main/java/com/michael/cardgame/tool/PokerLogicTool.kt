package com.michael.cardgame.tool

import android.util.Log
import com.michael.cardgame.bean.CardData
import com.michael.cardgame.constants.Constants.FOUR_OF_KIND
import com.michael.cardgame.constants.Constants.FULL_HOUSE
import com.michael.cardgame.constants.Constants.POKER_10
import com.michael.cardgame.constants.Constants.POKER_13
import com.michael.cardgame.constants.Constants.POKER_2
import com.michael.cardgame.constants.Constants.POKER_A
import com.michael.cardgame.constants.Constants.POKER_CLUBS
import com.michael.cardgame.constants.Constants.SINGLE
import com.michael.cardgame.constants.Constants.STRAIGHT
import com.michael.cardgame.constants.Constants.STRAIGHT_FLUSH
import com.michael.cardgame.constants.Constants.TWO_PAIR
import java.util.Random
import kotlin.math.min

object PokerLogicTool {

    /**
     * 尋找同花順 棄用
     */
    fun searchForStraightFlushNew(cardList: MutableList<CardData>): MutableList<MutableList<CardData>> {
        Log.i("Poker", "searchForStraightFlushNew")
        val dataList = mutableListOf<MutableList<CardData>>()
        val copylist = cardList.toMutableList()
        cardList.sortWith { o1, o2 ->
            o1.cardValue - o2.cardValue
        }
        //先找最大的同花順 為2開頭
        val straightWith2 = findStraightFlushWithSpecialNum(copylist, POKER_2)
        if (straightWith2.isNotEmpty()) {
            dataList.add(straightWith2)
        }
        //接者找第二大的同花順
        val straightWithA = findStraightFlushWithSpecialNum(copylist, POKER_A)
        if (straightWithA.isNotEmpty()) {
            dataList.add(straightWithA)
        }
        val straightWithNormal = findStraightFlushWithSpecialNum(copylist, -1)
        if (straightWithNormal.isNotEmpty()) {
            dataList.add(straightWithNormal)
        }

        return dataList
    }

    fun searchForStraightNew(cardList: MutableList<CardData>): MutableList<MutableList<CardData>> {
        Log.i("Poker", "searchForStraightNew")
        val dataList = mutableListOf<MutableList<CardData>>()
        val copylist = cardList.toMutableList()
        cardList.sortWith { o1, o2 ->
            o1.cardValue - o2.cardValue
        }
        //先找最大的同花順 為2開頭
        val straightWith2 = findStraightWithSpecialNum(copylist, POKER_2)
        if (straightWith2.isNotEmpty()) {
            dataList.add(straightWith2)
        }
        //接者找第二大的同花順
        val straightWithA = findStraightWithSpecialNum(copylist, POKER_A)
        if (straightWithA.isNotEmpty()) {
            dataList.add(straightWithA)
        }
        val straightWithNormal = findStraightWithSpecialNum(copylist, -1)
        if (straightWithNormal.isNotEmpty()) {
            dataList.add(straightWithNormal)
        }

        return dataList
    }

    private fun findStraightFlushWithSpecialNum(
        copylist: MutableList<CardData>,
        num: Int
    ): MutableList<CardData> {
        val straightFlushList = mutableListOf<CardData>()
        if (num == -1) {
            for (card in copylist) {
                if (straightFlushList.isEmpty()) {
                    straightFlushList.add(card)
                }
                val straightData = straightFlushList[straightFlushList.size - 1]
                var isAddData = false
                for (card2 in copylist) {
                    if (straightData.cardValue + 1 == card2.cardValue && straightData.cardType == card2.cardType) {
                        straightFlushList.add(card2)
                        isAddData = true
                    }
                }
                if (!isAddData) {
                    straightFlushList.clear()
                }
                if (straightFlushList.size == 5) {
                    return straightFlushList
                }
            }
            return mutableListOf()
        }
        var isFoundBiggestCard = false
        for (card in copylist) {
            if (card.cardValue == num) {
                isFoundBiggestCard = true
            }
        }
        Log.i("Poker", "isFoundBiggestCard : $isFoundBiggestCard")
        if (isFoundBiggestCard) {
            var isReadyToSearch = false
            for (card in copylist) {
                if (num == POKER_A && card.cardValue == POKER_10) {
                    Log.i("Poker", "找第二個最大的同花順")
                    isReadyToSearch = true
                } else if (card.cardValue == POKER_2 && card.cardValue != POKER_10) {
                    Log.i("Poker", "找最大的同花順")
                    isReadyToSearch = true
                }
                if (isReadyToSearch) {
                    if (straightFlushList.isEmpty()) {
                        straightFlushList.add(card)
                    }
                    val straightData = straightFlushList[straightFlushList.size - 1]
                    var isAddData = false
                    for (card2 in copylist) {
                        if (straightData.cardValue + 1 == 14 && num == POKER_A && card2.cardValue == POKER_A) {
                            straightFlushList.add(card2)
                            isAddData = true
                            continue
                        }
                        if (straightData.cardValue + 1 == card2.cardValue && straightData.cardType == card2.cardType) {
                            Log.i("Poker", "addData : ${card2.cardValue}")
                            straightFlushList.add(card2)
                            isAddData = true
                        }
                    }
                    if (!isAddData) {
                        straightFlushList.clear()
                    }
                    if (straightFlushList.size == 5) {
                        countLeftCards(copylist, straightFlushList)
                        return straightFlushList
                    }
                }
            }
        }
        return mutableListOf()
    }




    private fun findStraightWithSpecialNum(
        copylist: MutableList<CardData>,
        num: Int
    ): MutableList<CardData> {
        val straightFlushList = mutableListOf<CardData>()
        if (num == -1) {
            for (card in copylist) {
                if (straightFlushList.isEmpty()) {
                    straightFlushList.add(card)
                }
                val straightData = straightFlushList[straightFlushList.size - 1]
                var isAddData = false
                for (card2 in copylist) {
                    if (straightData.cardValue + 1 == card2.cardValue) {
                        straightFlushList.add(card2)
                        isAddData = true
                        break
                    }
                }
                if (!isAddData) {
                    straightFlushList.clear()
                }
                if (straightFlushList.size == 5) {
                    return straightFlushList
                }
            }
            return mutableListOf()
        }
        var isFoundBiggestCard = false
        for (card in copylist) {
            if (card.cardValue == num) {
                isFoundBiggestCard = true
            }
        }
        Log.i("Poker", "isFoundBiggestCard : $isFoundBiggestCard")
        if (isFoundBiggestCard) {
            var isReadyToSearch = false
            for (card in copylist) {
                if (num == POKER_A && card.cardValue == POKER_10) {
                    Log.i("Poker", "找第二個最大的同花順")
                    isReadyToSearch = true
                } else if (card.cardValue == POKER_2 && card.cardValue != POKER_10) {
                    Log.i("Poker", "找最大的同花順")
                    isReadyToSearch = true
                }
                if (isReadyToSearch) {
                    if (straightFlushList.isEmpty()) {
                        straightFlushList.add(card)
                    }
                    val straightData = straightFlushList[straightFlushList.size - 1]
                    var isAddData = false
                    for (card2 in copylist) {
                        if (straightData.cardValue + 1 == 14 && num == POKER_A && card2.cardValue == POKER_A) {
                            straightFlushList.add(card2)
                            isAddData = true
                            continue
                        }
                        if (straightData.cardValue + 1 == card2.cardValue) {
                            Log.i("Poker", "addData : ${card2.cardValue}")
                            straightFlushList.add(card2)
                            isAddData = true
                            break
                        }
                    }
                    if (!isAddData) {
                        straightFlushList.clear()
                    }
                    if (straightFlushList.size == 5) {
                        countLeftCards(copylist, straightFlushList)
                        return straightFlushList
                    }
                }
            }
        }
        return mutableListOf()
    }

    /**
     * 搜尋鐵支
     */
    fun searchForFourOfKindNew(cardList: MutableList<CardData>): MutableList<MutableList<CardData>> {
        val fourOfKindList = mutableListOf<MutableList<CardData>>()
        var fourOfKindNum = 0
        var sameCount = 0
        for (card in cardList){
            val dataList = mutableListOf<CardData>()
            if (sameCount == 4 && fourOfKindNum == card.cardValue){
                continue
            }else{
                sameCount = 0
                fourOfKindNum = card.cardValue
            }
            for (card2 in cardList){
                if (fourOfKindNum == card2.cardValue){
                    dataList.add(card2)
                    sameCount ++
                }
            }
            if (sameCount == 4){
                fourOfKindList.add(dataList)
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
        val straightFlushList = searchForStraightFlushNew(userCardList)
        if (straightFlushList.isEmpty()) {
            Log.i("Poker", "同花順清單為空")
            return false
        }
        var isFound3Clubs = false
        for (cardList in straightFlushList) {
            for (card in cardList) {
                Log.i(
                    "Poker",
                    "找到同花順 cardValue : ${card.cardValue} , cardType : ${Tool.getFlavor(card.cardType)}"
                )
                if (card.cardValue == 3 && card.cardType == POKER_CLUBS) {
                    isFound3Clubs = true
                }
            }
        }
        return isFound3Clubs
    }

    /**
     * 尋找順子且跟者梅花三
     */
    fun isStraightWith3Clubs(userCardList: MutableList<CardData>): Boolean {
        val straightFlushList = searchForStraightNew(userCardList)
        if (straightFlushList.isEmpty()) {
            Log.i("Poker", "同花順清單為空")
            return false
        }
        var isFound3Clubs = false
        for (cardList in straightFlushList) {
            for (card in cardList) {
                Log.i(
                    "Poker",
                    "找到同花順 cardValue : ${card.cardValue} , cardType : ${Tool.getFlavor(card.cardType)}"
                )
                if (card.cardValue == 3 && card.cardType == POKER_CLUBS) {
                    isFound3Clubs = true
                }
            }
        }
        return isFound3Clubs
    }

    fun isFourOfKindWith3Clubs(userCardList: MutableList<CardData>): Boolean {
        val fourOfKindList = searchForFourOfKindNew(userCardList)
        if (fourOfKindList.isEmpty()) {
            Log.i("Poker", "鐵枝清單為空")
            return false
        }
        var isFoundFourOfKind = false
        for (cardList in fourOfKindList) {
            for (card in cardList){
                Log.i(
                    "Poker",
                    "找到鐵支 cardValue : ${card.cardValue} , cardType : ${Tool.getFlavor(card.cardType)}"
                )
                if (card.cardValue == 3 && card.cardType == POKER_CLUBS) {
                    isFoundFourOfKind = true
                    break
                }
            }
            if (isFoundFourOfKind){
                break
            }
        }
        return isFoundFourOfKind
    }

    fun getMinSingleCard(
        userCardList: MutableList<CardData>
    ): CardData {
        val cardList = userCardList.toMutableList()
        //尋找同花順
        val straightFlushList = searchForStraightFlushNew(cardList)
        val straightIterator = straightFlushList.iterator()
        while (straightIterator.hasNext()) {
            val straightList = straightIterator.next()
            //剩餘卡片張數
            countLeftCards(cardList, straightList)
        }
        //尋找鐵支
        val fourOfKindList = searchForFourOfKindNew(cardList)
        //剩餘卡片張數
        for (fourList in fourOfKindList){
            countLeftCards(cardList, fourList)
        }
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
        var position = 0
        var num = 0
        for ((index,card) in cardList.withIndex()){
            if (num == 0 || card.cardValue == POKER_2){
                num = card.cardValue
                continue
            }
            if (num < card.cardValue){
                num = card.cardValue
                position = index
            }
        }
        return cardList[position]
    }

    private fun getMaxSingleCard(
        userCardList: MutableList<CardData>
    ): CardData? {
        val cardList = userCardList.toMutableList()
        //尋找同花順
        val straightFlushList = searchForStraightFlushNew(cardList)
        val straightIterator = straightFlushList.iterator()
        while (straightIterator.hasNext()) {
            val straightList = straightIterator.next()
            //剩餘卡片張數
            countLeftCards(cardList, straightList)
        }
        //尋找鐵支
        val fourList = searchForFourOfKindNew(cardList)
        val fourOfKindList = mutableListOf<CardData>()
        for (list in fourList){
            fourOfKindList.addAll(list)
        }
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
            if (((card.cardValue == POKER_2 || card.cardValue == POKER_A) && biggestCardNum != POKER_2)) {
                biggestCardNum = card.cardValue
                biggestIndex = index
                break
            }
            if (biggestCardNum < card.cardValue){
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
        val straightFlushList = searchForStraightFlushNew(userCardList)
        if (straightFlushList.isEmpty()) {
            return mutableListOf()
        }
        for (cardList in straightFlushList) {
            cardList.sortWith { o1, o2 ->
                o1.cardValue - o2.cardValue
            }
        }
        currentPlayCardDataList.sortWith { o1, o2 ->
            o1.cardValue - o2.cardValue
        }
        val straightList = mutableListOf<CardData>()
        var isUserCardWin = false
        for (cardList in straightFlushList) {
            val userCardData = cardList[0]
            val currentCard = currentPlayCardDataList[0]
            val isCurrentFoundSecondBiggestCards =
                isFoundSecondBiggestCards(currentPlayCardDataList)
            val isUserFoundSecondBiggestCards = isFoundSecondBiggestCards(cardList)
            if (currentCard.cardValue == POKER_2 && userCardData.cardValue != POKER_2) {
                isUserCardWin = false
            } else if (currentCard.cardValue == POKER_2) {
                isUserCardWin = userCardData.cardType > currentCard.cardType
            } else if (userCardData.cardValue == POKER_2) {
                isUserCardWin = true;
            } else if (currentCard.cardValue == userCardData.cardValue && (isCurrentFoundSecondBiggestCards && isUserFoundSecondBiggestCards)) {
                isUserCardWin = userCardData.cardType > currentCard.cardType
            } else if (isCurrentFoundSecondBiggestCards) {
                isUserCardWin = false
            } else if (isUserFoundSecondBiggestCards) {
                isUserCardWin = true
            } else if (currentCard.cardValue > userCardData.cardValue) {
                isUserCardWin = false
            } else if (currentCard.cardValue == userCardData.cardValue) {
                isUserCardWin = userCardData.cardType > currentCard.cardType
            }
            if (isUserCardWin) {
                straightList.addAll(cardList)
            }
        }
        return straightList
    }

    private fun isFoundSecondBiggestCards(cardList: MutableList<CardData>): Boolean {
        var isFoundA = false
        var isFoundK = false
        for (card in cardList) {
            if (card.cardValue == POKER_A) {
                isFoundA = true
            }
            if (card.cardValue == POKER_13) {
                isFoundK = true
            }
        }
        return isFoundA && isFoundK
    }

    fun searchForFourOfKindCompare(
        userCardList: MutableList<CardData>,
        currentPlayCardDataList: MutableList<CardData>
    ): MutableList<CardData> {
        val fourOfKindList = searchForFourOfKindNew(userCardList)
        if (fourOfKindList.isEmpty()) {
            return mutableListOf()
        }
        var currentCardNum = 0
        for (card in currentPlayCardDataList){
            if (currentCardNum == 0){
                currentCardNum = card.cardValue
                continue
            }
            if (currentCardNum == card.cardValue){
                break
            }else{
                currentCardNum = card.cardValue
            }
        }
        Log.i("Poker","比對 currentCardNum : $currentCardNum")
        var isUserCardWin = false
        val winFourOfKindList = mutableListOf<CardData>()
        for ((index,cardList) in fourOfKindList.withIndex()){
            val fourOfKindCardNum = cardList[0].cardValue
            if (fourOfKindCardNum == POKER_2){
                isUserCardWin = true
                winFourOfKindList.addAll(fourOfKindList[index])
                break
            }
            if (currentCardNum == POKER_2){
                isUserCardWin = false
                break
            }
            if (fourOfKindCardNum == POKER_A){
                isUserCardWin = true
                winFourOfKindList.addAll(fourOfKindList[index])
                break
            }
            if (currentCardNum == POKER_A){
                isUserCardWin = false
                break
            }
            if (fourOfKindCardNum > currentCardNum){
                isUserCardWin = true
                winFourOfKindList.addAll(fourOfKindList[index])
                break
            }
        }
        if (isUserCardWin){
            val cardData = getMinSingleCard(userCardList)
            winFourOfKindList.add(cardData)
        }
        Log.i("Poker","鐵支出牌 : ${winFourOfKindList.size}")
        return winFourOfKindList
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
        val lastUserCardNum = getFullHouseNum(fullHouseList)
        val lastCurrentCardNum = getFullHouseNum(currentPlayCardDataList)

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
        if (twoPairList.size != 2 || currentPlayCardDataList.size != 2) {
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
            if (list[0].cardValue == POKER_2 || (list[0].cardValue == POKER_A && biggestTwoPairNum != POKER_2 || biggestTwoPairNum < list[0].cardValue)) {
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

    var myCurrentPlayCardType = 0

    fun compareMyCardAndCurrentCard(
        mineSelectedCardList: MutableList<CardData>,
        currentPlayCardDataList: MutableList<CardData>
    ): Boolean {
        if (currentPlayCardDataList.isEmpty()) {
            if (isCheckSingleCard(mineSelectedCardList)) {
                return true
            }
            if (isCheckStraightFlush(mineSelectedCardList)) {
                return true
            }
            if (isCheckFourOfKind(mineSelectedCardList)) {
                return true
            }
            if (isCheckFullHouse(mineSelectedCardList)) {
                return true
            }
            if (isCheckStraight(mineSelectedCardList)){
                return true
            }
            if (isCheckTwoPair(mineSelectedCardList)) {
                return true
            }
            return false
        }
        mineSelectedCardList.sortWith { o1, o2 ->
            o1.cardValue - o2.cardValue
        }
        currentPlayCardDataList.sortWith { o1, o2 ->
            o1.cardValue - o2.cardValue
        }
        if (mineSelectedCardList.size != currentPlayCardDataList.size) {
            Log.i("Poker","張數不一致 無法出 我 : ${mineSelectedCardList.size} 張 , 對手 : ${currentPlayCardDataList.size} 張")
            return false
        }

        //如果雙方都是同花順
        if (isCheckStraightFlush(mineSelectedCardList) && isCheckStraightFlush(
                currentPlayCardDataList
            )
        ) {
            myCurrentPlayCardType = STRAIGHT_FLUSH
            Log.i("Poker", "都是同花順")
            val myFirstCardNum = mineSelectedCardList[0].cardValue
            val myFirstCardType = mineSelectedCardList[0].cardType
            val userFirstCardNum = currentPlayCardDataList[0].cardValue
            val userFirstCardType = currentPlayCardDataList[0].cardType
            if (myFirstCardNum == POKER_2 && userFirstCardNum == POKER_2) {
                return myFirstCardType > userFirstCardType
            }
            if (myFirstCardNum == POKER_2) {
                return true
            }
            if (userFirstCardNum == POKER_2) {
                return false
            }
            val mineLastCardNum = mineSelectedCardList[mineSelectedCardList.size - 1].cardValue
            val userLastCardNum =
                currentPlayCardDataList[currentPlayCardDataList.size - 1].cardValue
            val mineLastCardType = mineSelectedCardList[mineSelectedCardList.size - 1].cardType
            val userLastCardType =
                currentPlayCardDataList[currentPlayCardDataList.size - 1].cardType
            if (mineLastCardNum == userLastCardNum) {
                return mineLastCardType > userLastCardType
            }
            return mineLastCardNum > userLastCardNum
        }



        //如果雙方都是鐵支 那摩只會筆數字
        if (isCheckFourOfKind(mineSelectedCardList) && isCheckFourOfKind(currentPlayCardDataList)) {
            myCurrentPlayCardType = FOUR_OF_KIND
            Log.i("Poker", "都是鐵支")
            val mineCardNum = checkFourOfKindNum(mineSelectedCardList)
            val userCardNum = checkFourOfKindNum(currentPlayCardDataList)
            Log.i("Poker","minNum : $mineCardNum , userNum : $userCardNum")
            if (mineCardNum == 2) {
                return true
            }
            if (userCardNum == 2) {
                return false
            }
            if (mineCardNum == 1) {
                return true
            }
            if (userCardNum == 1) {
                return false
            }
            return mineCardNum > userCardNum
        }


        //如果雙方都是葫蘆 那麼只會比對數字
        if (isCheckFullHouse(mineSelectedCardList) && isCheckFullHouse(currentPlayCardDataList)) {
            myCurrentPlayCardType = FULL_HOUSE
            Log.i("Poker", "都是葫蘆")
            val lastMyNum = getFullHouseNum(mineSelectedCardList)
            val lastUserNum = getFullHouseNum(currentPlayCardDataList)

            if (lastMyNum == 2) {
                return true
            }
            if (lastUserNum == 2) {
                return false
            }
            if (lastMyNum == 1) {
                return true
            }
            if (lastUserNum == 1) {
                return false
            }
            return lastMyNum > lastUserNum
        }

        //如果雙方都是順子
        if (isCheckStraight(mineSelectedCardList) && isCheckStraight(
                currentPlayCardDataList
            )
        ) {
            myCurrentPlayCardType = STRAIGHT
            Log.i("Poker", "都是順子")
            val myFirstCardNum = mineSelectedCardList[0].cardValue
            val myFirstCardType = mineSelectedCardList[0].cardType
            val userFirstCardNum = currentPlayCardDataList[0].cardValue
            val userFirstCardType = currentPlayCardDataList[0].cardType
            if (myFirstCardNum == POKER_2 && userFirstCardNum == POKER_2) {
                return myFirstCardType > userFirstCardType
            }
            if (myFirstCardNum == POKER_2) {
                return true
            }
            if (userFirstCardNum == POKER_2) {
                return false
            }
            val mineLastCardNum = mineSelectedCardList[mineSelectedCardList.size - 1].cardValue
            val userLastCardNum =
                currentPlayCardDataList[currentPlayCardDataList.size - 1].cardValue
            val mineLastCardType = mineSelectedCardList[mineSelectedCardList.size - 1].cardType
            val userLastCardType =
                currentPlayCardDataList[currentPlayCardDataList.size - 1].cardType
            if (mineLastCardNum == userLastCardNum) {
                return mineLastCardType > userLastCardType
            }
            return mineLastCardNum > userLastCardNum
        }

        //如果雙方都是兔胚 那麼除了比對數字之外還會比對花色
        if (isCheckTwoPair(mineSelectedCardList) && isCheckTwoPair(currentPlayCardDataList)) {
            myCurrentPlayCardType = TWO_PAIR
            Log.i("Poker", "都是兔胚")
            val lastMyNum = mineSelectedCardList[mineSelectedCardList.size - 1].cardValue
            val lastUserNum = currentPlayCardDataList[currentPlayCardDataList.size - 1].cardValue
            val lastMyType = mineSelectedCardList[mineSelectedCardList.size - 1].cardType
            val lastUserType = currentPlayCardDataList[currentPlayCardDataList.size - 1].cardType
            if (lastMyNum == lastUserNum) {
                return lastMyType > lastUserType
            }
            if (lastMyNum == 2) {
                return true
            }
            if (lastUserNum == 2) {
                return false
            }
            if (lastMyNum == 1) {
                return true
            }
            if (lastUserNum == 1) {
                return false
            }
            return lastMyNum > lastUserNum
        }
        //判斷是否為單張
        if (isCheckSingleCard(mineSelectedCardList) && isCheckSingleCard(currentPlayCardDataList)) {
            myCurrentPlayCardType = SINGLE
            Log.i("Poker", "都是單張")
            val lastMyNum = mineSelectedCardList[mineSelectedCardList.size - 1].cardValue
            val lastUserNum = currentPlayCardDataList[currentPlayCardDataList.size - 1].cardValue
            val lastMyType = mineSelectedCardList[mineSelectedCardList.size - 1].cardType
            val lastUserType = currentPlayCardDataList[currentPlayCardDataList.size - 1].cardType
            if (lastMyNum == lastUserNum) {
                return lastMyType > lastUserType
            }
            if (lastMyNum == 2) {
                return true
            }
            if (lastUserNum == 2) {
                return false
            }
            if (lastMyNum == 1) {
                return true
            }
            if (lastUserNum == 1) {
                return false
            }
            return lastMyNum > lastUserNum
        }


        return false
    }

    private fun getFullHouseNum(cardList: MutableList<CardData>): Int {
        var sameCount = 0
        var num = 0
        for (card in cardList){
            for (card2 in cardList){
                if (card.cardValue == card2.cardValue){
                    sameCount ++
                }
            }
            if (sameCount == 3){
                num = card.cardValue
                break
            }else{
                sameCount = 0
            }
        }
        return num
    }

    private fun isCheckSingleCard(cardList: MutableList<CardData>): Boolean {
        return cardList.size == 1
    }

    private fun isCheckTwoPair(cardList: MutableList<CardData>): Boolean {
        if (cardList.size != 2) {
            return false
        }
        val firstNum = cardList[0].cardValue
        val secondNum = cardList[1].cardValue
        return firstNum == secondNum
    }

    private fun isCheckFullHouse(cardList: MutableList<CardData>): Boolean {
        if (cardList.size != 5) {
            return false
        }
        var sameCount = 0
        for (card in cardList){
            for (card2 in cardList){
                if (card.cardValue == card2.cardValue){
                    sameCount ++
                }
            }
            if (sameCount == 3){
                break
            }else{
                sameCount = 0
            }
        }
        return sameCount == 3
    }

    /**
     * 判斷是否為同花順
     */
    private fun isCheckStraightFlush(cardList: MutableList<CardData>): Boolean {
        if (cardList.size != 5) {
            return false
        }
        cardList.sortWith { o1, o2 ->
            o1.cardValue - o2.cardValue
        }

        var collectCount = 0
        for ((index, card) in cardList.withIndex()) {
            for (position in index + 1 until cardList.size) {
                val cardData = cardList[position]
                if (cardData.cardValue == 1 && card.cardValue == 13) {
                    collectCount++
                    continue
                }
                if (cardData.cardValue > card.cardValue && cardData.cardValue - card.cardValue == 1 && cardData.cardType == card.cardType) {
                    collectCount++
                }
            }
        }
        return collectCount == 4
    }

    /**
     * 判斷是否為順子
     */
    private fun isCheckStraight(cardList: MutableList<CardData>): Boolean {
        if (cardList.size != 5) {
            return false
        }
        cardList.sortWith { o1, o2 ->
            o1.cardValue - o2.cardValue
        }

        var collectCount = 0
        for ((index, card) in cardList.withIndex()) {
            for (position in index + 1 until cardList.size) {
                val cardData = cardList[position]
                if (cardData.cardValue == 1 && card.cardValue == 13) {
                    collectCount++
                    continue
                }
                if (cardData.cardValue > card.cardValue && cardData.cardValue - card.cardValue == 1) {
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
            Log.i("Poker", "鐵支不到五支")
            return false
        }
        Log.i("Poker","出的牌有 ${cardList.size} 張")
        var sameCount = 0
        for (card in cardList) {
            for (card2 in cardList) {
                if (card.cardValue != card2.cardValue) {
                    continue
                }
                sameCount ++
            }
            if (sameCount == 4){
                break
            }else{
                sameCount = 0
            }
        }
        Log.i("Poker", "判斷鐵支 : $sameCount 張")
        return sameCount == 4
    }

    fun checkCardsType(mineSelectedCardList: MutableList<CardData>): Int {
        if (mineSelectedCardList.size > 5) {
            return -1
        }
        if (isCheckStraightFlush(mineSelectedCardList)) {
            return STRAIGHT_FLUSH
        }
        if (isCheckFourOfKind(mineSelectedCardList)) {
            return FOUR_OF_KIND
        }
        if (isCheckFullHouse(mineSelectedCardList)) {
            return FULL_HOUSE
        }
        if (isCheckStraight(mineSelectedCardList)){
            return STRAIGHT
        }
        if (isCheckTwoPair(mineSelectedCardList)) {
            return TWO_PAIR
        }
        if (isCheckSingleCard(mineSelectedCardList)) {
            return SINGLE
        }
        return -1
    }

    fun playTwoPair(userCardList: MutableList<CardData>): MutableList<CardData> {
        val cardList = userCardList.toMutableList()
        val twoPairLists = searchForSpecificCard(cardList, 2)
        if (twoPairLists.isEmpty()) {
            return mutableListOf()
        }
        var minNum = 0
        var position = 0
        for ((index, pairList) in twoPairLists.withIndex()) {
            if (minNum == 0) {
                minNum = pairList[0].cardValue
                position = index
                continue
            }
            if (minNum > pairList[0].cardValue) {
                minNum = pairList[0].cardValue
                position = index
            }
        }
        return twoPairLists[position]
    }

    fun searchForStraightCompare(
        userCardList: MutableList<CardData>,
        currentPlayCardDataList: MutableList<CardData>
    ): MutableList<CardData> {
        val straightFlushList = searchForStraightNew(userCardList)
        if (straightFlushList.isEmpty()) {
            return mutableListOf()
        }
        for (cardList in straightFlushList) {
            cardList.sortWith { o1, o2 ->
                o1.cardValue - o2.cardValue
            }
        }
        currentPlayCardDataList.sortWith { o1, o2 ->
            o1.cardValue - o2.cardValue
        }
        val straightList = mutableListOf<CardData>()
        var isUserCardWin = false
        for (cardList in straightFlushList) {
            val userCardData = cardList[0]
            val currentCard = currentPlayCardDataList[0]
            val isCurrentFoundSecondBiggestCards =
                isFoundSecondBiggestCards(currentPlayCardDataList)
            val isUserFoundSecondBiggestCards = isFoundSecondBiggestCards(cardList)
            if (currentCard.cardValue == POKER_2 && userCardData.cardValue != POKER_2) {
                isUserCardWin = false
            } else if (currentCard.cardValue == POKER_2) {
                isUserCardWin = userCardData.cardType > currentCard.cardType
            } else if (userCardData.cardValue == POKER_2) {
                isUserCardWin = true;
            } else if (currentCard.cardValue == userCardData.cardValue && (isCurrentFoundSecondBiggestCards && isUserFoundSecondBiggestCards)) {
                isUserCardWin = userCardData.cardType > currentCard.cardType
            } else if (isCurrentFoundSecondBiggestCards) {
                isUserCardWin = false
            } else if (isUserFoundSecondBiggestCards) {
                isUserCardWin = true
            } else if (currentCard.cardValue > userCardData.cardValue) {
                isUserCardWin = false
            } else if (currentCard.cardValue == userCardData.cardValue) {
                isUserCardWin = userCardData.cardType > currentCard.cardType
            }
            if (isUserCardWin) {
                straightList.addAll(cardList)
            }
        }
        return straightList
    }

}