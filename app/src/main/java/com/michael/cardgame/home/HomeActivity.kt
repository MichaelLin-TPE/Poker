package com.michael.cardgame.home

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.michael.cardgame.R
import com.michael.cardgame.base.BaseActivity
import com.michael.cardgame.big_two.BigTwoActivity
import com.michael.cardgame.databinding.ActivityHomeBinding
import com.michael.cardgame.tool.SpeechTool

class HomeActivity : BaseActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var viewModel: HomeViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_home)
        viewModel = getViewModel(HomeViewModel::class.java)

        SpeechTool.init(this)
        handleLiveData()
        initView()
    }

    private fun initView() {
        binding.ivBigTwo.setOnClickListener {
            goToPage(BigTwoActivity::class.java)
        }
    }

    private fun handleLiveData() {
        viewModel.showUserCashAmountLiveData.observe(this){
            binding.tvUserCash.text = it
        }
        viewModel.showUserDiamondCountLiveData.observe(this){
            binding.tvUserDiamond.text = it
        }
        viewModel.showUserNameLiveData.observe(this){
            binding.tvUserName.text = it
        }
        viewModel.showUserPhotoLiveData.observe(this){
            binding.ivUserPhoto.setImageResource(it)
        }
    }
}