package com.michael.cardgame.lobby

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.michael.cardgame.base.BaseViewModel
import com.michael.cardgame.bean.RoomData
import com.michael.cardgame.bean.UserData
import com.michael.cardgame.tool.Tool
import com.michael.cardgame.tool.UserDataTool
import kotlin.random.Random

class OnlineGameLobbyViewModel(application: Application) : BaseViewModel(application) {

    val showCreateGameDialogLiveData = MutableLiveData<Boolean>()
    val goToGameRoomActivityLiveData = MutableLiveData<String>()
    val showRoomListLiveData = MutableLiveData<MutableList<RoomData>>()
    val showUserNameAndPhotoLiveData = MutableLiveData<Pair<String, Int>>()
    val showCashDiamondLiveData = MutableLiveData<Pair<Int, Int>>()
    fun onCreate() {
        db.onCatchRoomList { roomList ->
            showRoomListLiveData.value = roomList
        }
        showUserNameAndPhotoLiveData.value =
            Pair(UserDataTool.getUserName(), UserDataTool.getUserPhoto())
        showCashDiamondLiveData.value =
            Pair(UserDataTool.getUserCashAmount(), UserDataTool.getUserDiamondCount())

    }

    fun onCreateGameClickListener() {
        showCreateGameDialogLiveData.value = true
    }

    fun onStartToCreateGame(gameName: String, bettingValue: Int) {
        db.createGame(gameName, bettingValue) { key ->
            Tool.showToast("創建房間成功")
            goToGameRoomActivityLiveData.value = key
        }
    }

    fun onRoomJoinClickListener(key: String, userList: MutableList<UserData>) {
        userList.add(
            UserData(
                UserDataTool.getEmail(),
                UserDataTool.getUserName(),
                UserDataTool.getUserPhoto(),
                false
            )
        )
        db.updateRoomUserList(key, userList) {
            goToGameRoomActivityLiveData.value = key
        }

    }

    fun onQuickJoinButtonClickListener() {
        val roomList: MutableList<RoomData>? = showRoomListLiveData.value
        roomList?.let {
            if (roomList.isEmpty()) {
                showErrorMsg("目前無任何房間~")
                return@let
            }
            val roomData = roomList[Random.nextInt(roomList.size)]
            val randomUserList = roomData.userList
            val randomKey = roomData.key
            randomUserList.add(
                UserData(
                    UserDataTool.getEmail(),
                    UserDataTool.getUserName(),
                    UserDataTool.getUserPhoto(),
                    false
                )
            )
            db.updateRoomUserList(randomKey, randomUserList) {
                goToGameRoomActivityLiveData.value = randomKey
            }
        }

    }


}