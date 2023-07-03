package com.michael.cardgame.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.michael.cardgame.LauncherViewModel
import com.michael.cardgame.big_two.BigTwoViewModel
import com.michael.cardgame.home.HomeViewModel
import com.michael.cardgame.lobby.OnlineGameLobbyViewModel
import com.michael.cardgame.room.RoomViewModel
import com.weather.sunny.application.MyApplication

class ViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RoomViewModel::class.java)){
            MyApplication.instance?.let {
                return RoomViewModel(it) as T
            }
        }
        if (modelClass.isAssignableFrom(OnlineGameLobbyViewModel::class.java)){
            MyApplication.instance?.let {
                return OnlineGameLobbyViewModel(it) as T
            }
        }
        if (modelClass.isAssignableFrom(BigTwoViewModel::class.java)){
            MyApplication.instance?.let {
                return BigTwoViewModel(it) as T
            }
        }
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)){
            MyApplication.instance?.let {
                return HomeViewModel(it) as T
            }
        }

        if (modelClass.isAssignableFrom(LauncherViewModel::class.java)){
            MyApplication.instance?.let {
                return LauncherViewModel(it) as T
            }
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}