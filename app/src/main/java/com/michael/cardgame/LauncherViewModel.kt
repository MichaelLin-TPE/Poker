package com.michael.cardgame

import android.app.Application
import android.database.DatabaseUtils
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.michael.cardgame.base.BaseViewModel
import com.michael.cardgame.tool.UserDataTool
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LauncherViewModel(private val application: Application) : BaseViewModel(application)  {
    fun onCatchUserData(name: String, photoId: Int) {
        if (name.isEmpty()){
            showErrorMsg("請輸入暱稱")
            return
        }
        if(photoId == 0){
            showErrorMsg("請選擇一張圖象")
            return
        }
        UserDataTool.saveUserData(name,photoId)
        goToHomePageLiveData.value = true
    }

    val goToHomePageLiveData = MutableLiveData<Boolean>()
    val showSetUpNameAndPhotoLiveData = MutableLiveData<Boolean>()


    init {

        val userName = UserDataTool.getUserName()
        if (userName.isEmpty()){

            showSetUpNameAndPhotoLiveData.value = true

        }else{
            viewModelScope.launch(Dispatchers.IO) {
                delay(2000)
                withContext(Dispatchers.Main){
                    goToHomePageLiveData.value = true
                }
            }
        }


    }

}