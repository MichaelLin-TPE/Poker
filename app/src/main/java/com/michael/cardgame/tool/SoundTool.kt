package com.michael.cardgame.tool

import android.app.Activity
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.SoundPool
import com.michael.cardgame.R

object SoundTool {

    private lateinit var dealCardPool:SoundPool
    private var dealCardId = 0
    private var shufflingCardMusic : MediaPlayer? = null
    private var backgroundMusic : MediaPlayer? = null


    fun initSoundPool(activity: Activity){
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()
        dealCardPool = SoundPool.Builder()
            .setMaxStreams(10)
            .setAudioAttributes(audioAttributes)
            .build()
        dealCardId = dealCardPool.load(activity, R.raw.deal_card_music,1)
    }


    fun playDealCardMusic(){
        dealCardPool.play(dealCardId,1f,1f,1,0,1f)
    }

    fun playShufflingCardMusic(activity: Activity){
        if (shufflingCardMusic == null) {
            shufflingCardMusic = MediaPlayer.create(activity, R.raw.shuffling_cards_music)
            shufflingCardMusic?.setVolume(1f, 1f)
            shufflingCardMusic?.isLooping = true
        }
        shufflingCardMusic?.start()
    }
    fun playBackgroundMusic(activity: Activity){
        if (backgroundMusic == null){
            backgroundMusic = MediaPlayer.create(activity,R.raw.bg_music)
            backgroundMusic?.setVolume(0.2f,0.2f)
            backgroundMusic?.isLooping = true
        }
        backgroundMusic?.start()
    }

    fun stopShufflingCardMusic(){
        shufflingCardMusic?.stop()
    }

    fun stopBackgroundMusic() {
        backgroundMusic?.pause()
    }

}