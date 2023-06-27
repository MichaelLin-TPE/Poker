package com.michael.cardgame

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.DocumentSnapshot
import com.michael.cardgame.base.BaseViewModel
import com.michael.cardgame.tool.FirebaseDAO
import com.michael.cardgame.tool.UserDataTool
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LauncherViewModel(private val application: Application) : BaseViewModel(application)  {

    val goToHomePageLiveData = MutableLiveData<Boolean>()
    val showSetUpNameAndPhotoLiveData = MutableLiveData<Boolean>()

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

    fun onStartFlow(email: String?, db: FirebaseDAO) {
        if (email == null){
            showErrorMsg("發生非預期性錯誤,請稍後再嘗試")
            return
        }
        db.getUserData(email,object : FirebaseDAO.OnFirebaseCatchUserDataListener{
            override fun onCatchUserData(value: DocumentSnapshot?) {



            }

            override fun onError(errorMsg: String) {
                showErrorMsg(errorMsg)
            }

            override fun onNeedToCreateUser() {
                showSetUpNameAndPhotoLiveData.value = true
            }
        })



//        val userName = UserDataTool.getUserName()
//        if (userName.isEmpty()){
//
//            showSetUpNameAndPhotoLiveData.value = true
//
//        }else{
//            viewModelScope.launch(Dispatchers.IO) {
//                delay(2000)
//                withContext(Dispatchers.Main){
//                    goToHomePageLiveData.value = true
//                }
//            }
//        }
    }




}