package com.michael.cardgame.room

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.michael.cardgame.R
import com.michael.cardgame.base.BaseActivity
import com.michael.cardgame.databinding.ActivityRoomBinding

class RoomActivity : BaseActivity() {

    private lateinit var binding : ActivityRoomBinding
    private lateinit var viewModel: RoomViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_room)
        viewModel = getViewModel(RoomViewModel::class.java)
        initView()
        handleLiveData()
        viewModel.onCreate(intent?.extras?.getString("key")!!)
    }

    private fun handleLiveData() {
        viewModel.showChatListLiveData.observe(this){
            val adapter = ChatAdapter(it)
            binding.rvMsgList.adapter = adapter
            binding.rvMsgList.scrollToPosition(it.size - 1)
        }
        viewModel.showUserListLiveData.observe(this){
            val adapter = UserListAdapter(it)
            binding.rvUserList.adapter = adapter
        }
        viewModel.showGameNameLiveData.observe(this){
            binding.roomTitle.text = it
        }
        viewModel.finishPageLiveData.observe(this){
            finish()
        }
    }

    private fun initView() {
        binding.ivBack.setOnClickListener {
            viewModel.onBackClickListener()
        }

        binding.tvSend.setOnClickListener {
            val msg = binding.edMsg.text.toString()
            viewModel.onMessageSendClickListener(msg)
            binding.edMsg.setText("")
        }
    }
}