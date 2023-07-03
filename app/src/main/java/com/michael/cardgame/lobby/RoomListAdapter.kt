package com.michael.cardgame.lobby

import android.util.Log
import android.view.LayoutInflater
import android.view.ScrollCaptureCallback
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.michael.cardgame.R
import com.michael.cardgame.bean.RoomData
import com.michael.cardgame.bean.UserData

class RoomListAdapter(private val dataList : MutableList<RoomData>,private val callback: (key:String,userList:MutableList<UserData>)->Unit) : RecyclerView.Adapter<RoomListAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_room_layout,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = dataList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = dataList[position]
        holder.tvName.text = data.name
        holder.tvBettingValue.text = "一張牌$${data.bettingValue}"
        holder.tvUserCount.text = "目前人數 ${data.userCount} 人"
        holder.tvJoinGame.setOnClickListener {
            callback(data.key,data.userList)
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName = itemView.findViewById<TextView>(R.id.room_title)
        val tvBettingValue = itemView.findViewById<TextView>(R.id.betting_value)
        val tvUserCount = itemView.findViewById<TextView>(R.id.user_count)
        val tvJoinGame = itemView.findViewById<TextView>(R.id.join_game_btn)
    }

}