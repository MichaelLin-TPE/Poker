package com.michael.cardgame.home

import android.app.Application
import android.service.autofill.UserData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.DocumentSnapshot
import com.michael.cardgame.base.BaseViewModel
import com.michael.cardgame.tool.FirebaseDAO
import com.michael.cardgame.tool.Tool
import com.michael.cardgame.tool.UserDataTool

class HomeViewModel(private val application: Application) : BaseViewModel(application) {


    val showUserDiamondCountLiveData = MutableLiveData<String>()
    val showUserCashAmountLiveData = MutableLiveData<String>()
    val showUserNameLiveData = MutableLiveData<String>()
    val showUserPhotoLiveData = MutableLiveData<Int>()

    init {
        db.getLiveUserData(object : FirebaseDAO.OnFirebaseCatchUserDataListener{
            override fun onCatchUserData(value: DocumentSnapshot) {
                showUserDiamondCountLiveData.value = Tool.formatThousand(value.getLong("diamondCount")?.toInt()!!)
                showUserCashAmountLiveData.value = "$"+Tool.formatThousand(value.getLong("cashCount")?.toInt()!!)
                showUserNameLiveData.value = value.getString("name")!!
                showUserPhotoLiveData.value = value.getLong("photoId")?.toInt()!!
            }

            override fun onError(errorMsg: String) {
                showErrorMsg(errorMsg)
            }

            override fun onNeedToCreateUser() {

            }

        })

    }


}