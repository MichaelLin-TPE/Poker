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
import com.michael.cardgame.databinding.DialogCreateGameLayoutBinding
import com.michael.cardgame.tool.FirebaseDAO
import com.michael.cardgame.tool.Tool
import com.michael.cardgame.tool.Tool.convertDp
import com.michael.cardgame.tool.UserDataTool
import kotlin.math.abs

class CreateGameDialog : DialogFragment() {


    companion object {
        fun newInstance(): CreateGameDialog {
            val args = Bundle()
            val fragment = CreateGameDialog()
            fragment.arguments = args
            return fragment
        }
    }

    private lateinit var onCreateGameConfirmListener: OnCreateGameConfirmListener

    fun setOnCreateGameConfirmListener(onCreateGameConfirmListener: OnCreateGameConfirmListener){
        this.onCreateGameConfirmListener = onCreateGameConfirmListener
    }

    private lateinit var binding : DialogCreateGameLayoutBinding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = activity?.layoutInflater
        val view = inflater?.inflate(R.layout.dialog_create_game_layout, null)
        initView(view!!)
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(view)
        dialog.window?.setBackgroundDrawable(ColorDrawable(android.graphics.Color.TRANSPARENT))
        val window = dialog.window
        val wlp = window?.attributes
        wlp?.width = 400.convertDp()
        wlp?.height = 230.convertDp()
        window?.attributes = wlp
        return dialog
    }

    private fun initView(view: View) {

        binding = DialogCreateGameLayoutBinding.bind(view)

        binding.createGameBtn.setOnClickListener {
            if (binding.edGameName.text.toString().isEmpty()){
                Tool.showToast("請輸入房間名稱")
                return@setOnClickListener
            }
            if (binding.edBettingValue.text.toString().isEmpty()){
                Tool.showToast("你要賭多大?")
                return@setOnClickListener
            }
            val gameName = binding.edGameName.text.toString()
            val bettingValue = binding.edBettingValue.text.toString().toInt()
            onCreateGameConfirmListener.onCreateGameConfirm(gameName,bettingValue)
            dismiss()
        }

    }

    fun interface OnCreateGameConfirmListener{
        fun onCreateGameConfirm(gameName:String,bettingValue:Int)
    }

}