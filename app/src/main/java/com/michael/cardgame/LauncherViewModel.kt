package com.michael.cardgame

import android.app.Application
import android.util.Log
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

class LauncherViewModel(private val application: Application) : BaseViewModel(application) {

    val goToHomePageLiveData = MutableLiveData<Boolean>()
    val showSetUpNameAndPhotoLiveData = MutableLiveData<Boolean>()
    fun onCatchUserData(name: String, photoId: Int) {
        if (name.isEmpty()) {
            showErrorMsg("請輸入暱稱")
            return
        }
        if (photoId == 0) {
            showErrorMsg("請選擇一張圖象")
            return
        }
        val map = hashMapOf<String, Any>(
            "email" to UserDataTool.getEmail(),
            "name" to name,
            "photoId" to photoId,
            "diamondCount" to 0,
            "cashCount" to 0
        )
        UserDataTool.saveUserCashAmount(0)
        UserDataTool.saveDiamondCount(0)
        UserDataTool.saveUserData(name, photoId)
        db.saveUserData(map)

        goToHomePageLiveData.value = true
    }

    fun onStartFlow(email: String?) {
        if (email == null) {
            showErrorMsg("發生非預期性錯誤,請稍後再嘗試")
            return
        }
        UserDataTool.saveEmail(email)

        db.getUserData(email, object : FirebaseDAO.OnFirebaseCatchUserDataListener {
            override fun onCatchUserData(value: DocumentSnapshot) {
                val name = value.getString("name")
                val photoId = value.getLong("photoId")
                val diamondCount = value.getLong("diamondCount")
                val cashCount = value.getLong("cashCount")
                if (name == null){
                    showErrorMsg("發生非預期性錯誤,請稍後再嘗試")
                    return
                }
                Log.i("Poker","name : $name , photoId : $photoId , diamondCount : $diamondCount , cashCount : $cashCount")
                UserDataTool.saveUserCashAmount(cashCount?.toInt()!!)
                UserDataTool.saveDiamondCount(diamondCount?.toInt()!!)
                UserDataTool.saveUserData(name, photoId?.toInt()!!)
                viewModelScope.launch(Dispatchers.IO) {
                    delay(2000)
                    withContext(Dispatchers.Main){
                        goToHomePageLiveData.value = true
                    }
                }

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

//        }
    }


}