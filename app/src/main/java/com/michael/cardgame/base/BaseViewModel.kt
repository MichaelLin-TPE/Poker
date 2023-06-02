package com.michael.cardgame.base

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.michael.cardgame.tool.Tool
import io.reactivex.disposables.CompositeDisposable

abstract class BaseViewModel(private val application: Application) : AndroidViewModel(application){

    protected val mCompositeSubscription = CompositeDisposable()

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