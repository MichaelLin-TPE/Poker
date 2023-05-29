package com.michael.cardgame

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewTreeObserver
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import com.michael.cardgame.base.BaseActivity
import com.michael.cardgame.bean.CardData
import com.michael.cardgame.databinding.ActivityMainBinding
import com.michael.cardgame.tool.Tool


class MainActivity : BaseActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel
    private var randomSingleCard : CardData? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        viewModel = getViewModel(MainViewModel::class.java)


        binding.rootView.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                // 這時佈局已經完成
                viewModel.setScreenWidthAndHeight(Tool.getScreenWidth(this@MainActivity), Tool.getScreenHeight(this@MainActivity))
                viewModel.startToFlow()
                val topLeftX = binding.pokerOutsideBg.x
                val topRightX = binding.pokerOutsideBg.x + binding.pokerOutsideBg.width - binding.ivUser2.width
                val topY = binding.pokerOutsideBg.y
                val bottomY = binding.pokerOutsideBg.y + binding.pokerOutsideBg.height
                val bottomRightX = binding.pokerOutsideBg.x + binding.pokerOutsideBg.width - binding.ivUser3.width
                viewModel.setAllUserLocation(topLeftX,topRightX,bottomRightX,topY,bottomY)

                // 可能需要移除 Listener 以避免重複調用
                binding.rootView.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })
        handleLiveData()

    }

    private fun handleLiveData() {
        viewModel.showPokerLiveData.observe(this) {
            showPoker(it)
        }
        viewModel.flipCardLiveData.observe(this) {
            flipCard(it)
        }
        viewModel.bringPokerTogether.observe(this) {
            bringPokerToCenter(it.first, it.second)
        }
        viewModel.moveSingleCardLiveData.observe(this) {
            moveSingleCard(it)
        }
        viewModel.moveBackSingleCardLiveData.observe(this){
            moveBackCard(it)
        }
        viewModel.dealCardLiveData.observe(this){
            dealMyCard(it)
        }
        viewModel.handleMyCardTouchListenerLiveData.observe(this){
            handleTouchListener(it)
        }
        viewModel.switchSingleCardLiveData.observe(this){
            it.cardView?.animate()?.x(it.targetX)?.setDuration(100)?.start()
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun handleTouchListener(it: CardData) {
        it.cardView?.setOnTouchListener(object : OnTouchListener {
            private var initialX = 0f
            private var initialY = 0f
            private var initialTouchX = 0f
            private var initialTouchY = 0f
            override fun onTouch(v: View, event: MotionEvent): Boolean {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        initialX = v.x
                        initialY = v.y
                        initialTouchX = event.rawX
                        initialTouchY = event.rawY
                        return true
                    }

                    MotionEvent.ACTION_UP -> {
                        v.animate().x(it.targetX).y(it.targetY).setDuration(100).start()
                        viewModel.refreshMyCardList()
                        return true
                    }
                    MotionEvent.ACTION_MOVE -> {
                        val moveX = initialX + event.rawX - initialTouchX
                        val moveY = initialY + event.rawY - initialTouchY
                        viewModel.setMovingCardLocationX(moveX,it)
                        Log.i("Michael","moveX : $moveX moveY : $moveY")
                        v.animate().x(moveX).y(moveY).setDuration(0).start()
                        return true
                    }
                }
                return false
            }
        })
    }

    private fun dealMyCard(it: CardData) {
        val ivCardBg = it.cardView?.findViewById<ImageView>(R.id.card_bg)
        ivCardBg?.visibility = View.GONE
        it.cardView?.bringToFront()
        it.cardView?.animate()
            ?.x(it.targetX)
            ?.y(it.targetY)
            ?.withEndAction {
                viewModel.dealMyNextCard()
            }
            ?.setDuration(200)
            ?.start()
    }

    private fun moveBackCard(pair: Pair<Float, Float>) {
        if (randomSingleCard == null){
            return
        }
        randomSingleCard!!.cardView?.animate()
            ?.x(pair.first)
            ?.withEndAction {
                viewModel.readyToDealCards()
            }
            ?.setDuration(500)
            ?.start()
    }

    private fun moveSingleCard(it: CardData) {
        val ivCardBg = it.cardView?.findViewById<ImageView>(R.id.card_bg)
        ivCardBg?.visibility = View.GONE
        it.cardView?.animate()
            ?.x(it.cardView?.x!! + Tool.getCardWidth())
            ?.withEndAction {
                Handler(Looper.getMainLooper()).postDelayed({
                    flipCard(it)
                },1000)
            }
            ?.setDuration(500)
            ?.start()
    }

    private fun flipCard(it: CardData) {
        randomSingleCard = it
        val animator = ObjectAnimator.ofFloat(it.cardView, "rotationY", 0f, 180f)
        animator.duration = 500
        animator.interpolator = AccelerateDecelerateInterpolator()
        animator.addUpdateListener { animation->
            val progress = animation.animatedFraction
            val ivCardBg = it.cardView?.findViewById<ImageView>(R.id.card_bg)
            if (ivCardBg?.visibility == View.VISIBLE){
                return@addUpdateListener
            }
            if (progress * 100 > 50){
                ivCardBg?.visibility = View.VISIBLE
            }
        }
        animator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                viewModel.flipSingleCardComplete()
            }
        })
        animator.start()
    }

    private fun bringPokerToCenter(cardData: CardData, plusValue: Float) {
        cardData.cardView?.findViewById<ImageView>(R.id.card_bg)?.visibility = View.VISIBLE
        val targetX =
            ((Tool.getScreenWidth(this) - (Tool.getCardWidth())) / 2).toFloat() - plusValue
        val targetY =
            ((Tool.getScreenHeight(this) - (Tool.getCardHeight())) / 2).toFloat() - plusValue
        cardData.cardView?.animate()
            ?.x(targetX)
            ?.withEndAction {
                cardData.cardView?.x = targetX
                cardData.cardView?.y = targetY
                viewModel.onCheckBringCardTogetherFinishedListener(plusValue)
            }
            ?.y(targetY)
            ?.setDuration(100)
            ?.start()

        Log.i("Poker", "plusValue : $plusValue")
    }

    /**
     * 顯示撲克牌
     */
    private fun showPoker(data: CardData) {
        val view = View.inflate(this, R.layout.item_poker_car_layout, null)
        binding.rootView.addView(view)
        val tvNumber1 = view.findViewById<TextView>(R.id.tv_number_1)
        val tvNumber2 = view.findViewById<TextView>(R.id.tv_number_2)
        val ivFlavor1 = view.findViewById<ImageView>(R.id.iv_flavor)
        val ivFlavor2 = view.findViewById<ImageView>(R.id.iv_flavor1)
        val ivCenterImage = view.findViewById<ImageView>(R.id.iv_flavor1_main)
        tvNumber2.text = data.cardNumber
        tvNumber1.text = data.cardNumber
        ivFlavor1.setImageResource(data.cardImage)
        ivFlavor2.setImageResource(data.cardImage)
        ivCenterImage.setImageResource(data.centerImage)
        view.visibility = View.INVISIBLE
        view.post {
            val layoutParams = view.layoutParams
            layoutParams.width = Tool.getCardWidth()
            layoutParams.height = Tool.getCardHeight()
            view.layoutParams = layoutParams
            view.x = data.targetX
            view.y = data.targetY
            view.visibility = View.VISIBLE
            data.cardView = view
        }

    }

    override fun onDestroy() {
        viewModel.onDestroy()
        super.onDestroy()
    }

}