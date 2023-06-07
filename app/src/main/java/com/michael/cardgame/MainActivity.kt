package com.michael.cardgame

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import com.michael.cardgame.base.BaseActivity
import com.michael.cardgame.bean.CardData
import com.michael.cardgame.databinding.ActivityMainBinding
import com.michael.cardgame.tool.Tool
import com.michael.cardgame.tool.Tool.convertDp
import java.util.Random


class MainActivity : BaseActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel
    private var randomSingleCard: CardData? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        viewModel = getViewModel(MainViewModel::class.java)


        binding.rootView.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                // 這時佈局已經完成
                viewModel.setScreenWidthAndHeight(
                    (binding.rootView.x + binding.rootView.width).toInt(),
                    (binding.pokerOutsideBg.y + binding.pokerOutsideBg.height).toInt()
                )
                viewModel.startToFlow()
                val topLeftX = binding.pokerOutsideBg.x + binding.ivUser1.width + Tool.getCardWidth()
                val topRightX = binding.pokerOutsideBg.x + binding.pokerOutsideBg.width - binding.ivUser2.width - Tool.getCardWidth() - Tool.getCardWidth()
                val topY = binding.pokerOutsideBg.y + binding.ivUser1.y + binding.ivUser1.height
                val bottomY = binding.pokerOutsideBg.y + binding.pokerOutsideBg.height - binding.ivUser3.height - Tool.getCardHeight()
                val bottomRightX = binding.pokerOutsideBg.x + binding.pokerOutsideBg.width - binding.ivUser3.width - Tool.getCardWidth() - Tool.getCardWidth()
                viewModel.setAllUserLocation(topLeftX, topRightX, bottomRightX, topY, bottomY)
                // 可能需要移除 Listener 以避免重複調用
                binding.rootView.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })
        handleLiveData()
        initView()
    }

    private fun initView() {
        binding.tvPlayCard.setOnClickListener {
            viewModel.onPlayMyCardClickListener()
        }
        binding.tvPass.setOnClickListener {
            viewModel.onPassClickListener()
        }
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
        viewModel.moveBackSingleCardLiveData.observe(this) {
            moveBackCard(it)
        }
        viewModel.dealCardLiveData.observe(this) {
            dealMyCard(it)
        }
        viewModel.handleMyCardTouchListenerLiveData.observe(this) {
            handleTouchListener(it)
        }
        viewModel.switchSingleCardLiveData.observe(this) {
            it.cardView?.animate()?.x(it.targetX)?.setDuration(100)?.start()
        }
        viewModel.userCollectCardsLiveData.observe(this){
            it.first.cardView?.animate()
                ?.rotation(0f)
                ?.x(getTargetX(it.second))
                ?.y(getTargetY(it.second))
                ?.withEndAction {
                    it.first.targetX = getTargetX(it.second)
                    it.first.targetY = getTargetY(it.second)
                    it.first.cardView?.visibility = View.INVISIBLE
                    viewModel.onCatchUserNum(it.second)
                }
                ?.setDuration(100)
                ?.start()
        }

        viewModel.showInformationLiveData.observe(this){
            binding.tvCenterInfo.alpha = 1f
            val animation : Animation = AnimationUtils.loadAnimation(this@MainActivity,R.anim.scale_and_fade)
            animation.setAnimationListener(object : Animation.AnimationListener{
                override fun onAnimationStart(animation: Animation?) {

                }

                override fun onAnimationEnd(animation: Animation?) {
                    binding.tvCenterInfo.visibility = View.INVISIBLE
                    viewModel.startPlayACard()
                }

                override fun onAnimationRepeat(animation: Animation?) {

                }
            })
            binding.tvCenterInfo.startAnimation(animation)
        }
        viewModel.startShowingUserCards.observe(this){
            it.first.cardView?.findViewById<ImageView>(R.id.card_bg)?.visibility = View.GONE
            it.first.cardView?.visibility = View.VISIBLE
            it.first.cardView?.bringToFront()
            it.first.cardView?.animate()
                ?.rotation(0f)
                ?.x(it.first.targetX)
                ?.y(it.first.targetY)
                ?.withEndAction {
                    val layoutParams = it.first.cardView?.layoutParams
                    layoutParams?.width = 40.convertDp()
                    layoutParams?.height = 75.convertDp()
                    it.first.cardView?.layoutParams = layoutParams
                    it.first.cardView?.bringToFront()
                    if (it.second){
                        viewModel.onPlayCardComplete()
                    }
                }
                ?.setDuration(100)
                ?.start()
        }
        viewModel.showMinePassButtonAndPlayCardButton.observe(this){
            binding.tvPass.visibility = it
            binding.tvPlayCard.visibility = it
        }

        viewModel.refreshMyCardsLiveData.observe(this){
            it.cardView?.animate()
                ?.rotation(0f)
                ?.x(it.targetX)
                ?.y(it.targetY)
                ?.withEndAction {

                }
                ?.setDuration(100)
                ?.start()
        }
        viewModel.bringAlreadyShowingCardTogetherLiveData.observe(this){
            bringAlreadyShowCardTogether(it)
        }

        viewModel.showPassContentLiveData.observe(this){
            when (it) {
                2 -> {
                    binding.tvUser1Pass.visibility = View.VISIBLE
                    disableContent(binding.tvUser1Pass)
                }
                3 -> {
                    binding.tvUser2Pass.visibility = View.VISIBLE
                    disableContent(binding.tvUser2Pass)
                }
                else -> {
                    binding.tvUser3Pass.visibility = View.VISIBLE
                    disableContent(binding.tvUser3Pass)
                }
            }
        }

        viewModel.bringAllSelectedCardLiveData.observe(this){
            it.cardView?.animate()?.x(it.targetX)?.setDuration(100)?.start()
        }

        viewModel.showUserLeftCardLiveData.observe(this){

            val leftCardCount = "手牌剩餘${it.first}張"
            when (it.second){
                1 ->{
                    binding.tvUser4Info.text = leftCardCount
                    binding.tvUser4Info.visibility = View.VISIBLE
                }
                2 ->{
                    binding.tvUser1Info.text = leftCardCount
                    binding.tvUser1Info.visibility = View.VISIBLE
                }
                3 ->{
                    binding.tvUser2Info.text = leftCardCount
                    binding.tvUser2Info.visibility = View.VISIBLE
                }
                else ->{
                    binding.tvUser3Info.text = leftCardCount
                    binding.tvUser3Info.visibility = View.VISIBLE
                }
            }
        }

    }

    private fun disableContent(tvContent: TextView) {
        Handler(Looper.getMainLooper()).postDelayed({
            tvContent.visibility = View.INVISIBLE
        },1000)
    }

    private fun getTargetY(userNum: Int): Float {
        return when(userNum){
            2 -> binding.pokerOutsideBg.y
            3 -> binding.pokerOutsideBg.y
            else -> binding.pokerOutsideBg.y + binding.pokerOutsideBg.height - 75.convertDp()
        }
    }

    private fun getTargetX(userNum: Int): Float {
        return when(userNum){
            2 -> binding.pokerOutsideBg.x + binding.ivUser1.width
            3 -> binding.pokerOutsideBg.x + binding.pokerOutsideBg.width - binding.ivUser2.width
            else -> binding.pokerOutsideBg.x + binding.pokerOutsideBg.width - binding.ivUser3.width
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun handleTouchListener(it: CardData) {

        it.cardView?.setOnClickListener { view ->
            it.isSelected = !it.isSelected
            if (it.isSelected) {
                it.cardView?.animate()?.y(it.targetY - 100)?.setDuration(100)?.start()
            } else {
                it.cardView?.animate()?.y(it.targetY)?.setDuration(100)?.start()
            }
            viewModel.onCatchMineSelectedCard(it)

        }
    }

    private fun dealMyCard(it: Pair<CardData, Boolean>) {
        val ivCardBg = it.first.cardView?.findViewById<ImageView>(R.id.card_bg)
        ivCardBg?.visibility = if (it.second) View.VISIBLE else View.GONE
        if (!it.second) {
            it.first.cardView?.bringToFront()
        }
        it.first.cardView?.animate()
            ?.x(it.first.targetX)
            ?.y(it.first.targetY)
            ?.withEndAction {
                if (it.second) {
                    val layoutParams = it.first.cardView?.layoutParams
                    layoutParams?.width = 40.convertDp()
                    layoutParams?.height = 75.convertDp()
                    it.first.cardView?.layoutParams = layoutParams
                    it.first.cardView?.animate()?.rotation(Random().nextFloat() * 45)
                        ?.setDuration(10)?.start()
                }
                viewModel.dealMyNextCard()
            }
            ?.setDuration(150)
            ?.start()
    }

    private fun moveBackCard(pair: Pair<Float, Float>) {
        if (randomSingleCard == null) {
            return
        }
        randomSingleCard!!.cardView?.animate()
            ?.x(pair.first)
            ?.withEndAction {
                randomSingleCard!!.cardView?.findViewById<ImageView>(R.id.card_bg)?.visibility =
                    View.VISIBLE
                viewModel.readyToDealCards(randomSingleCard?.cardValue)
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
                    randomSingleCard = it
                    viewModel.flipSingleCardComplete()
                }, 500)
            }
            ?.setDuration(500)
            ?.start()
    }

    /**
     * 暫時不用到
     */
    private fun flipCard(it: CardData) {
        randomSingleCard = it
        val animator = ObjectAnimator.ofFloat(it.cardView, "rotationY", 0f, 180f)
        animator.duration = 500
        animator.interpolator = AccelerateDecelerateInterpolator()
        animator.addUpdateListener { animation ->
            val progress = animation.animatedFraction
            val ivCardBg = it.cardView?.findViewById<ImageView>(R.id.card_bg)
            if (ivCardBg?.visibility == View.VISIBLE) {
                return@addUpdateListener
            }
            if (progress * 100 > 50) {
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
            ?.setDuration(50)
            ?.start()
    }

    private fun bringAlreadyShowCardTogether(cardData: CardData){
        val targetX =
            ((Tool.getScreenWidth(this) - (Tool.getCardWidth())) / 2).toFloat()
        val targetY =
            ((Tool.getScreenHeight(this) - (Tool.getCardHeight())) / 2).toFloat()
        cardData.cardView?.animate()
            ?.rotation(Random().nextFloat() * 45)
            ?.x(targetX)
            ?.withEndAction {
                cardData.cardView?.x = targetX
                cardData.cardView?.y = targetY
            }
            ?.y(targetY)
            ?.setDuration(50)
            ?.start()
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