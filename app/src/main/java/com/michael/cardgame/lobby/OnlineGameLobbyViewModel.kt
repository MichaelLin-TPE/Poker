package com.michael.cardgame.lobby

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.michael.cardgame.base.BaseViewModel
import com.michael.cardgame.bean.RoomData
import com.michael.cardgame.bean.UserData
import com.michael.cardgame.tool.Tool
import com.michael.cardgame.tool.UserDataTool

class OnlineGameLobbyViewModel(application: Application) : BaseViewModel(application) {

    val showCreateGameDialogLiveData = MutableLiveData<Boolean>()
    val goToGameRoomActivityLiveData = MutableLiveData<String>()
    val showRoomListLiveData = MutableLiveData<MutableList<RoomData>>()
    val showUserNameAndPhotoLiveData = MutableLiveData<Pair<String,Int>>()
    val showCashDiamondLiveData = MutableLiveData<Pair<Int,Int>>()
    fun onCreate() {
        db.onCatchRoomList{roomList->
            showRoomListLiveData.value = roomList
        }
        showUserNameAndPhotoLiveData.value = Pair(UserDataTool.getUserName(),UserDataTool.getUserPhoto())
        showCashDiamondLiveData.value = Pair(UserDataTool.getUserCashAmount(),UserDataTool.getUserDiamondCount())

    }

    fun onCreateGameClickListener() {
        showCreateGameDialogLiveData.value = true
    }

    fun onStartToCreateGame(gameName: String, bettingValue: Int) {
        db.createGame(gameName,bettingValue){key->
            Tool.showToast("創建房間成功")
            goToGameRoomActivityLiveData.value = key
        }
    }

    fun onRoomJoinClickListener(key: String, userList: MutableList<UserData>) {
        db.updateRoomUserList(key,userList){
            goToGameRoomActivityLiveData.value = key
        }

    }


}