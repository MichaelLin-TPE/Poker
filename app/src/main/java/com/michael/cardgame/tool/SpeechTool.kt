package com.michael.cardgame.tool

import android.app.Activity
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.TextToSpeech.OnInitListener
import android.util.Log
import java.util.Locale

object SpeechTool : OnInitListener{

    private lateinit var tts : TextToSpeech
    private var isTextToSpeechInitSuccessful = false

    fun init(activity: Activity){
        tts = TextToSpeech(activity,this)
    }
    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS){
            val result = tts.setLanguage(Locale.TRADITIONAL_CHINESE)
            isTextToSpeechInitSuccessful = !(result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED)
            Log.i("Poker","建置完成")
            if (isTextToSpeechInitSuccessful){
                makeSpeech(speechMsg)
            }
        }else{
            Log.i("Poker","建置失敗")
        }
    }
    private var speechMsg = ""
    fun makeSpeech(msg:String){
        speechMsg = msg
        if (isTextToSpeechInitSuccessful){
            val params = Bundle()
            tts.setSpeechRate(1.2f)
            tts.speak(msg,TextToSpeech.QUEUE_ADD,params,null)
        }else{
            Log.i("Poker","還沒建置完")
            speechMsg = msg
        }
    }
}