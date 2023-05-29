package com.michael.cardgame

import android.app.Application
import android.util.Log
import android.widget.ImageView
import androidx.lifecycle.MutableLiveData
import com.michael.cardgame.base.BaseViewModel
import com.michael.cardgame.bean.CardData
import com.michael.cardgame.tool.Tool
import com.michael.cardgame.tool.Tool.convertDp
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.Random
import java.util.concurrent.TimeUnit
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

    fun setMovingCardLocationX(moveX: Float, cardData: CardData) {
        for (myCard in myCardList){
            if (abs((moveX - myCard.targetX).toInt()) <= 10){
                val movingCardTargetX = cardData.targetX
                cardData.targetX = myCard.targetX
                myCard.targetX = movingCardTargetX
                switchSingleCardLiveData.value = myCard
            }
            myCard.cardView?.bringToFront()
        }
    }

    fun refreshMyCardList() {

    }


}