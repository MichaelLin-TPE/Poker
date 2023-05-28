package com.michael.cardgame

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import com.michael.cardgame.base.BaseActivity
import com.michael.cardgame.bean.CardData
import com.michael.cardgame.databinding.ActivityMainBinding
import com.michael.cardgame.tool.Tool
import com.michael.cardgame.tool.Tool.convertDp


class MainActivity : BaseActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel
    private var randomSingleCard : CardData? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        viewModel = getViewModel(MainViewModel::class.java)
        viewModel.setScreenWidthAndHeight(Tool.getScreenWidth(this), Tool.getScreenHeight(this))
        viewModel.startToFlow()
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
            ?.x(it.cardView?.x!! + 400)
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