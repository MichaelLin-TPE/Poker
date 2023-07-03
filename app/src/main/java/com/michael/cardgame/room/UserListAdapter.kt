package com.michael.cardgame.room

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.michael.cardgame.R
import com.michael.cardgame.bean.UserData

class UserListAdapter(private val dataList : MutableList<UserData>) : RecyclerView.Adapter<UserListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_user_layout,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = dataList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int){
        val data = dataList[position]
        holder.ivPhoto.setImageResource(data.photoId)
        holder.tvName.text = data.name
    }
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivPhoto: ImageView = itemView.findViewById(R.id.ic_photo)
        val tvName: TextView = itemView.findViewById(R.id.name)
    }
}