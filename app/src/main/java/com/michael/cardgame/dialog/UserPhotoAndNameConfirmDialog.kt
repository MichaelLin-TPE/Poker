package com.michael.cardgame.dialog

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.RecyclerView
import com.michael.cardgame.PhotoAdapter
import com.michael.cardgame.R
import com.michael.cardgame.tool.Tool.convertDp

class UserPhotoAndNameConfirmDialog : DialogFragment() {
    companion object {
        fun newInstance(): UserPhotoAndNameConfirmDialog {
            val args = Bundle()
            val fragment = UserPhotoAndNameConfirmDialog()
            fragment.arguments = args
            return fragment
        }

    }

    private lateinit var onSetUpUserDataListener: OnSetUpUserDataListener

    fun setOnSetUpUserDataListener(onSetUpUserDataListener: OnSetUpUserDataListener){
        this.onSetUpUserDataListener = onSetUpUserDataListener
    }


    private var photoId = 0

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = activity?.layoutInflater
        val view = inflater?.inflate(R.layout.dialog_user_photo_and_name_confirm_layout, null)
        initView(view!!)
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(view)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val window = dialog.window
        val wlp = window?.attributes
        wlp?.width = 300.convertDp()
        wlp?.height = 330.convertDp()
        window?.attributes = wlp
        return dialog
    }
    private lateinit var adapter: PhotoAdapter

    private fun initView(view: View) {
        isCancelable = false
        val rvList = view.findViewById<RecyclerView>(R.id.rv_photo_list)
        val edName = view.findViewById<EditText>(R.id.ed_name)
        val tvSubmit = view.findViewById<TextView>(R.id.tv_submit)

        val dataList = ArrayList<Int>()
        dataList.add(R.drawable.ic_boy)
        dataList.add(R.drawable.ic_boy1)
        dataList.add(R.drawable.ic_boy2)
        dataList.add(R.drawable.ic_boy4)
        dataList.add(R.drawable.ic_girl)
        dataList.add(R.drawable.ic_girl1)
        dataList.add(R.drawable.ic_girl3)
        dataList.add(R.drawable.ic_girl4)

        adapter = PhotoAdapter(dataList){
            for ((index,resId) in dataList.withIndex()){
                if (resId == it){
                    adapter.setNewData(index)
                    break
                }
            }
            photoId = it
        }
        rvList.adapter = adapter

        tvSubmit.setOnClickListener {
            onSetUpUserDataListener.onSubmitUserData(edName.text.toString(),photoId)
        }
    }

    fun interface OnSetUpUserDataListener{
        fun onSubmitUserData(userName:String,resId:Int)
    }

}