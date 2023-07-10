package com.michael.cardgame.room

import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.common.reflect.TypeToken
import com.google.firebase.firestore.auth.User
import com.google.gson.Gson
import com.michael.cardgame.base.BaseViewModel
import com.michael.cardgame.bean.AcceptData
import com.michael.cardgame.bean.MessageData
import com.michael.cardgame.bean.UserData
import com.michael.cardgame.tool.UserDataTool
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit
import kotlin.random.Random

class RoomViewModel(application: Application) : BaseViewModel(application) {

    val showGameNameLiveData = MutableLiveData<String>()
    val showUserListLiveData = MutableLiveData<MutableList<UserData>>()
    val showChatListLiveData = MutableLiveData<MutableList<MessageData>>()
    val setReadyButtonLiveData = MutableLiveData<String>()
    val showBettingValueLiveData = MutableLiveData<String>()
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
            val roomBoss = userDataMap["roomBoss"].toString()
            val acceptList = getAcceptList()
            var isFoundBoss = false
            for (user in userList){
                if (user.email == roomBoss){
                    user.isRoomBoss = true
                    isFoundBoss = true
                }

            }
            for (user in userList){
                for (accept in getAcceptList()){
                    if (user.email == accept.email && accept.isAccept){
                        user.isReady = true
                        break
                    }
                }
            }
            showBettingValueLiveData.value = "一張$${userDataMap["bettingValue"]}"
            showGameNameLiveData.value = gameName
            showUserListLiveData.value = userList
            var isFoundReady = false
            for (accept in acceptList){
                if (accept.email == UserDataTool.getEmail() && accept.isAccept){
                    setReadyButtonLiveData.value = "取消準備"
                    isFoundReady = true
                }
            }
            if (!isFoundReady){
                setReadyButtonLiveData.value = "我準備好了"
            }

            val isRoomBoss = userDataMap["roomBoss"].toString() == UserDataTool.getEmail()
            if (isRoomBoss){
                setReadyButtonLiveData.value = "開始遊戲"
            }
            val email = userList[0].email
            if (userList.size == 1 && email == UserDataTool.getEmail()){
                db.updateRoomBoss(key,email)
            }
            if (!isFoundBoss){
                val randomEmail = getUserList()[Random.nextInt(getUserList().size)].email
                db.updateRoomBoss(key,randomEmail)
            }

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

    private fun getAcceptList():MutableList<AcceptData>{
        if (userDataMap.isEmpty()){
            return mutableListOf()
        }
        val acceptJson :String = userDataMap["acceptList"].toString()
        return Gson().fromJson(acceptJson,object : TypeToken<MutableList<AcceptData>>(){}.type)
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
        val acceptList = getAcceptList()
        for (accept in acceptList){
            if (accept.email == UserDataTool.getEmail()){
                acceptList.remove(accept)
                break
            }
        }
        db.updateAcceptList(key,acceptList)
        db.updateRoomUserList(key,userList){
            db.removeLiveCatch()
            finishPageLiveData.value = true
        }

    }

    fun onMessageSendClickListener(msg: String) {
        db.sendMsg(msg,key)
    }
    private fun isRoomBoss() : Boolean{
        if (userDataMap.isEmpty()){
            return false
        }
        val roomBoss =  userDataMap["roomBoss"].toString()
        return roomBoss == UserDataTool.getEmail()
    }

    private var subscription : Disposable? = null

    fun onReadyButtonClickListener() {
        if (isRoomBoss()){
            if (!isCanStartGame()){
                db.sendMsg("快點準備啦~要開始了",key)
                return
            }

            subscription = Observable.interval(1000, TimeUnit.MILLISECONDS)
                .take(5)
                .subscribeOn(Schedulers.io())
                .doOnComplete {
                    Log.i("Poker","開始遊戲")
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    if (!isCanStartGame()){
                        db.sendMsg("0x00100系統:有人沒按下準備,遊戲中斷",key)
                        subscription?.dispose()
                        mCompositeSubscription.remove(subscription!!)
                        return@subscribe
                    }
                    db.sendMsg("0x00100系統:遊戲即將開始",key)
                }
            subscription?.let {
                mCompositeSubscription.add(it)
            }


            return
        }


        var isFoundReady = false
        val acceptList = getAcceptList()
        for (accept in acceptList){
            if (accept.email == UserDataTool.getEmail() && accept.isAccept){
                acceptList.remove(accept)
                isFoundReady = true
                break
            }
        }
        if (!isFoundReady){
            acceptList.add(AcceptData(UserDataTool.getEmail(),true))
        }

        db.updateAcceptList(key,acceptList)

    }

    private fun isCanStartGame(): Boolean {
        var acceptCount = 0
        for (accept in getAcceptList()){
            if (accept.isAccept){
                acceptCount ++
            }
        }
        return acceptCount >= 0
    }

}