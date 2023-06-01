package com.michael.cardgame

import android.app.Application
import android.util.Log
import android.view.View
import androidx.lifecycle.MutableLiveData
import com.michael.cardgame.base.BaseViewModel
import com.michael.cardgame.bean.CardData
import com.michael.cardgame.constants.Constants.FOUR_OF_KIND
import com.michael.cardgame.constants.Constants.FULL_HOUSE
import com.michael.cardgame.constants.Constants.MINE
import com.michael.cardgame.constants.Constants.POKER_CLUBS
import com.michael.cardgame.constants.Constants.SINGLE
import com.michael.cardgame.constants.Constants.STRAIGHT_FLUSH
import com.michael.cardgame.constants.Constants.TWO_PAIR
import com.michael.cardgame.constants.Constants.USER_2
import com.michael.cardgame.constants.Constants.USER_3
import com.michael.cardgame.constants.Constants.USER_4
import com.michael.cardgame.tool.PokerLogicTool
import com.michael.cardgame.tool.Tool
import com.michael.cardgame.tool.Tool.convertDp
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.Collections
import java.util.Random
import java.util.concurrent.TimeUnit
import kotlin.math.min
import kotlin.math.sin

class MainViewModel(private val application: Application) : BaseViewModel(application) {

    val showPokerLiveData = MutableLiveData<CardData>()
    val bringPokerTogether = MutableLiveData<Pair<CardData, Float>>()
    val flipCardLiveData = MutableLiveData<CardData>()
    val moveSingleCardLiveData = MutableLiveData<CardData>()
    val moveBackSingleCardLiveData = MutableLiveData<Pair<Float, Float>>()
    val dealCardLiveData = MutableLiveData<Pair<CardData,Boolean>>()
    val handleMyCardTouchListenerLiveData = MutableLiveData<CardData>()
    val switchSingleCardLiveData = MutableLiveData<CardData>()
    val userCollectCardsLiveData = MutableLiveData<Pair<CardData,Int>>()
    val showInformationLiveData = MutableLiveData<String>()
    val startShowingUserCards = MutableLiveData<Pair<CardData,Boolean>>()
    val showMinePassButtonAndPlayCardButton = MutableLiveData<Int>()
    private val allCardList = Tool.getAllCardList()
    private var screenWidth = 0
    private var screenHeight = 0

    private var cardIndexFinishedCount = 0f
    private var singleCardOriginalX = 0f
    private var singleCardOriginalY = 0f
    private var user2LocationX = 0f
    private var user2LocationY = 0f
    private var user3LocationX = 0f
    private var user3LocationY = 0f
    private var user4LocationX = 0f
    private var user4LocationY = 0f
    private val user2CardList = mutableListOf<CardData>()
    private val user3CardList = mutableListOf<CardData>()
    private val user4CardList = mutableListOf<CardData>()
    private var dealCardNumber = 0
    private var currentNextUserNum = 0 //下一個換誰出
    private var currentCardType = 0 //目前出牌的組合
    private var currentPlayCardDataList = mutableListOf<CardData>() //目前所出的排組
    private var mineSelectedCardList = mutableListOf<CardData>() //我自己選的牌組
    fun startToFlow() {
        showMinePassButtonAndPlayCardButton.value = View.INVISIBLE
        startToShowRefreshCardAnimation()
    }

    private fun startToShowRefreshCardAnimation() {

        val startX = (screenWidth - (Tool.getCardWidth() + (30.convertDp() * 19))).toFloat() / 2
        val startY = (screenHeight - (Tool.getCardHeight() + (75.convertDp() * 4))).toFloat() / 2
        Log.i("Michael", "width $screenWidth height $screenHeight")
        var row = 0
        var position = 0
        for ((index, card) in allCardList.withIndex()) {
            if (index % 20 == 0) {
                row++
                position = 0
            }
            card.targetX = startX + 30.convertDp() * position
            card.targetY = startY + 75.convertDp() * row
            position++
        }
        mCompositeSubscription.add(
            Observable.interval(50, TimeUnit.MILLISECONDS)
                .zipWith(allCardList) { _, item -> item }
                .subscribeOn(Schedulers.io())
                .doOnComplete {
                    startToBringAllPokerTogether()
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { cardData ->
                    showPokerLiveData.value = cardData

                })
    }

    private fun startToBringAllPokerTogether() {
        mCompositeSubscription.add(
            Observable.interval(50, TimeUnit.MILLISECONDS)
                .zipWith(allCardList) { _, item -> item }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { cardData ->
                    bringPokerTogether.value = Pair(cardData, cardIndexFinishedCount)
                    cardIndexFinishedCount += 0.5f
                })
    }

    private fun pickRandomCard() {
        val randomSingleCard = allCardList[Random().nextInt(allCardList.size)]
        if (randomSingleCard.cardView == null) {
            return
        }
        singleCardOriginalX = randomSingleCard.cardView?.x!!
        singleCardOriginalY = randomSingleCard.cardView?.y!!
        moveSingleCardLiveData.value = randomSingleCard
    }

    fun setScreenWidthAndHeight(screenWidth: Int, screenHeight: Int) {
        this.screenWidth = screenWidth
        this.screenHeight = screenHeight
    }

    fun onDestroy() {
        clearCompositeDisposable()
    }

    fun onCheckBringCardTogetherFinishedListener(plusValue: Float) {
        if (plusValue == 25.5f) {
            pickRandomCard()
        }
    }

    fun flipSingleCardComplete() {
        moveBackSingleCardLiveData.value = Pair(singleCardOriginalX, singleCardOriginalY)
    }

    /**
     * 開始發牌
     */
    fun readyToDealCards(cardValue: Int?) {

        dealCardNumber = cardValue!!

        allCardList.shuffle()
        myCardList.clear()
        for (index in 0 until allCardList.size) {
            val cardData = allCardList[index]
            if (index == 0 || index % 4 == 0){
                myCardList.add(cardData)
            }
            if (index % 4 == 1){
                user2CardList.add(cardData)
            }
            if (index % 4 == 2){
                user3CardList.add(cardData)
            }
            if (index % 4 == 3){
                user4CardList.add(cardData)
            }
        }
        setUpOtherUserCard(user2CardList,user2LocationX,user2LocationY)
        setUpOtherUserCard(user3CardList,user3LocationX,user3LocationY)
        setUpOtherUserCard(user4CardList,user4LocationX,user4LocationY)


        refreshUsersCardList(user2CardList)
        refreshUsersCardList(user3CardList)
        refreshUsersCardList(user4CardList)
        refreshMyCardList()

    }

    private fun setUpOtherUserCard(
        cardList: MutableList<CardData>,
        locationX: Float,
        locationY: Float
    ) {
        for (card in cardList){
            card.targetX = locationX
            card.targetY = locationY
        }
    }


    private var dealCardIndex = 0
    private var myCardList = mutableListOf<CardData>()
    private var myIndex = 0
    private var user2Index = 0
    private var user3Index = 0
    private var user4Index = 0
    private var mineFirst = false
    private var user2First = false
    private var user3First = false
    private var user4First = false


    private fun dealCard() {
        if (dealCardIndex < allCardList.size) {
            dealCardLiveData.value = checkOrder()
            addIndex()
            dealCardIndex++
        } else {
            dealCardIndex = 0
            for (card in myCardList) {
                handleMyCardTouchListenerLiveData.value = card
            }
            user2Index = 0
            user3Index = 0
            user4Index = 0
            user2StartToCollectCards()
            user3StartToCollectCards()
            user4StartToCollectCards()
        }
    }
    //其他用戶開始蒐集他們的卡片
    private fun user2StartToCollectCards() {
        if (user2Index < user2CardList.size){
            userCollectCardsLiveData.value = Pair(user2CardList[user2Index],2)
            user2Index ++
        }else{
            user2Index = 0
        }
    }
    private fun user3StartToCollectCards() {
        if (user3Index < user3CardList.size){
            userCollectCardsLiveData.value = Pair(user3CardList[user3Index],3)
            user3Index ++
        }else{
            user3Index = 0
        }
    }
    private fun user4StartToCollectCards() {
        if (user4Index < user4CardList.size){
            userCollectCardsLiveData.value = Pair(user4CardList[user4Index],4)
            user4Index ++
        }else{
            user4Index = 0
            //走到這洗牌的流程結束
            startGame()
        }
    }

    private fun startGame() {
        showInformationLiveData.value = application.getString(R.string.show_three_of_club)
    }


    private fun addIndex() {
        if (mineFirst){
            mineFirst = false
            user4First = true
            myIndex ++
            return
        }
        if (user2First){
            user2First = false
            mineFirst = true
            user2Index ++
            return
        }
        if (user3First){
            user3First = false
            user2First = true
            user3Index ++
            return
        }
        if (user4First){
            user4First = false
            user3First = true
            user4Index ++
        }
    }

    private fun checkOrder(): Pair<CardData, Boolean> {
        if (mineFirst){
            return Pair(myCardList[myIndex],false)
        }
        if (user2First){
            return Pair(user2CardList[user2Index],true)
        }
        if (user3First){
            return Pair(user3CardList[user3Index],true)
        }
        return Pair(user4CardList[user4Index],true)
    }


    fun setAllUserLocation(
        topLeftX: Float,
        topRightX: Float,
        bottomRightX: Float,
        topY: Float,
        bottomY: Float
    ) {
        user2LocationX = topLeftX
        user2LocationY = topY
        user3LocationX = topRightX
        user3LocationY = topY
        user4LocationX = bottomRightX
        user4LocationY = bottomY
    }

    fun dealMyNextCard() {
        dealCard()
    }

    private fun refreshUsersCardList(userCardList: MutableList<CardData>){
        val cardList = userCardList.toMutableList()
        Collections.sort(cardList, Comparator { o1, o2 ->
            o1.cardValue - o2.cardValue
        })
        //尋找同花順
        val straightFlushList = PokerLogicTool.searchForStraightFlush(cardList)
        //剩餘卡片張數
        PokerLogicTool.countLeftCards(cardList, straightFlushList)
        //尋找鐵支
        val fourOfKindList = PokerLogicTool.searchForFourOfKind(cardList)
        //剩餘卡片張數
        PokerLogicTool.countLeftCards(cardList, fourOfKindList)
        //尋找葫蘆
        val fullHouseList = PokerLogicTool.searchForFullHouse(cardList, false)
        //剩餘卡片張數
        PokerLogicTool.countLeftCards(cardList, fullHouseList)
        //尋找兔胚
        val twoPairList = PokerLogicTool.searchTwoPair(cardList, false)
        //剩餘卡片張數
        PokerLogicTool.countLeftCards(cardList, twoPairList)

        //整合
        val arrangeAllCardList = mutableListOf<CardData>()
        arrangeAllCardList.addAll(cardList)
        arrangeAllCardList.addAll(twoPairList)
        arrangeAllCardList.addAll(fullHouseList)
        arrangeAllCardList.addAll(fourOfKindList)
        arrangeAllCardList.addAll(straightFlushList)
        userCardList.clear()
        userCardList.addAll(arrangeAllCardList)
    }

    private fun refreshMyCardList() {
        val cardList = myCardList.toMutableList()
        Collections.sort(cardList, Comparator { o1, o2 ->
            o1.cardValue - o2.cardValue
        })
        //尋找同花順
        val straightFlushList = PokerLogicTool.searchForStraightFlush(cardList)
        //剩餘卡片張數
        PokerLogicTool.countLeftCards(cardList, straightFlushList)
        //尋找鐵支
        val fourOfKindList = PokerLogicTool.searchForFourOfKind(cardList)
        //剩餘卡片張數
        PokerLogicTool.countLeftCards(cardList, fourOfKindList)
        //尋找葫蘆
        val fullHouseList = PokerLogicTool.searchForFullHouse(cardList, false)
        //剩餘卡片張數
        PokerLogicTool.countLeftCards(cardList, fullHouseList)
        //尋找兔胚
        val twoPairList = PokerLogicTool.searchTwoPair(cardList, false)
        //剩餘卡片張數
        PokerLogicTool.countLeftCards(cardList, twoPairList)

        //整合
        val arrangeAllCardList = mutableListOf<CardData>()
        arrangeAllCardList.addAll(cardList)
        arrangeAllCardList.addAll(twoPairList)
        arrangeAllCardList.addAll(fullHouseList)
        arrangeAllCardList.addAll(fourOfKindList)
        arrangeAllCardList.addAll(straightFlushList)
        myCardList.clear()
        myCardList = arrangeAllCardList.toMutableList()
        val startX = (screenWidth - (Tool.getCardWidth() + (40.convertDp() * 12))).toFloat() / 2
        val startY = screenHeight - Tool.getCardHeight()
        for ((index, cardData) in myCardList.withIndex()) {
            cardData.targetX = startX + 40.convertDp() * index
            cardData.targetY = startY.toFloat()
            cardData.sid = index + 1
        }
        mineFirst = dealCardNumber % 4 == 1
        user2First = dealCardNumber % 4 == 2
        user3First = dealCardNumber % 4 == 3
        user4First = dealCardNumber % 4 == 0
        Log.i("Poker","mine : $mineFirst , user2 $user2First , user3 $user3First , user4 $user4First")
        dealCard()


    }

    fun onCatchUserNum(userNum: Int) {
        when (userNum) {
            2 -> {
                user2StartToCollectCards()
            }
            3 -> {
                user3StartToCollectCards()
            }
            else -> {
                user4StartToCollectCards()
            }
        }
    }

    fun startPlayACard() {
        Log.i("Poker","開始出牌 尋找梅花三的玩家")
        var isMineTure = false
        for (card in myCardList){
            if (card.cardValue == 3 && card.cardType == POKER_CLUBS){
                isMineTure = true
                break
            }
        }
        if (isMineTure){
            Log.i("Poker","由我開始出")
            currentNextUserNum = USER_4
            return
        }
        var isUser2Turn = false
        for (card in user2CardList){
            if (card.cardValue == 3 && card.cardType == POKER_CLUBS){
                isUser2Turn = true
                break
            }
        }
        if (isUser2Turn){
            Log.i("Poker","由User2開始出")
            showUsersCard(user2CardList)
            playCardForFirstTime(USER_2)
            currentNextUserNum = MINE
            return
        }
        var isUser3Turn = false
        for (card in user3CardList){
            if (card.cardValue == 3 && card.cardType == POKER_CLUBS){
                isUser3Turn = true
                break
            }
        }
        if (isUser3Turn){

            Log.i("Poker","由User3開始出")
            showUsersCard(user3CardList)
            playCardForFirstTime(USER_3)
            currentNextUserNum = USER_2
            return
        }
        var isUser4Turn = false
        for (card in user4CardList){
            if (card.cardValue == 3 && card.cardType == POKER_CLUBS){
                isUser4Turn = true
                break
            }
        }
        if (isUser4Turn){
            Log.i("Poker","由User4開始出")
            showUsersCard(user4CardList)
            playCardForFirstTime(USER_4)
            currentNextUserNum = USER_3

        }
    }

    private fun getUserCardList(userNum: Int): MutableList<CardData> {
        return when(userNum){
            2 ->user2CardList
            3 ->user3CardList
            4 ->user4CardList
            else -> user4CardList
        }
    }

    private fun playCardForFirstTime(userNum: Int) {
        currentPlayCardDataList.clear()
        //尋找同花順且跟者梅花三
        val isStraightFlushWith3Clubs = PokerLogicTool.isStraightFlushWith3Clubs(getUserCardList(userNum))
        Log.i("Poker", "isStraightFlushWith3Clubs : $isStraightFlushWith3Clubs")
        if (isStraightFlushWith3Clubs){
            val straightFlushList = PokerLogicTool.searchForStraightFlush(getUserCardList(userNum))

            for ((index,card) in straightFlushList.withIndex()){
                card.targetX = getPlayCardTargetX(userNum,index)
                card.targetY = getPlayCardTargetY(userNum,index)
                startShowingUserCards.value = Pair(card,index == straightFlushList.size - 1)
            }
            PokerLogicTool.countLeftCards(getUserCardList(userNum),straightFlushList)
            currentPlayCardDataList.addAll(straightFlushList)
            currentCardType = STRAIGHT_FLUSH
            return
        }
        //尋找鐵支且跟者梅花三
        val isFourOfKindWith3Clubs = PokerLogicTool.isFourOfKindWith3Clubs(getUserCardList(userNum))
        Log.i("Poker", "isFourOfKindWith3Clubs : $isFourOfKindWith3Clubs")
        if (isFourOfKindWith3Clubs){
            val fourOfKindList = PokerLogicTool.searchForFourOfKind(getUserCardList(userNum))
            val singleCardWithOutThree = PokerLogicTool.getMinSingleCard(getUserCardList(userNum))
            startShowingUserCards.value = Pair(singleCardWithOutThree,false)
            for ((index,card) in fourOfKindList.withIndex()){
                if (card.cardValue == 3){
                    card.targetX = getPlayCardTargetX(userNum,index)
                    card.targetY = getPlayCardTargetY(userNum,index)
                    startShowingUserCards.value = Pair(card,index == fourOfKindList.size - 1)
                }
            }
            PokerLogicTool.countLeftCards(getUserCardList(userNum),fourOfKindList)
            currentPlayCardDataList.addAll(fourOfKindList)
            currentCardType = FOUR_OF_KIND
            return
        }
        //尋找葫蘆且跟者梅花三
        val isFullHouseWith3Clubs = PokerLogicTool.isFullHouseWith3Clubs(getUserCardList(userNum))
        Log.i("Poker", "isFullHouseWith3Clubs : $isFullHouseWith3Clubs")
        if (isFullHouseWith3Clubs){
            val fullHouseListWith3Clubs = PokerLogicTool.searchForFullHouse(getUserCardList(userNum),true)
            for ((index,card) in fullHouseListWith3Clubs.withIndex()){
                card.targetX = getPlayCardTargetX(userNum,index)
                card.targetY = getPlayCardTargetY(userNum,index)
                startShowingUserCards.value = Pair(card,index == fullHouseListWith3Clubs.size - 1)
            }
            PokerLogicTool.countLeftCards(getUserCardList(userNum),fullHouseListWith3Clubs)
            currentPlayCardDataList.addAll(fullHouseListWith3Clubs)
            currentCardType = FULL_HOUSE
            return
        }
        //尋找兔胚且跟者梅花三
        val isTwoPair = PokerLogicTool.isTwoPairWith3Clubs(getUserCardList(userNum))
        Log.i("Poker", "isTwoPair : $isTwoPair")
        if (isTwoPair){
            val twoPairListWith3Clubs = PokerLogicTool.searchTwoPair(getUserCardList(userNum),true)
            for ((index,card) in twoPairListWith3Clubs.withIndex()){
                card.targetX = getPlayCardTargetX(userNum,index)
                card.targetY = getPlayCardTargetY(userNum,index)
                startShowingUserCards.value = Pair(card,index == twoPairListWith3Clubs.size - 1)
            }
            PokerLogicTool.countLeftCards(getUserCardList(userNum),twoPairListWith3Clubs)
            currentPlayCardDataList.addAll(twoPairListWith3Clubs)
            currentCardType = TWO_PAIR
            return
        }
        val singleCardWithThree = PokerLogicTool.getMinSingleCardOnlyThreeClub(getUserCardList(userNum))
        Log.i("Poker", "singleCardWithThree")
        singleCardWithThree.targetX = getPlayCardTargetX(userNum,0)
        singleCardWithThree.targetY = getPlayCardTargetY(userNum,0)
        startShowingUserCards.value = Pair(singleCardWithThree,true)
        val list = mutableListOf<CardData>()
        list.add(singleCardWithThree)
        PokerLogicTool.countLeftCards(getUserCardList(userNum),list)
        currentPlayCardDataList.addAll(list)
        currentCardType = SINGLE
        
    }

    private fun getPlayCardTargetX(userNum: Int, index: Int): Float {
        return when(userNum){
            2 -> user2LocationX + 40.convertDp() * index
            3 -> user3LocationX - 40.convertDp() * index
            else -> user4LocationX - 40.convertDp() * index
        }
    }

    private fun getPlayCardTargetY(userNum: Int, index: Int): Float {
        return when(userNum){
            2 -> user2LocationY
            3 -> user3LocationY
            else -> user4LocationY
        }
    }

    /**
     * 單純把User的卡牌用Log印出來的方法
     */
    private fun showUsersCard(cardList: MutableList<CardData>) {
        var log = ""
        for (card in cardList){
            log += "cardValue : ${card.cardValue} , 花色 : ${Tool.getFlavor(card.cardType)}\n"
        }
        Log.i("Poker","user cardList : \n$log")
    }

    fun onPlayCardComplete() {
        mCompositeSubscription.add(Completable.timer(200,TimeUnit.MILLISECONDS)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {

                otherUserPlayCard()

            })
    }

    private fun otherUserPlayCard() {
        Log.i("Poker","準備發牌 : $currentNextUserNum")
        if (currentNextUserNum == MINE){
            Log.i("Poker","輪到我了")
            showMinePassButtonAndPlayCardButton.value = View.VISIBLE
            return
        }

        val cardList = searchForSpecificCard(getUserCardList(currentNextUserNum))
        if (cardList.isEmpty()){
            Log.i("Poker","user$currentNextUserNum : Pass")
            countNextPlayer()
            return
        }
        currentPlayCardDataList.clear()
        currentPlayCardDataList = cardList.toMutableList()
        for ((index,card) in cardList.withIndex()){
            card.targetX = getPlayCardTargetX(currentNextUserNum,index)
            card.targetY = getPlayCardTargetY(currentNextUserNum,index)
            startShowingUserCards.value = Pair(card,index == cardList.size - 1)
        }
        Log.i("Poker","user$currentNextUserNum 發牌")
        countNextPlayer()
        Log.i("Poker","下一位輪到 : $currentNextUserNum")
    }
    private fun countNextPlayer(){
        if (currentNextUserNum - 1 == 0){
            currentNextUserNum = USER_4
        }else{
            currentNextUserNum --
        }
    }

    private fun searchForSpecificCard(userCardList: MutableList<CardData>): MutableList<CardData> {
        if (currentCardType == SINGLE){
            if (PokerLogicTool.getMinSingleCardCompare(userCardList,currentPlayCardDataList) == null){
                return mutableListOf()
            }
            return mutableListOf(PokerLogicTool.getMinSingleCardCompare(userCardList,currentPlayCardDataList)!!)
        }

        return when(currentCardType){
            STRAIGHT_FLUSH -> PokerLogicTool.searchForStraightFlushCompare(userCardList,currentPlayCardDataList)
            FOUR_OF_KIND -> PokerLogicTool.searchForFourOfKindCompare(userCardList,currentPlayCardDataList)
            FULL_HOUSE -> PokerLogicTool.searchForFullHouseCompare(userCardList,currentPlayCardDataList)
            else -> PokerLogicTool.searchTwoPairCompare(userCardList,currentPlayCardDataList)
        }

    }

    fun onPlayMyCardClickListener() {
        val isAbleToPlayCard = Tool.compareMyCardAndCurrentCard(mineSelectedCardList,currentPlayCardDataList)
    }

    fun onPassClickListener() {

    }

    fun onCatchMineSelectedCard(it: CardData) {
        if (it.isSelected){
            mineSelectedCardList.add(it)
        }else{
            mineSelectedCardList.remove(it)
        }
        for (card in mineSelectedCardList){
            Log.i("Poker","cardNum : ${card.cardValue} 花色 : ${Tool.getFlavor(card.cardType)}")
        }
    }




}