package com.michael.cardgame.base

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.michael.cardgame.tool.FirebaseDAO
import com.michael.cardgame.tool.Tool
import io.reactivex.disposables.CompositeDisposable

abstract class BaseViewModel(private val application: Application) : AndroidViewModel(application){

    protected val mCompositeSubscription = CompositeDisposable()
    protected val db = FirebaseDAO()

    init {
        db.init(Firebase.firestore)
    }


    fun onStop() {
        db.stopConnectFirebase()
    }

    fun onResume() {
        db.startToConnectFirebase()
    }
    fun onCatchOnlineUsers(onCatchOnlineUsersListener: FirebaseDAO.OnCatchOnlineUsersListener){

        db.setOnCatchOnlineUsersListener{
            onCatchOnlineUsersListener.onCatchOnlineUsersCount(it)
        }
    }
    open fun showErrorMsg(msg:String){
        Tool.showToast(msg)
    }
    fun Int.toString(resId:Int):String{
        return application.getString(resId)
    }

    open fun handleCoroutineException(throwable: Throwable) {
        // 處理協程異常的共享邏輯
    }
    fun clearCompositeDisposable(){
        mCompositeSubscription.dispose()
    }


}