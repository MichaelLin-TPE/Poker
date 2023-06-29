package com.michael.cardgame.home

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.databinding.DataBindingUtil
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.michael.cardgame.LauncherActivity
import com.michael.cardgame.R
import com.michael.cardgame.base.BaseActivity
import com.michael.cardgame.big_two.BigTwoActivity
import com.michael.cardgame.databinding.ActivityHomeBinding
import com.michael.cardgame.dialog.ChooseGameModeDialog
import com.michael.cardgame.tool.FirebaseDAO
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

    override fun onStop() {
        super.onStop()
        viewModel.onStop()
    }

    override fun onResume() {
        super.onResume()
        viewModel.onResume()
    }

    private fun initView() {
        binding.ivBigTwo.setOnClickListener {
            viewModel.onBigTwoClickListener()

        }
        binding.tvLogout.setOnClickListener {
            Firebase.auth.signOut()
            goToPage(LauncherActivity::class.java)
            finish()
        }
    }

    private fun handleLiveData() {
        viewModel.showBigTwoChooseGameModeDialogLiveData.observe(this){
            val dialog = ChooseGameModeDialog.newInstance()
            dialog.show(supportFragmentManager,"dialog")
        }
        viewModel.showOnlineUserCount.observe(this){
            binding.tvOnlineUser.text = "在線人數 : $it 人"
        }
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