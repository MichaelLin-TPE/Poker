package com.michael.cardgame

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import com.michael.cardgame.base.BaseActivity
import com.michael.cardgame.bean.CardData
import com.michael.cardgame.databinding.ActivityMainBinding
import com.michael.cardgame.tool.Tool
import com.michael.cardgame.tool.Tool.convertDp

class MainActivity : BaseActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_main)
        viewModel = getViewModel(MainViewModel::class.java)
        viewModel.setScreenWidthAndHeight(Tool.getScreenWidth(this),Tool.getScreenHeight(this))
        viewModel.startToFlow()
        handleLiveData()

    }

    private fun handleLiveData() {
        viewModel.showPokerLiveData.observe(this){
            showPoker(it)
        }
        viewModel.bringPokerTogether.observe(this){
            bringPokerToCenter(it)
        }
    }

    private fun bringPokerToCenter(cardData: CardData) {
        val targetX = ((Tool.getScreenWidth(this) - (90.convertDp())) / 2).toFloat()
        val targetY = ((Tool.getScreenHeight(this) - (150.convertDp())) / 2).toFloat()
        cardData.cardView?.animate()?.x(targetX)?.y(targetY)?.setDuration(100)?.start()
    }

    /**
     * 顯示撲克牌
     */
    private fun showPoker(data: CardData) {
        val view = View.inflate(this,R.layout.item_poker_car_layout,null)
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
            layoutParams.width = 90.convertDp()
            layoutParams.height = 150.convertDp()
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