package com.michael.cardgame

import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.michael.cardgame.base.BaseActivity
import com.michael.cardgame.big_two.BigTwoActivity
import com.michael.cardgame.dialog.UserPhotoAndNameConfirmDialog
import com.michael.cardgame.home.HomeActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LauncherActivity : BaseActivity() {

    private lateinit var viewModel: LauncherViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_page)
        viewModel = getViewModel(LauncherViewModel::class.java)


        handleLiveData()
        initView()
    }

    private fun initView() {

    }

    private fun handleLiveData() {
        viewModel.goToHomePageLiveData.observe(this){
            goToPage(HomeActivity::class.java)
        }
        viewModel.showSetUpNameAndPhotoLiveData.observe(this){
            val dialog = UserPhotoAndNameConfirmDialog.newInstance()
            dialog.show(supportFragmentManager,"dialog")
            dialog.setOnSetUpUserDataListener{ name,photoId->
                viewModel.onCatchUserData(name,photoId)
            }
        }
    }
}