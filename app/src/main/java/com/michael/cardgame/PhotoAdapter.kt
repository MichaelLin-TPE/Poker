package com.michael.cardgame

import android.provider.ContactsContract.Contacts.Photo
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.michael.cardgame.tool.Tool

class PhotoAdapter(private val dataList : ArrayList<Int>,private val onPhotoSelectedListener: OnPhotoSelectedListener) : RecyclerView.Adapter<PhotoAdapter.ViewHolder>() {

    private var selectPosition = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_photo_layout,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = dataList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val resId = dataList[position]
        holder.ivPhoto.setImageResource(resId)

        if (selectPosition == position){
            holder.ivPhoto.background = ContextCompat.getDrawable(Tool.getContext(),R.drawable.user_photo_bg)
        }else{
            holder.ivPhoto.background = null
        }

        holder.ivPhoto.setOnClickListener {
            onPhotoSelectedListener.onPhotoSelected(resId)
        }


    }

    fun setNewData(index: Int) {
        selectPosition = index
        notifyDataSetChanged()
    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivPhoto = itemView.findViewById<ImageView>(R.id.iv_photo)
    }


    fun interface OnPhotoSelectedListener{
        fun onPhotoSelected(redId:Int)
    }

}