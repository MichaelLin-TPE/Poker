package com.weather.sunny.application

import android.app.Application
import com.michael.cardgame.tool.SpeechTool

class MyApplication : Application() {
    companion object{
        var instance : MyApplication? = null
    }
    override fun onCreate() {
        super.onCreate()
        instance = this
    }

}