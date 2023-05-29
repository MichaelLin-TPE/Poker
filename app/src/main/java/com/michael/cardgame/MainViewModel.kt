package com.michael.cardgame

import android.app.Application
import android.util.Log
import android.widget.ImageView
import androidx.lifecycle.MutableLiveData
import com.michael.cardgame.base.BaseViewModel
import com.michael.cardgame.bean.CardData
import com.michael.cardgame.constants.Constants
import com.michael.cardgame.constants.Constants.POKER_6
import com.michael.cardgame.constants.Constants.POKER_CLUBS
import com.michael.cardgame.tool.Tool
import com.michael.cardgame.tool.Tool.convertDp
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.Collections
import java.util.Random
import java.util.concurrent.TimeUnit
import java.util.stream.Collectors
import kotlin.math.abs

class MainViewModel(private val application: Application) : BaseViewModel(application) {

    val showPokerLiveData = MutableLiveData<CardData>()
    val bringPokerTogether = MutableLiveData<Pair<CardData, Float>>()
    val flipCardLiveData = MutableLiveData<CardData>()
    val moveSingleCardLiveData = MutableLiveData<CardData>()
    val moveBackSingleCardLiveData = MutableLiveData<Pair<Float,Float>>()
    val dealCardLiveData = MutableLiveData<CardData>()
    val handleMyCardTouchListenerLiveData = MutableLiveData<CardData>()
    val switchSingleCardLiveData = MutableLiveData<CardData>()
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
            Observable.interval(100, TimeUnit.MILLISECONDS)
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

    private fun startToFlipCard() {
        mCompositeSubscription.add(
            Observable.interval(100, TimeUnit.MILLISECONDS)
                .zipWith(allCardList) { _, item -> item }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { cardData ->
                    flipCardLiveData.value = cardData
                })
    }

    private fun startToBringAllPokerTogether() {
        mCompositeSubscription.add(
            Observable.interval(100, TimeUnit.MILLISECONDS)
                .zipWith(allCardList) { _, item -> item }
                .subscribeOn(Schedulers.io())
                .doOnComplete {
//                    allCardList.shuffle()
//                    pickRandomCard()
                }
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

    /**
     * 目前用不到
     */
    fun finishFlipCount() {
    }

    fun onCheckBringCardTogetherFinishedListener(plusValue: Float) {
        if (plusValue == 25.5f) {
            pickRandomCard()
        }
    }

    fun flipSingleCardComplete() {
        moveBackSingleCardLiveData.value = Pair(singleCardOriginalX,singleCardOriginalY)
    }

    /**
     * 開始發牌
     */
    fun readyToDealCards() {
        val startX = (screenWidth - (Tool.getCardWidth() + (30.convertDp() * 12))).toFloat() / 2
        val startY = screenHeight - Tool.getCardHeight()
        allCardList.shuffle()
        myCardList.clear()
        for (index in 0..12){
            val cardData = allCardList[index]
            cardData.targetX = startX + 30.convertDp() * index
            cardData.targetY = startY.toFloat()
            cardData.sid = index + 1
            myCardList.add(cardData)
        }


        dealCard()

    }
    private var dealCardIndex = 0
    private val myCardList = mutableListOf<CardData>()
    private fun dealCard() {
        if (dealCardIndex < myCardList.size){
            dealCardLiveData.value = myCardList[dealCardIndex]
            dealCardIndex ++
        }else{
            dealCardIndex = 0
            refreshMyCardList()


            for (card in myCardList){
                handleMyCardTouchListenerLiveData.value = card
            }
        }
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
    private var switchCardIndex = 0
    private var originalSid = 0
    fun setMovingCardLocationX(moveX: Float, cardData: CardData) {
        for ((index,myCard) in myCardList.withIndex()){
            if (abs((moveX - myCard.targetX).toInt()) <= 10){
                val movingCardTargetX = cardData.targetX
                originalSid = cardData.sid
                cardData.targetX = myCard.targetX
                myCard.targetX = movingCardTargetX
                switchSingleCardLiveData.value = myCard
                switchCardIndex = index + 1
            }
        }
    }

    private fun refreshMyCardList() {

        val list = mutableListOf<CardData>()
        list.add(CardData("3", R.drawable.ic_clubs,
            Constants.POKER_3,
            POKER_CLUBS,R.drawable.ic_clubs,0f,0f,null,0))
        list.add(CardData("3", R.drawable.ic_diamond,
            Constants.POKER_3,
            Constants.POKER_DIAMOND,R.drawable.ic_diamond,0f,0f,null,0))
        list.add(CardData("4", R.drawable.ic_clubs,
            Constants.POKER_4,
            POKER_CLUBS,R.drawable.ic_clubs,0f,0f,null,0))
        list.add(CardData("4", R.drawable.ic_diamond,
            Constants.POKER_4,
            Constants.POKER_DIAMOND,R.drawable.ic_diamond,0f,0f,null,0))
        list.add(CardData("4", R.drawable.ic_heart,
            Constants.POKER_4,
            Constants.POKER_HEART,R.drawable.ic_heart,0f,0f,null,0))
        list.add(CardData("4", R.drawable.ic_spades,
            Constants.POKER_4,
            Constants.POKER_SPADES,R.drawable.ic_spades,0f,0f,null,0))
        list.add(CardData("6", R.drawable.ic_clubs,
            POKER_6,
            POKER_CLUBS,R.drawable.ic_clubs,0f,0f,null,0))
        list.add(CardData("5", R.drawable.ic_clubs,
            Constants.POKER_5,
            POKER_CLUBS,R.drawable.ic_clubs,0f,0f,null,0))
        list.add(CardData("5", R.drawable.ic_diamond,
            Constants.POKER_5,
            Constants.POKER_DIAMOND,R.drawable.ic_diamond,0f,0f,null,0))
        list.add(CardData("7", R.drawable.ic_clubs,
            Constants.POKER_7, POKER_CLUBS,R.drawable.ic_clubs,0f,0f,null,0))
        list.add(CardData("7", R.drawable.ic_diamond,
            Constants.POKER_7,
            Constants.POKER_DIAMOND,R.drawable.ic_diamond,0f,0f,null,0))
        list.add(CardData("7", R.drawable.ic_heart,
            Constants.POKER_7,
            Constants.POKER_HEART,R.drawable.ic_heart,0f,0f,null,0))
        list.add(CardData("7", R.drawable.ic_spades,
            Constants.POKER_7,
            Constants.POKER_SPADES,R.drawable.ic_spades,0f,0f,null,0))

        val cardList = list.toMutableList()
        Collections.sort(cardList, Comparator { o1, o2 ->
            o1.cardValue - o2.cardValue
        })
        for (data in cardList){
            Log.i("Poker","card value : "+data.cardValue)
        }

        //尋找同花順
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
        for (data in straightFlushList){
            Log.i("Poker","straightFlushList value : ${data.cardValue} type : ${data.cardType}")
        }

        countLeftCards(cardList,straightFlushList)

        //尋找鐵支
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
        for (data in fourOfKindList){
            Log.i("Poker"," fourOfKindList value : ${data.cardValue} type : ${data.cardType}")
        }
        countLeftCards(cardList,fourOfKindList)



    }

    private fun countLeftCards(
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
        for (data in cardList){
            Log.i("Poker","left card value : "+data.cardValue + " type : ${data.cardType}")
        }
    }


}