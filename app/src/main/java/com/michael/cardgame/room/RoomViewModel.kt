package com.michael.cardgame.room

import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import com.michael.cardgame.base.BaseViewModel
import com.michael.cardgame.bean.MessageData
import com.michael.cardgame.bean.UserData
import com.michael.cardgame.tool.UserDataTool

class RoomViewModel(application: Application) : BaseViewModel(application) {

    val showGameNameLiveData = MutableLiveData<String>()
    val showUserListLiveData = MutableLiveData<MutableList<UserData>>()
    val showChatListLiveData = MutableLiveData<MutableList<MessageData>>()
    val finishPageLiveData = MutableLiveData<Boolean>()
    private var key = ""
    private var userDataMap :Map<String,Any> = hashMapOf()

    fun onCreate(key:String){
        this.key = key
        Log.i("Poker","key : $key")
        db.onCatchRoomData(key){ userDataMap->
            this.userDataMap = userDataMap
            Log.i("Poker","有資料")
            val gameName :String = userDataMap["gameName"].toString()
            val userList = getUserList()
            Log.i("Poker","收到新的USERLIST 長度 : ${userList.size}")
            showGameNameLiveData.value = gameName
            showUserListLiveData.value = userList
        }

        db.onCatchChatList(key){
            it.sortWith{ o1,o2->
                (o1.time - o2.time).toInt()
            }
            showChatListLiveData.value = it
        }
    }

    private fun getUserList(): MutableList<UserData> {
        if (userDataMap.isEmpty()){
            return mutableListOf()
        }
        val userListJson :String = userDataMap["usersList"].toString()
        return Gson().fromJson(userListJson,object : TypeToken<MutableList<UserData>>(){}.type)
    }

    fun onBackClickListener() {
        if (getUserList().isNotEmpty() && getUserList().size == 1){
            db.removeRoom(key){
                db.removeLiveCatch()
                finishPageLiveData.value = true
            }
            return
        }
        val userList = getUserList()
        for (user in userList){
            if (user.name == UserDataTool.getUserName()){
                userList.remove(user)
                break
            }
        }
        Log.i("Poker","修改後的UserList 長度 : ${userList.size}")
        db.removeRoomUserList(key,userList){
            db.removeLiveCatch()
            finishPageLiveData.value = true
        }
    }

    fun onMessageSendClickListener(msg: String) {
        db.sendMsg(msg,key)
    }

}