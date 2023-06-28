package com.michael.cardgame.big_two

import android.app.Application
import android.util.Log
import android.view.View
import androidx.lifecycle.MutableLiveData
import com.michael.cardgame.R
import com.michael.cardgame.base.BaseViewModel
import com.michael.cardgame.bean.CardData
import com.michael.cardgame.bean.LeftUserCardListData
import com.michael.cardgame.constants.Constants.FOUR_OF_KIND
import com.michael.cardgame.constants.Constants.FULL_HOUSE
import com.michael.cardgame.constants.Constants.MINE
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
import com.michael.cardgame.constants.Constants.SINGLE
import com.michael.cardgame.constants.Constants.STRAIGHT
import com.michael.cardgame.constants.Constants.STRAIGHT_FLUSH
import com.michael.cardgame.constants.Constants.TWO_PAIR
import com.michael.cardgame.constants.Constants.USER_2
import com.michael.cardgame.constants.Constants.USER_3
import com.michael.cardgame.constants.Constants.USER_4
import com.michael.cardgame.tool.PokerLogicTool
import com.michael.cardgame.tool.SpeechTool
import com.michael.cardgame.tool.Tool
import com.michael.cardgame.tool.Tool.convertDp
import com.michael.cardgame.tool.UserDataTool
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.Collections
import java.util.Random
import java.util.concurrent.TimeUnit

class BigTwoViewModel(private val application: Application) : BaseViewModel(application) {

    val showPokerLiveData = MutableLiveData<CardData>()
    val bringPokerTogether = MutableLiveData<Pair<CardData, Float>>()
    val flipCardLiveData = MutableLiveData<CardData>()
    val moveSingleCardLiveData = MutableLiveData<CardData>()
    val moveBackSingleCardLiveData = MutableLiveData<Pair<Float, Float>>()
    val dealCardLiveData = MutableLiveData<Pair<CardData, Boolean>>()
    val handleMyCardTouchListenerLiveData = MutableLiveData<CardData>()
    val switchSingleCardLiveData = MutableLiveData<CardData>()
    val userCollectCardsLiveData = MutableLiveData<Pair<CardData, Int>>()
    val showInformationLiveData = MutableLiveData<String>()
    val startShowingUserCards = MutableLiveData<Pair<CardData, Boolean>>()
    val showMinePassButtonAndPlayCardButton = MutableLiveData<Pair<Int, Int>>()
    val refreshMyCardsLiveData = MutableLiveData<CardData>()
    val bringAlreadyShowingCardTogetherLiveData = MutableLiveData<CardData>()
    val bringAllSelectedCardLiveData = MutableLiveData<CardData>()
    val showPassContentLiveData = MutableLiveData<Int>()
    val showUserLeftCardLiveData = MutableLiveData<Pair<Int, Int>>()
    val hideUserLeftCardLiveData = MutableLiveData<Int>()
    val showConfirmDialogLiveData = MutableLiveData<ArrayList<LeftUserCardListData>>()
    val removeAllCardLiveData = MutableLiveData<CardData>()
    val showUserPhotoLiveData = MutableLiveData<Int>()
    val showTotalAmount = MutableLiveData<Pair<Int,Int>>()
    val showUserName = MutableLiveData<Pair<String,Int>>()
    val playShufflingCardMusicLiveData = MutableLiveData<Boolean>()
    val playDealCardLiveData = MutableLiveData<Boolean>()
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
    private var currentPlayCardDataList = mutableListOf<CardData>() //目前玩家所發出去的排組
    private var mineSelectedCardList = mutableListOf<CardData>() //我自己選的牌組
    private var allAlreadyShowingCardList = mutableListOf<CardData>()
    private var passCount = 0 //計算本ROUND的Pass數量
    private var bot2Name = ""
    private var bot3Name = ""
    private var bot4Name = ""

    init {
        showUserPhotoLiveData.value = UserDataTool.getUserPhoto()

    }

    fun startToFlow() {
        showMinePassButtonAndPlayCardButton.value = Pair(View.GONE, View.GONE)
        showTotalAmount.value = Pair(UserDataTool.getUserCashAmount(),1)
        showTotalAmount.value = Pair(UserDataTool.getBot2CashAmount(),2)
        showTotalAmount.value = Pair(UserDataTool.getBot3CashAmount(),3)
        showTotalAmount.value = Pair(UserDataTool.getBot4CashAmount(),4)
        showUserName.value = Pair(UserDataTool.getUserName(),1)
        val nameList = Tool.getNameArray()
        bot2Name = nameList[Random().nextInt(nameList.size)]
        nameList.remove(bot2Name)
        bot3Name = nameList[Random().nextInt(nameList.size)]
        nameList.remove(bot3Name)
        bot4Name = nameList[Random().nextInt(nameList.size)]
        showUserName.value = Pair(bot2Name,2)
        showUserName.value = Pair(bot3Name,3)
        showUserName.value = Pair(bot4Name,4)

        Log.i("Poker","mine : ${UserDataTool.getUserCashAmount()} , bot2 : ${UserDataTool.getBot2CashAmount()} , bot3 : ${UserDataTool.getBot3CashAmount()} , bot4 : ${UserDataTool.getBot4CashAmount()}")
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
        playShufflingCardMusicLiveData.value = true
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
        cardIndexFinishedCount = 0f
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
        Log.i("Poker","width : $screenWidth height : $screenHeight")
    }

    fun onDestroy() {
        clearCompositeDisposable()
    }

    fun onCheckBringCardTogetherFinishedListener(plusValue: Float) {
        if (plusValue == 25.5f) {
            playShufflingCardMusicLiveData.value = false
            pickRandomCard()
        }
    }

    fun flipSingleCardComplete() {
        moveBackSingleCardLiveData.value = Pair(singleCardOriginalX, singleCardOriginalY)
    }

    private fun isForUser1(cardData: CardData): Boolean {
        return (cardData.cardValue == POKER_A && cardData.cardType == POKER_CLUBS) ||
                (cardData.cardValue == POKER_2 && cardData.cardType == POKER_DIAMOND) ||
                (cardData.cardValue == POKER_3 && cardData.cardType == POKER_HEART) ||
                (cardData.cardValue == POKER_4 && cardData.cardType == POKER_SPADES) ||
                (cardData.cardValue == POKER_5 && cardData.cardType == POKER_CLUBS) ||
                (cardData.cardValue == POKER_6 && cardData.cardType == POKER_DIAMOND) ||
                (cardData.cardValue == POKER_7 && cardData.cardType == POKER_HEART) ||
                (cardData.cardValue == POKER_8 && cardData.cardType == POKER_SPADES) ||
                (cardData.cardValue == POKER_9 && cardData.cardType == POKER_CLUBS) ||
                (cardData.cardValue == POKER_10 && cardData.cardType == POKER_DIAMOND) ||
                (cardData.cardValue == POKER_11 && cardData.cardType == POKER_HEART) ||
                (cardData.cardValue == POKER_12 && cardData.cardType == POKER_SPADES) ||
                (cardData.cardValue == POKER_13 && cardData.cardType == POKER_CLUBS)
    }

    private fun isForUser2(cardData: CardData): Boolean {

        return (cardData.cardValue == POKER_A && cardData.cardType == POKER_DIAMOND) ||
                (cardData.cardValue == POKER_2 && cardData.cardType == POKER_HEART) ||
                (cardData.cardValue == POKER_3 && cardData.cardType == POKER_SPADES) ||
                (cardData.cardValue == POKER_4 && cardData.cardType == POKER_CLUBS) ||
                (cardData.cardValue == POKER_5 && cardData.cardType == POKER_DIAMOND) ||
                (cardData.cardValue == POKER_6 && cardData.cardType == POKER_HEART) ||
                (cardData.cardValue == POKER_7 && cardData.cardType == POKER_SPADES) ||
                (cardData.cardValue == POKER_8 && cardData.cardType == POKER_CLUBS) ||
                (cardData.cardValue == POKER_9 && cardData.cardType == POKER_DIAMOND) ||
                (cardData.cardValue == POKER_10 && cardData.cardType == POKER_HEART) ||
                (cardData.cardValue == POKER_11 && cardData.cardType == POKER_SPADES) ||
                (cardData.cardValue == POKER_12 && cardData.cardType == POKER_CLUBS) ||
                (cardData.cardValue == POKER_13 && cardData.cardType == POKER_DIAMOND)
    }

    private fun isForUser3(cardData: CardData): Boolean {
        return (cardData.cardValue == POKER_A && cardData.cardType == POKER_HEART) ||
                (cardData.cardValue == POKER_2 && cardData.cardType == POKER_SPADES) ||
                (cardData.cardValue == POKER_3 && cardData.cardType == POKER_CLUBS) ||
                (cardData.cardValue == POKER_4 && cardData.cardType == POKER_DIAMOND) ||
                (cardData.cardValue == POKER_5 && cardData.cardType == POKER_HEART) ||
                (cardData.cardValue == POKER_6 && cardData.cardType == POKER_SPADES) ||
                (cardData.cardValue == POKER_7 && cardData.cardType == POKER_CLUBS) ||
                (cardData.cardValue == POKER_8 && cardData.cardType == POKER_DIAMOND) ||
                (cardData.cardValue == POKER_9 && cardData.cardType == POKER_HEART) ||
                (cardData.cardValue == POKER_10 && cardData.cardType == POKER_SPADES) ||
                (cardData.cardValue == POKER_11 && cardData.cardType == POKER_CLUBS) ||
                (cardData.cardValue == POKER_12 && cardData.cardType == POKER_DIAMOND) ||
                (cardData.cardValue == POKER_13 && cardData.cardType == POKER_HEART)

    }

    private fun isForUser4(cardData: CardData): Boolean {
        return (cardData.cardValue == POKER_A && cardData.cardType == POKER_SPADES) ||
                (cardData.cardValue == POKER_2 && cardData.cardType == POKER_CLUBS) ||
                (cardData.cardValue == POKER_3 && cardData.cardType == POKER_DIAMOND) ||
                (cardData.cardValue == POKER_4 && cardData.cardType == POKER_HEART) ||
                (cardData.cardValue == POKER_5 && cardData.cardType == POKER_SPADES) ||
                (cardData.cardValue == POKER_6 && cardData.cardType == POKER_CLUBS) ||
                (cardData.cardValue == POKER_7 && cardData.cardType == POKER_DIAMOND) ||
                (cardData.cardValue == POKER_8 && cardData.cardType == POKER_HEART) ||
                (cardData.cardValue == POKER_9 && cardData.cardType == POKER_SPADES) ||
                (cardData.cardValue == POKER_10 && cardData.cardType == POKER_CLUBS) ||
                (cardData.cardValue == POKER_11 && cardData.cardType == POKER_DIAMOND) ||
                (cardData.cardValue == POKER_12 && cardData.cardType == POKER_HEART) ||
                (cardData.cardValue == POKER_13 && cardData.cardType == POKER_SPADES)
    }

    /**
     * 開始發牌
     */
    fun readyToDealCards(cardValue: Int?) {

        dealCardNumber = cardValue!!

        allCardList.shuffle()
        myCardList.clear()
        user2CardList.clear()
        user3CardList.clear()
        user4CardList.clear()
        for (index in 0 until allCardList.size) {
            val cardData = allCardList[index]
//            if (isForUser1(cardData)) {
//                myCardList.add(cardData)
//            }
//            if (isForUser2(cardData)) {
//                user2CardList.add(cardData)
//            }
//            if (isForUser3(cardData)) {
//                user3CardList.add(cardData)
//            }
//            if (isForUser4(cardData)) {
//                user4CardList.add(cardData)
//            }
            if (index == 0 || index % 4 == 0) {
                myCardList.add(cardData)
            }
            if (index % 4 == 1) {
                user2CardList.add(cardData)
            }
            if (index % 4 == 2) {
                user3CardList.add(cardData)
            }
            if (index % 4 == 3) {
                user4CardList.add(cardData)
            }
        }
        setUpOtherUserCard(user2CardList, user2LocationX, user2LocationY)
        setUpOtherUserCard(user3CardList, user3LocationX, user3LocationY)
        setUpOtherUserCard(user4CardList, user4LocationX, user4LocationY)
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
        for (card in cardList) {
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
            playDealCardLiveData.value = true
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
        if (user2Index < user2CardList.size) {
            userCollectCardsLiveData.value = Pair(user2CardList[user2Index], 2)
            user2Index++
        } else {
            user2Index = 0
        }
    }

    private fun user3StartToCollectCards() {
        if (user3Index < user3CardList.size) {
            userCollectCardsLiveData.value = Pair(user3CardList[user3Index], 3)
            user3Index++
        } else {
            user3Index = 0
        }
    }

    private fun user4StartToCollectCards() {
        if (user4Index < user4CardList.size) {
            userCollectCardsLiveData.value = Pair(user4CardList[user4Index], 4)
            user4Index++
        } else {
            user4Index = 0
            showUserLeftCardLiveData.value = Pair(myCardList.size, MINE)
            showUserLeftCardLiveData.value = Pair(user2CardList.size, USER_2)
            showUserLeftCardLiveData.value = Pair(user3CardList.size, USER_3)
            showUserLeftCardLiveData.value = Pair(user4CardList.size, USER_4)
            //走到這洗牌的流程結束
            startGame()
        }
    }

    private fun startGame() {
        showInformationLiveData.value = application.getString(R.string.show_three_of_club)
        SpeechTool.makeSpeech(application.getString(R.string.show_three_of_club))
    }


    private fun addIndex() {
        if (mineFirst) {
            mineFirst = false
            user4First = true
            myIndex++
            return
        }
        if (user2First) {
            user2First = false
            mineFirst = true
            user2Index++
            return
        }
        if (user3First) {
            user3First = false
            user2First = true
            user3Index++
            return
        }
        if (user4First) {
            user4First = false
            user3First = true
            user4Index++
        }
    }

    private fun checkOrder(): Pair<CardData, Boolean> {
        if (mineFirst) {
            return Pair(myCardList[myIndex], false)
        }
        if (user2First) {
            return Pair(user2CardList[user2Index], true)
        }
        if (user3First) {
            return Pair(user3CardList[user3Index], true)
        }
        return Pair(user4CardList[user4Index], true)
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

    private fun refreshUsersCardList(userCardList: MutableList<CardData>) {
        val cardList = userCardList.toMutableList()
        Collections.sort(cardList, Comparator { o1, o2 ->
            o1.cardValue - o2.cardValue
        })
        //尋找同花順
        val findStraightList = PokerLogicTool.searchForStraightFlushNew(cardList)
        val straightFlushList = mutableListOf<CardData>()
        for (findList in findStraightList) {
            straightFlushList.addAll(findList)
        }
        val straightIterator = findStraightList.iterator()
        while (straightIterator.hasNext()) {
            val straightList = straightIterator.next()
            //剩餘卡片張數
            PokerLogicTool.countLeftCards(cardList, straightList)
        }
        //尋找鐵支
        val fourOfKindLists = PokerLogicTool.searchForFourOfKindNew(cardList)
        val fourOfKindList = mutableListOf<CardData>()
        for (fourList in fourOfKindLists) {
            fourOfKindList.addAll(fourList)
        }
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
        val findStraightList = PokerLogicTool.searchForStraightFlushNew(cardList)
        val straightFlushList = mutableListOf<CardData>()
        for (findList in findStraightList) {
            straightFlushList.addAll(findList)
        }
        val straightIterator = findStraightList.iterator()
        while (straightIterator.hasNext()) {
            val straightList = straightIterator.next()
            //剩餘卡片張數
            PokerLogicTool.countLeftCards(cardList, straightList)
        }
        //剩餘卡片張數
        PokerLogicTool.countLeftCards(cardList, straightFlushList)
        //尋找鐵支
        val fourOfKindLists = PokerLogicTool.searchForFourOfKindNew(cardList)
        val fourOfKindList = mutableListOf<CardData>()
        for (fourList in fourOfKindLists) {
            fourOfKindList.addAll(fourList)
        }
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
        val startX = (screenWidth - (Tool.getCardWidth() + (30.convertDp() * 12))).toFloat() / 2
        val startY = screenHeight - Tool.getCardHeight()
        for ((index, cardData) in myCardList.withIndex()) {
            cardData.targetX = startX + 30.convertDp() * index
            cardData.targetY = startY.toFloat()
            cardData.sid = index + 1
        }
        mineFirst = dealCardNumber % 4 == 1
        user2First = dealCardNumber % 4 == 2
        user3First = dealCardNumber % 4 == 3
        user4First = dealCardNumber % 4 == 0
        Log.i(
            "Poker",
            "mine : $mineFirst , user2 $user2First , user3 $user3First , user4 $user4First"
        )
        dealCardIndex = 0
        myIndex = 0
        user2Index = 0
        user3Index = 0
        user4Index = 0
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

    private var isThreeOfClubInMyHand = false

    fun startPlayACard() {
        Log.i("Poker", "開始出牌 尋找梅花三的玩家")
        var isMineTure = false
        for (card in myCardList) {
            if (card.cardValue == 3 && card.cardType == POKER_CLUBS) {
                isMineTure = true
                break
            }
        }
        if (isMineTure) {
            isThreeOfClubInMyHand = true
            Log.i("Poker", "由我開始出")
            currentNextUserNum = MINE
            showMinePassButtonAndPlayCardButton.value = Pair(View.VISIBLE, View.GONE)
            return
        }
        var isUser2Turn = false
        for (card in user2CardList) {
            if (card.cardValue == 3 && card.cardType == POKER_CLUBS) {
                isUser2Turn = true
                break
            }
        }
        if (isUser2Turn) {
            Log.i("Poker", "由User2開始出")
            currentNextUserNum = USER_2
            showUsersCard(user2CardList)
            playCardForFirstTime(USER_2)
            return
        }
        var isUser3Turn = false
        for (card in user3CardList) {
            if (card.cardValue == 3 && card.cardType == POKER_CLUBS) {
                isUser3Turn = true
                break
            }
        }
        if (isUser3Turn) {
            currentNextUserNum = USER_3
            Log.i("Poker", "由User3開始出")
            showUsersCard(user3CardList)
            playCardForFirstTime(USER_3)
            return
        }
        var isUser4Turn = false
        for (card in user4CardList) {
            if (card.cardValue == 3 && card.cardType == POKER_CLUBS) {
                isUser4Turn = true
                break
            }
        }
        if (isUser4Turn) {
            currentNextUserNum = USER_4
            Log.i("Poker", "由User4開始出")
            showUsersCard(user4CardList)
            playCardForFirstTime(USER_4)

        }
    }

    private fun getUserCardList(userNum: Int): MutableList<CardData> {
        return when (userNum) {
            2 -> user2CardList
            3 -> user3CardList
            4 -> user4CardList
            else -> user4CardList
        }
    }

    private fun playCardForFirstTime(userNum: Int) {
        currentPlayCardDataList.clear()
        //尋找同花順且跟者梅花三
        val isStraightFlushWith3Clubs =
            PokerLogicTool.isStraightFlushWith3Clubs(getUserCardList(userNum))
        Log.i("Poker", "isStraightFlushWith3Clubs : $isStraightFlushWith3Clubs")
        if (isStraightFlushWith3Clubs) {
            val straightList = PokerLogicTool.searchForStraightFlushNew(getUserCardList(userNum))
            val straightFlushList = mutableListOf<CardData>()
            for ((index, cardList) in straightList.withIndex()) {
                for (card in cardList) {
                    if (card.cardValue == POKER_3 && card.cardType == POKER_CLUBS) {
                        straightFlushList.addAll(straightList[index])
                        break
                    }
                }
                if (straightFlushList.isNotEmpty()) {
                    break
                }
            }
            straightFlushList.reverse()
            showCardInfo(straightFlushList)
            countNextPlayer()
            for ((index, card) in straightFlushList.withIndex()) {
                card.targetX = getPlayCardTargetX(index, straightFlushList.size)
                card.targetY = getPlayCardTargetY(straightFlushList.size)
                startShowingUserCards.value = Pair(card, index == straightFlushList.size - 1)
            }
            passCount = 0
            PokerLogicTool.countLeftCards(getUserCardList(userNum), straightFlushList)
            showUserLeftCardLiveData.value =
                Pair(getUserCardList(userNum).size, userNum)
            currentPlayCardDataList.addAll(straightFlushList)
            allAlreadyShowingCardList.addAll(currentPlayCardDataList)
            currentCardType = STRAIGHT_FLUSH
            makeItSpeech()
            return
        }
        //尋找鐵支且跟者梅花三
        val isFourOfKindWith3Clubs = PokerLogicTool.isFourOfKindWith3Clubs(getUserCardList(userNum))
        Log.i("Poker", "isFourOfKindWith3Clubs : $isFourOfKindWith3Clubs")
        if (isFourOfKindWith3Clubs) {
            val fourOfKindLists = PokerLogicTool.searchForFourOfKindNew(getUserCardList(userNum))
            val fourOfKindList = mutableListOf<CardData>()
            for (list in fourOfKindLists) {
                if (list[0].cardValue == POKER_3) {
                    fourOfKindList.addAll(list)
                    break
                }
            }
            val singleCardWithOutThree = PokerLogicTool.getMinSingleCard(getUserCardList(userNum))
            fourOfKindList.add(singleCardWithOutThree)
            showCardInfo(fourOfKindList)
            countNextPlayer()
            for ((index, card) in fourOfKindList.withIndex()) {
                card.targetX = getPlayCardTargetX(index + 1,fourOfKindList.size)
                card.targetY = getPlayCardTargetY(fourOfKindList.size)
                startShowingUserCards.value = Pair(card, index == fourOfKindList.size - 1)
            }
            passCount = 0
            PokerLogicTool.countLeftCards(getUserCardList(userNum), fourOfKindList)
            showUserLeftCardLiveData.value =
                Pair(getUserCardList(userNum).size, userNum)
            currentPlayCardDataList.addAll(fourOfKindList)
            allAlreadyShowingCardList.addAll(currentPlayCardDataList)
            currentCardType = FOUR_OF_KIND
            makeItSpeech()
            return
        }
        //尋找葫蘆且跟者梅花三
        val isFullHouseWith3Clubs = PokerLogicTool.isFullHouseWith3Clubs(getUserCardList(userNum))
        Log.i("Poker", "isFullHouseWith3Clubs : $isFullHouseWith3Clubs")
        if (isFullHouseWith3Clubs) {
            val fullHouseListWith3Clubs =
                PokerLogicTool.searchForFullHouse(getUserCardList(userNum), true)
            showCardInfo(fullHouseListWith3Clubs)
            countNextPlayer()
            for ((index, card) in fullHouseListWith3Clubs.withIndex()) {
                card.targetX = getPlayCardTargetX(index, fullHouseListWith3Clubs.size)
                card.targetY = getPlayCardTargetY(fullHouseListWith3Clubs.size)
                startShowingUserCards.value = Pair(card, index == fullHouseListWith3Clubs.size - 1)
            }
            passCount = 0
            PokerLogicTool.countLeftCards(getUserCardList(userNum), fullHouseListWith3Clubs)
            showUserLeftCardLiveData.value =
                Pair(getUserCardList(userNum).size, userNum)
            currentPlayCardDataList.addAll(fullHouseListWith3Clubs)
            allAlreadyShowingCardList.addAll(currentPlayCardDataList)
            currentCardType = FULL_HOUSE
            makeItSpeech()
            return
        }
        //尋找順子且有梅花三
        val isStraightWith3Clubs = PokerLogicTool.isStraightWith3Clubs(getUserCardList(userNum))
        Log.i("Poker", "isStraightFlushWith3Clubs : $isStraightFlushWith3Clubs")
        if (isStraightWith3Clubs) {
            val straightList = PokerLogicTool.searchForStraightNew(getUserCardList(userNum))
            val straightFlushList = mutableListOf<CardData>()
            for ((index, cardList) in straightList.withIndex()) {
                for (card in cardList) {
                    if (card.cardValue == POKER_3 && card.cardType == POKER_CLUBS) {
                        straightFlushList.addAll(straightList[index])
                        break
                    }
                }
                if (straightFlushList.isNotEmpty()) {
                    break
                }
            }
            straightFlushList.reverse()
            showCardInfo(straightFlushList)
            countNextPlayer()
            for ((index, card) in straightFlushList.withIndex()) {
                card.targetX = getPlayCardTargetX(index, straightFlushList.size)
                card.targetY = getPlayCardTargetY(straightFlushList.size)
                startShowingUserCards.value = Pair(card, index == straightFlushList.size - 1)
            }
            passCount = 0
            PokerLogicTool.countLeftCards(getUserCardList(userNum), straightFlushList)
            showUserLeftCardLiveData.value =
                Pair(getUserCardList(userNum).size, userNum)
            currentPlayCardDataList.addAll(straightFlushList)
            allAlreadyShowingCardList.addAll(currentPlayCardDataList)
            currentCardType = STRAIGHT
            makeItSpeech()
            return
        }

        //尋找兔胚且跟者梅花三
        val isTwoPair = PokerLogicTool.isTwoPairWith3Clubs(getUserCardList(userNum))
        Log.i("Poker", "isTwoPair : $isTwoPair")
        if (isTwoPair) {
            val twoPairListWith3Clubs = PokerLogicTool.searchTwoPair(getUserCardList(userNum), true)
            showCardInfo(twoPairListWith3Clubs)
            countNextPlayer()
            for ((index, card) in twoPairListWith3Clubs.withIndex()) {
                card.targetX = getPlayCardTargetX(index, twoPairListWith3Clubs.size)
                card.targetY = getPlayCardTargetY(twoPairListWith3Clubs.size)
                startShowingUserCards.value = Pair(card, index == twoPairListWith3Clubs.size - 1)
            }
            passCount = 0
            PokerLogicTool.countLeftCards(getUserCardList(userNum), twoPairListWith3Clubs)
            showUserLeftCardLiveData.value =
                Pair(getUserCardList(userNum).size, userNum)
            currentPlayCardDataList.addAll(twoPairListWith3Clubs)
            allAlreadyShowingCardList.addAll(currentPlayCardDataList)
            currentCardType = TWO_PAIR
            makeItSpeech()
            return
        }
        val singleCardWithThree =
            PokerLogicTool.getMinSingleCardOnlyThreeClub(getUserCardList(userNum))
        Log.i("Poker", "singleCardWithThree")
        val list = mutableListOf<CardData>()
        list.add(singleCardWithThree)
        showCardInfo(list)
        countNextPlayer()
        singleCardWithThree.targetX = getPlayCardTargetX(0, 1)
        singleCardWithThree.targetY = getPlayCardTargetY(1)
        startShowingUserCards.value = Pair(singleCardWithThree, true)
        passCount = 0
        PokerLogicTool.countLeftCards(getUserCardList(userNum), list)
        showUserLeftCardLiveData.value =
            Pair(getUserCardList(userNum).size, userNum)
        currentPlayCardDataList.addAll(list)
        allAlreadyShowingCardList.addAll(currentPlayCardDataList)
        currentCardType = SINGLE
        makeItSpeech()
        showCardInfo(list)

    }

    private fun showCardInfo(cardList: MutableList<CardData>) {
        for (card in cardList) {
            Log.i(
                "Poker",
                "user$currentNextUserNum 發牌 : ${card.cardValue} 花色 : ${Tool.getFlavor(card.cardType)}"
            )
        }
    }

    private fun getPlayCardTargetX(index: Int, cardCount: Int): Float {

        return ((screenWidth - (Tool.getCardWidth() * cardCount)) / 2f) + (Tool.getCardWidth() * index)

//        return when (userNum) {
//            2 -> user2LocationX + 40.convertDp() * index
//            3 -> user3LocationX - 40.convertDp() * index
//            else -> user4LocationX - 40.convertDp() * index
//        }
    }

    private fun getPlayCardTargetY(cardCount:Int): Float {

        return (screenHeight - Tool.getCardHeight()) / 2f

//        return when (userNum) {
//            2 -> user2LocationY
//            3 -> user3LocationY
//            else -> user4LocationY
//        }
    }

    /**
     * 單純把User的卡牌用Log印出來的方法
     */
    private fun showUsersCard(cardList: MutableList<CardData>) {
        var log = ""
        for (card in cardList) {
            log += "cardValue : ${card.cardValue} , 花色 : ${Tool.getFlavor(card.cardType)}\n"
        }
        Log.i("Poker", "user cardList : \n$log")
    }

    fun onPlayCardComplete() {
        mCompositeSubscription.add(Completable.timer(1000, TimeUnit.MILLISECONDS)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {

                otherUserPlayCard()

            })
    }

    private fun otherUserPlayCard() {
        if (myCardList.isEmpty()) {
            Tool.showToast(application.getString(R.string.winner) + " : ${UserDataTool.getUserName()}")
            SpeechTool.makeSpeech(application.getString(R.string.winner) + " : ${UserDataTool.getUserName()}")
            showConfirmDialog()
            return
        }
        if (user2CardList.isEmpty()) {
            Tool.showToast(application.getString(R.string.winner) + " : $bot2Name")
            SpeechTool.makeSpeech(application.getString(R.string.winner) + " : $bot2Name")
            showConfirmDialog()
            return
        }
        if (user3CardList.isEmpty()) {
            Tool.showToast(application.getString(R.string.winner) + " : $bot3Name")
            SpeechTool.makeSpeech(application.getString(R.string.winner) + " : $bot3Name")
            showConfirmDialog()
            return
        }
        if (user4CardList.isEmpty()) {
            Tool.showToast(application.getString(R.string.winner) + " : $bot4Name")
            SpeechTool.makeSpeech(application.getString(R.string.winner) + " : $bot4Name")
            showConfirmDialog()
            return
        }
        if (currentNextUserNum == MINE) {
            Log.i("Poker", "輪到我了 pass count : $passCount")
            if (passCount == 3) {
                showMinePassButtonAndPlayCardButton.value = Pair(View.VISIBLE, View.GONE)
            } else {
                showMinePassButtonAndPlayCardButton.value = Pair(View.VISIBLE, View.VISIBLE)
            }
            checkPassCount()
            return
        }
        checkPassCount();
        val cardList = searchForSpecificCard(getUserCardList(currentNextUserNum))
        if (cardList.isEmpty()) {
            showPassContentLiveData.value = currentNextUserNum
            doPass()
            SpeechTool.makeSpeech("Pass")
            Log.i("Poker", "user$currentNextUserNum : Pass count : $passCount")
            countNextPlayer()
            onPlayCardComplete()
            return
        }

        currentPlayCardDataList.clear()
        currentPlayCardDataList = cardList.toMutableList()
        makeItSpeech()
        cardList.sortWith { o1, o2 ->
            o1.cardValue - o2.cardValue
        }
        playDealCardLiveData.value = true
        for ((index, card) in cardList.withIndex()) {
            card.targetX = getPlayCardTargetX(index, cardList.size)
            card.targetY = getPlayCardTargetY(cardList.size)
            startShowingUserCards.value = Pair(card, index == cardList.size - 1)
        }
        passCount = 0
        allAlreadyShowingCardList.addAll(currentPlayCardDataList)
        PokerLogicTool.countLeftCards(getUserCardList(currentNextUserNum), cardList)
        Log.i(
            "Poker",
            "user$currentNextUserNum 剩餘的卡牌數量 : ${getUserCardList(currentNextUserNum).size}"
        )
        showUserLeftCardLiveData.value =
            Pair(getUserCardList(currentNextUserNum).size, currentNextUserNum)
        countNextPlayer()
    }

    private fun makeItSpeech() {
        when(currentCardType){
            STRAIGHT_FLUSH->SpeechTool.makeSpeech("同花順")
            FOUR_OF_KIND ->SpeechTool.makeSpeech("鐵支")
            FULL_HOUSE->fullHouseSpeech()
            STRAIGHT->SpeechTool.makeSpeech("順子")
            TWO_PAIR->twoPairSpeech()
            SINGLE->singleSpeech()
        }
    }

    private fun singleSpeech() {
        val num = currentPlayCardDataList[0].cardValue
        if (num == POKER_A){
            SpeechTool.makeSpeech("Ace")
        }else{
            SpeechTool.makeSpeech("$num")
        }
    }

    private fun twoPairSpeech() {
        val num = currentPlayCardDataList[0].cardValue
        if (num == POKER_A){
            SpeechTool.makeSpeech("Ace胚")
        }else{
            SpeechTool.makeSpeech("${num}胚")
        }

    }

    private fun fullHouseSpeech() {
        val num = PokerLogicTool.checkFullHouseNum(currentPlayCardDataList)
        if (num == POKER_A){
            SpeechTool.makeSpeech("Ace葫蘆")
        }else{
            SpeechTool.makeSpeech("${num}葫蘆")
        }

    }

    private fun showConfirmDialog() {
        val list = ArrayList<LeftUserCardListData>()

        list.add(LeftUserCardListData(myCardList, MINE,UserDataTool.getUserName()))

        list.add(LeftUserCardListData(user2CardList, USER_2,bot2Name))

        list.add(LeftUserCardListData(user3CardList, USER_3,bot3Name))

        list.add(LeftUserCardListData(user4CardList, USER_4,bot4Name))

        showConfirmDialogLiveData.value = list
    }

    private fun checkPassCount() {
        if (passCount >= 3) {
            bringAlreadyShowCardToCenter()
            currentPlayCardDataList.clear()
            passCount = 0
        }
    }

    private fun bringAlreadyShowCardToCenter() {
        for (card in allAlreadyShowingCardList) {
            bringAlreadyShowingCardTogetherLiveData.value = card
        }
    }

    private fun doPass() {
        passCount++
    }

    private fun countNextPlayer() {
        if (currentNextUserNum - 1 == 0) {
            currentNextUserNum = USER_4
        } else {
            currentNextUserNum--
        }
    }

    private fun searchForSpecificCard(userCardList: MutableList<CardData>): MutableList<CardData> {

        if (currentPlayCardDataList.isEmpty()) { //全PASS才會走這
            Log.i("Poker", "all pass 發牌")
            val straightFlushList = PokerLogicTool.searchForStraightFlushNew(userCardList)
            if (straightFlushList.isNotEmpty()) {
                Log.i("Poker", "user${currentNextUserNum} 發同花順")
                currentCardType = STRAIGHT_FLUSH
                return straightFlushList[Random().nextInt(straightFlushList.size)]
            }
            val fourList = PokerLogicTool.searchForFourOfKindNew(userCardList)
            if (fourList.isNotEmpty()) {
                Log.i("Poker", "user${currentNextUserNum} 發鐵支")
                currentCardType = FOUR_OF_KIND
                val fourOfKindList = mutableListOf<CardData>()
                fourOfKindList.addAll(fourList[0])
                fourOfKindList.add(PokerLogicTool.getMinSingleCard(userCardList))
                return fourOfKindList
            }
            val fullHouseList = PokerLogicTool.searchForFullHouse(userCardList, false)
            if (fullHouseList.isNotEmpty()) {
                Log.i("Poker", "user${currentNextUserNum} 發葫蘆")
                currentCardType = FULL_HOUSE
                return fullHouseList
            }
            val straightList = PokerLogicTool.searchForStraightNew(userCardList)
            if (straightList.isNotEmpty()) {
                Log.i("Poker", "user${currentNextUserNum} 發順子")
                currentCardType = STRAIGHT
                return straightList[Random().nextInt(straightList.size)]
            }

            val twoPairList = PokerLogicTool.playTwoPair(userCardList)
            if (twoPairList.isNotEmpty()) {
                Log.i("Poker", "user${currentNextUserNum} 發兔胚")
                currentCardType = TWO_PAIR
                return twoPairList
            }
            Log.i("Poker", "user${currentNextUserNum} 發單張")
            currentCardType = SINGLE

            val singleCard = PokerLogicTool.searchForSingleCard(userCardList)
            singleCard.sortWith{ o1,o2->
                o1.cardValue - o2.cardValue
            }
            if (singleCard.size == 1 && singleCard[0].cardValue == POKER_2){
                return singleCard
            }
            var card = singleCard[0]
            if (card.cardValue == POKER_2){
                card = singleCard[1]
            }
            return mutableListOf(card)

        }
        if (currentCardType == STRAIGHT_FLUSH) {
            return PokerLogicTool.searchForStraightFlushCompare(
                userCardList,
                currentPlayCardDataList
            )
        }
        if (currentCardType == FOUR_OF_KIND) {
            return PokerLogicTool.searchForFourOfKindCompare(
                userCardList,
                currentPlayCardDataList
            )
        }
        if ((myCardList.size <= 8 || user2CardList.size <= 8 || user3CardList.size <= 8 || user4CardList.size <= 8)) {
            val straightFlush = PokerLogicTool.searchForStraightFlushNew(userCardList)
            if (straightFlush.isNotEmpty()) {
                currentCardType = STRAIGHT_FLUSH
                return straightFlush[Random().nextInt(straightFlush.size)]
            }
            val fourOfKind = PokerLogicTool.searchForFourOfKindNew(userCardList)

            if (fourOfKind.isNotEmpty()) {
                currentCardType = FOUR_OF_KIND
                val list = fourOfKind[Random().nextInt(fourOfKind.size)]
                list.add(PokerLogicTool.getMinCard(userCardList))
                return list
            }
        }

        Log.i("Poker", "目前的需要發的卡牌組為 : ${Tool.getCardType(currentCardType)}")
        if (currentCardType == SINGLE) {
            if (PokerLogicTool.getMinSingleCardCompare(
                    userCardList,
                    currentPlayCardDataList
                ) == null
            ) {
                return mutableListOf()
            }
            return mutableListOf(
                PokerLogicTool.getMinSingleCardCompare(
                    userCardList,
                    currentPlayCardDataList
                )!!
            )
        }

        return when (currentCardType) {
            FULL_HOUSE -> PokerLogicTool.searchForFullHouseCompare(
                userCardList,
                currentPlayCardDataList
            )

            STRAIGHT -> PokerLogicTool.searchForStraightCompare(
                userCardList,
                currentPlayCardDataList
            )

            else -> PokerLogicTool.searchTwoPairCompare(userCardList, currentPlayCardDataList)
        }

    }

    fun onPlayMyCardClickListener() {
        if (passCount == 3) {
            currentPlayCardDataList.clear()
        }
        Log.i("Poker","要出幾張牌 : ${mineSelectedCardList.size}")
        val isAbleToPlayCard = PokerLogicTool.compareMyCardAndCurrentCard(
            mineSelectedCardList,
            currentPlayCardDataList
        )
        if (!isAbleToPlayCard) {
            showErrorMsg(application.getString(R.string.can_not_play_card))
            return
        }
        if (isThreeOfClubInMyHand) {
            var isHasThreeOfClub = false
            for (data in mineSelectedCardList) {
                if (data.cardValue == POKER_3) {
                    isHasThreeOfClub = true
                }
            }
            if (!isHasThreeOfClub) {
                showErrorMsg(application.getString(R.string.need_three_club))
                return
            }
            isThreeOfClubInMyHand = false
        }

        showMinePassButtonAndPlayCardButton.value = Pair(View.GONE, View.GONE)
        currentCardType = PokerLogicTool.checkCardsType(mineSelectedCardList)
        currentPlayCardDataList = mineSelectedCardList.toMutableList()
        allAlreadyShowingCardList.addAll(currentPlayCardDataList)
        makeItSpeech()
        countNextPlayer()
        Log.i("Poker", "我出的手排數量 size : ${mineSelectedCardList.size}")
        playDealCardLiveData.value = true
        for ((index, card) in mineSelectedCardList.withIndex()) {
            card.targetX = getPlayCardTargetX(index, mineSelectedCardList.size)
            card.targetY = getPlayCardTargetY(mineSelectedCardList.size)
            Log.i(
                "Poker",
                "index == mineSelectedCardList.size - 1 , index : $index , ${index == mineSelectedCardList.size - 1}"
            )
            startShowingUserCards.value = Pair(card, index == mineSelectedCardList.size - 1)
        }
        passCount = 0
        doRefreshMyCard()
        Log.i("Poker", "我剩餘的卡牌數量 : ${myCardList.size}")
        showUserLeftCardLiveData.value = Pair(myCardList.size, MINE)
    }

    /**
     * 重新整理我的牌組
     */
    private fun doRefreshMyCard() {
        val indexArray = mutableListOf<Int>()
        for ((myCardIndex, myCard) in myCardList.withIndex()) {
            for (card in mineSelectedCardList) {
                if (card.cardValue == myCard.cardValue && card.cardType == myCard.cardType) {
                    indexArray.add(myCardIndex)
                }
            }
        }
        for ((i, index) in indexArray.withIndex()) {
            for (pos in index + 1 until myCardList.size) {
                if ((i + 1) < indexArray.size && indexArray[i + 1] == pos) {
                    continue
                }
                val cardData = myCardList[pos]
                if (cardData.isSelected) {
                    continue
                }
                cardData.targetX = cardData.targetX - 30.convertDp()
                refreshMyCardsLiveData.value = cardData
            }
        }
        val list = mutableListOf<CardData>()
        for (index in indexArray) {
            list.add(myCardList[index])
        }
        val iterator = myCardList.iterator()
        while (iterator.hasNext()) {
            val data = iterator.next()
            for (card in list) {
                if (data.cardValue == card.cardValue && data.cardType == card.cardType) {
                    iterator.remove()
                }
            }
        }


        mineSelectedCardList.clear()
    }

    fun onPassClickListener() {
        SpeechTool.makeSpeech("Pass")
        doPass()
        countNextPlayer()
        onPlayCardComplete()
        showMinePassButtonAndPlayCardButton.value = Pair(View.GONE, View.GONE)
        for (data in mineSelectedCardList) {
            for (card in myCardList) {
                if (data.cardValue == card.cardValue && data.cardType == card.cardType) {
                    data.isSelected = false
                    bringAllSelectedCardLiveData.value = data
                }
            }
        }
        mineSelectedCardList.clear()
    }

    fun onCatchMineSelectedCard(it: CardData) {
        if (it.isSelected) {
            mineSelectedCardList.add(it)
        } else {
            mineSelectedCardList.remove(it)
        }
    }

    fun onPlayAgainClickListener() {
        currentPlayCardDataList.clear()
        mineSelectedCardList.clear()
        for (data in allCardList) {
            removeAllCardLiveData.value = data
        }
        allCardList.clear()
        allCardList.addAll(Tool.getAllCardList())
        startToShowRefreshCardAnimation()
        hideUserLeftCardLiveData.value = View.GONE
        showTotalAmount.value = Pair(UserDataTool.getUserCashAmount(),1)
        showTotalAmount.value = Pair(UserDataTool.getBot2CashAmount(),2)
        showTotalAmount.value = Pair(UserDataTool.getBot3CashAmount(),3)
        showTotalAmount.value = Pair(UserDataTool.getBot4CashAmount(),4)
    }


}