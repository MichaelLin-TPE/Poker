package com.michael.cardgame.dialog

import android.app.Dialog
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.michael.cardgame.R
import com.michael.cardgame.bean.CardData
import com.michael.cardgame.bean.LeftUserCardListData
import com.michael.cardgame.constants.Constants.MINE
import com.michael.cardgame.constants.Constants.POKER_2
import com.michael.cardgame.constants.Constants.USER_2
import com.michael.cardgame.constants.Constants.USER_3
import com.michael.cardgame.constants.Constants.USER_4
import com.michael.cardgame.custom.CardTextView
import com.michael.cardgame.tool.FirebaseDAO
import com.michael.cardgame.tool.Tool
import com.michael.cardgame.tool.Tool.convertDp
import com.michael.cardgame.tool.UserDataTool
import kotlin.math.abs

class MessageDialog : DialogFragment() {

    companion object {
        fun newInstance(title:String, content:String,confirmContent:String): MessageDialog {
            val args = Bundle()
            args.putString("title",title)
            args.putString("content",content)
            args.putString("confirm",confirmContent)
            val fragment = MessageDialog()
            fragment.arguments = args
            return fragment
        }

    }

    private lateinit var onMessageDialogItemClickListener: OnMessageDialogItemClickListener

    fun setOnMessageDialogItemClickListener(onMessageDialogItemClickListener: OnMessageDialogItemClickListener){
        this.onMessageDialogItemClickListener = onMessageDialogItemClickListener
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = activity?.layoutInflater
        val view = inflater?.inflate(R.layout.dialog_msg_layout, null)
        initView(view!!)
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(view)
        dialog.window?.setBackgroundDrawable(ColorDrawable(android.graphics.Color.TRANSPARENT))
        val window = dialog.window
        val wlp = window?.attributes
        wlp?.width = 300.convertDp()
        wlp?.height = 300.convertDp()
        window?.attributes = wlp
        return dialog
    }

    private fun initView(view: View) {
        val title = arguments?.getString("title")
        val content = arguments?.getString("content")
        val confirm = arguments?.getString("confirm")

        val tvTitle = view.findViewById<TextView>(R.id.title)
        val tvContent = view.findViewById<TextView>(R.id.tv_content)
        val tvConfirm = view.findViewById<TextView>(R.id.postive_btn)
        val tvCancel = view.findViewById<TextView>(R.id.cancel_btn)

        tvTitle.text = title
        tvContent.text = content
        tvConfirm.text = confirm

        tvCancel.setOnClickListener {
            dismiss()
        }

        tvConfirm.setOnClickListener {
            onMessageDialogItemClickListener.onConfirmClick()
            dismiss()
        }


    }

    fun interface OnMessageDialogItemClickListener{
        fun onConfirmClick()
    }

}