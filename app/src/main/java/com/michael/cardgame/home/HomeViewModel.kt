package com.michael.cardgame.home

import android.app.Application
import android.service.autofill.UserData
import androidx.lifecycle.MutableLiveData
import com.michael.cardgame.base.BaseViewModel
import com.michael.cardgame.tool.Tool
import com.michael.cardgame.tool.UserDataTool

class HomeViewModel(private val application: Application) : BaseViewModel(application) {

    val showUserDiamondCountLiveData = MutableLiveData<String>()
    val showUserCashAmountLiveData = MutableLiveData<String>()
    val showUserNameLiveData = MutableLiveData<String>()
    val showUserPhotoLiveData = MutableLiveData<Int>()

    init {
        showUserDiamondCountLiveData.value = Tool.formatThousand(UserDataTool.getUserDiamondCount())
        showUserCashAmountLiveData.value = "$"+Tool.formatThousand(UserDataTool.getUserCashAmount())
        showUserNameLiveData.value = UserDataTool.getUserName()
        showUserPhotoLiveData.value = UserDataTool.getUserPhoto()
    }


}