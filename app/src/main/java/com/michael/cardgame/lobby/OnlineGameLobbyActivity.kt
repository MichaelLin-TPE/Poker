package com.michael.cardgame.lobby

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.michael.cardgame.R
import com.michael.cardgame.base.BaseActivity
import com.michael.cardgame.databinding.ActivityOnlineGameLobbyBinding

class OnlineGameLobbyActivity : BaseActivity() {

    private lateinit var binding: ActivityOnlineGameLobbyBinding
    private lateinit var viewModel: OnlineGameLobbyViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_online_game_lobby)
        viewModel = getViewModel(OnlineGameLobbyViewModel::class.java)




    }
}