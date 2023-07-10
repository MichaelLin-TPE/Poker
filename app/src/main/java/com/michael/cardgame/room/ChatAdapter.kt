package com.michael.cardgame.room

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.michael.cardgame.R
import com.michael.cardgame.bean.MessageData
import com.michael.cardgame.tool.Tool
import com.michael.cardgame.tool.UserDataTool

class ChatAdapter(private val dataList:MutableList<MessageData>) : RecyclerView.Adapter<ChatAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_chat_msg_layout,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = dataList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val msgData = dataList[position]

        if (msgData.msg.startsWith("0x00100系統")){
            holder.tvUserMsg.visibility = View.INVISIBLE
            holder.tvUserName.visibility = View.INVISIBLE
            holder.tvMyMsg.visibility = View.INVISIBLE
            holder.tvSystem.visibility = View.VISIBLE
            holder.tvSystem.text = msgData.msg.replace("0x00100","")
            return
        }
        holder.tvSystem.visibility = View.GONE
        if (msgData.userName == UserDataTool.getUserName()){
            holder.tvUserMsg.visibility = View.INVISIBLE
            holder.tvUserName.visibility = View.INVISIBLE
            holder.tvMyMsg.visibility = View.VISIBLE
            holder.tvMyMsg.text = msgData.msg
        }else{
            holder.tvUserMsg.visibility = View.VISIBLE
            holder.tvUserName.visibility = View.VISIBLE
            holder.tvMyMsg.visibility = View.INVISIBLE
            holder.tvUserMsg.text = msgData.msg
            holder.tvUserName.text = "${msgData.userName} 說:"
        }

    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvUserName : TextView = itemView.findViewById(R.id.user_name)
        val tvUserMsg : TextView = itemView.findViewById(R.id.user_msg)
        val tvMyMsg : TextView = itemView.findViewById(R.id.my_msg)
        val tvSystem : TextView = itemView.findViewById(R.id.system_content)
    }
}