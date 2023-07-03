package com.michael.cardgame.lobby

import android.content.Intent
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.michael.cardgame.R
import com.michael.cardgame.base.BaseActivity
import com.michael.cardgame.databinding.ActivityOnlineGameLobbyBinding
import com.michael.cardgame.dialog.CreateGameDialog
import com.michael.cardgame.room.RoomActivity

class OnlineGameLobbyActivity : BaseActivity() {

    private lateinit var binding: ActivityOnlineGameLobbyBinding
    private lateinit var viewModel: OnlineGameLobbyViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_online_game_lobby)
        viewModel = getViewModel(OnlineGameLobbyViewModel::class.java)



        initView()
        handleLiveData()
        viewModel.onCreate()
    }

    private fun handleLiveData() {

        viewModel.showCashDiamondLiveData.observe(this){
            binding.tvCash.text = it.first.toString()
            binding.tvDiamond.text = it.second.toString()
        }

        viewModel.showUserNameAndPhotoLiveData.observe(this){
            binding.tvUsername.text = it.first
            binding.ivPhoto.setImageResource(it.second)
        }
        viewModel.showCreateGameDialogLiveData.observe(this){
            val dialog = CreateGameDialog.newInstance()
            dialog.show(supportFragmentManager,"dialog")
            dialog.setOnCreateGameConfirmListener{ gameName,bettingValue->
                viewModel.onStartToCreateGame(gameName,bettingValue)
            }
        }
        viewModel.goToGameRoomActivityLiveData.observe(this){key->
            val intent = Intent(this@OnlineGameLobbyActivity,RoomActivity::class.java)
            intent.putExtra("key",key)
            startActivity(intent)

        }

        viewModel.showRoomListLiveData.observe(this){
            val adapter = RoomListAdapter(it){key,userList ->
                viewModel.onRoomJoinClickListener(key,userList)
            }
            binding.gameList.adapter = adapter

        }
    }

    private fun initView() {
        binding.ivBack.setOnClickListener {
            finish()
        }

        binding.icCreateGame.setOnClickListener {
            viewModel.onCreateGameClickListener()
        }

        binding.icQuickJoin.setOnClickListener {

        }


    }
}