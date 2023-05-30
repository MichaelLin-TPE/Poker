package com.michael.cardgame

import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.michael.cardgame.base.BaseViewModel
import com.michael.cardgame.bean.CardData
import com.michael.cardgame.tool.PokerLogicTool
import com.michael.cardgame.tool.Tool
import com.michael.cardgame.tool.Tool.convertDp
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.Collections
import java.util.Random
import java.util.concurrent.TimeUnit

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
    fun startToFlow() {
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
            otherUserStartToCollectTheirCards()
//            otherUserStartToCollectTheirCards(user3CardList, 3)
//            otherUserStartToCollectTheirCards(user4CardList, 4)
        }
    }
    //其他用戶開始蒐集他們的卡片
    fun otherUserStartToCollectTheirCards() {
        if (dealCardIndex < user2CardList.size){
            userCollectCardsLiveData.value = Pair(user2CardList[dealCardIndex],2)
            dealCardIndex ++
        }
    }

    private fun addIndex() {
        if (mineFirst){
            mineFirst = false
            user2First = true
            myIndex ++
            return
        }
        if (user2First){
            user2First = false
            user3First = true
            user2Index ++
            return
        }
        if (user3First){
            user3First = false
            user4First = true
            user3Index ++
            return
        }
        if (user4First){
            user4First = false
            mineFirst = true
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
        val fullHouseList = PokerLogicTool.searchForFullHouse(cardList)
        //剩餘卡片張數
        PokerLogicTool.countLeftCards(cardList, fullHouseList)
        //尋找兔胚
        val twoPairList = PokerLogicTool.searchTwoPair(cardList)
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


}