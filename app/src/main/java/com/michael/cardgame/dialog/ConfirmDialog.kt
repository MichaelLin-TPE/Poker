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
import com.michael.cardgame.R
import com.michael.cardgame.bean.CardData
import com.michael.cardgame.bean.LeftUserCardListData
import com.michael.cardgame.constants.Constants.MINE
import com.michael.cardgame.constants.Constants.USER_2
import com.michael.cardgame.constants.Constants.USER_3
import com.michael.cardgame.constants.Constants.USER_4
import com.michael.cardgame.custom.CardTextView
import com.michael.cardgame.tool.Tool
import com.michael.cardgame.tool.Tool.convertDp

class ConfirmDialog : DialogFragment() {

    companion object{
        fun newInstance(dataList : ArrayList<LeftUserCardListData>): ConfirmDialog {
            val args = Bundle()
            args.putSerializable("data",dataList)
            val fragment = ConfirmDialog()
            fragment.arguments = args
            return fragment
        }

    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = activity?.layoutInflater
        val view = inflater?.inflate(R.layout.dialog_confirm_layout, null)
        initView(view!!)
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(view)
        dialog.window?.setBackgroundDrawable(ColorDrawable(android.graphics.Color.TRANSPARENT))
        dialog.setCancelable(false)
        val window = dialog.window
        val wlp = window?.attributes
        wlp?.width = 650.convertDp()
        wlp?.height = 400.convertDp()
        window?.attributes = wlp
        return dialog
    }

    private fun initView(view: View) {

        val dataList = arguments?.getSerializable("data") as ArrayList<LeftUserCardListData>

        val user1ListView = view.findViewById<LinearLayout>(R.id.user1_card_list_view)
        val user2ListView = view.findViewById<LinearLayout>(R.id.user2_card_list_view)
        val user3ListView = view.findViewById<LinearLayout>(R.id.user3_card_list_view)
        val user4ListView = view.findViewById<LinearLayout>(R.id.user4_card_list_view)

        val user1Win = view.findViewById<CardTextView>(R.id.tv_user1_win)
        val user2Win = view.findViewById<CardTextView>(R.id.tv_user2_win)
        val user3Win = view.findViewById<CardTextView>(R.id.tv_user3_win)
        val user4Win = view.findViewById<CardTextView>(R.id.tv_user4_win)


        for (data in dataList){
            if (data.userNum == MINE){
                user1Win.visibility = if (data.cardList.isNotEmpty()) View.GONE else View.VISIBLE
                addView(user1ListView,data.cardList)
            }
            if (data.userNum == USER_2){
                user2Win.visibility = if (data.cardList.isNotEmpty()) View.GONE else View.VISIBLE
                addView(user2ListView,data.cardList)
            }
            if (data.userNum == USER_3){
                user3Win.visibility = if (data.cardList.isNotEmpty()) View.GONE else View.VISIBLE
                addView(user3ListView,data.cardList)
            }
            if (data.userNum == USER_4){
                user4Win.visibility = if (data.cardList.isNotEmpty()) View.GONE else View.VISIBLE
                addView(user4ListView,data.cardList)
            }
        }

    }

    private fun addView(user1ListView: LinearLayout, cardList: MutableList<CardData>) {
        for (data in cardList){
            val view = View.inflate(requireContext(), R.layout.item_poker_car_layout_smaller, null)
            user1ListView.addView(view)
            val tvNumber1 = view.findViewById<TextView>(R.id.tv_number_1)
            val tvNumber2 = view.findViewById<TextView>(R.id.tv_number_2)
            val ivFlavor1 = view.findViewById<ImageView>(R.id.iv_flavor)
            val ivFlavor2 = view.findViewById<ImageView>(R.id.iv_flavor1)
            val ivCenterImage = view.findViewById<ImageView>(R.id.iv_flavor1_main)
            tvNumber2.text = data.cardNumber
            tvNumber1.text = data.cardNumber
            ivFlavor1.setImageResource(data.cardImage)
            ivFlavor2.setImageResource(data.cardImage)
            ivCenterImage.setImageResource(data.centerImage)
            view.visibility = View.INVISIBLE
            view.post {
                val layoutParams = view.layoutParams
                layoutParams.width = 35.convertDp()
                layoutParams.height = 60.convertDp()
                view.layoutParams = layoutParams
                view.visibility = View.VISIBLE
            }
        }

    }

}