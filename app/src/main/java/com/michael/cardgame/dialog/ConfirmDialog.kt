package com.michael.cardgame.dialog

import android.app.Dialog
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
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
import com.michael.cardgame.constants.Constants.POKER_2
import com.michael.cardgame.constants.Constants.USER_2
import com.michael.cardgame.constants.Constants.USER_3
import com.michael.cardgame.constants.Constants.USER_4
import com.michael.cardgame.custom.CardTextView
import com.michael.cardgame.tool.Tool
import com.michael.cardgame.tool.Tool.convertDp
import com.michael.cardgame.tool.UserDataTool
import kotlin.math.abs

class ConfirmDialog : DialogFragment() {

    companion object {
        fun newInstance(dataList: ArrayList<LeftUserCardListData>): ConfirmDialog {
            val args = Bundle()
            args.putSerializable("data", dataList)
            val fragment = ConfirmDialog()
            fragment.arguments = args
            return fragment
        }

    }

    private lateinit var onPlayAgainClickListener: OnPlayAgainClickListener

    fun setOnPlayAgainClickListener(onPlayAgainClickListener: OnPlayAgainClickListener) {
        this.onPlayAgainClickListener = onPlayAgainClickListener
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = activity?.layoutInflater
        val view = inflater?.inflate(R.layout.dialog_confirm_layout, null)
        initView(view!!)
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(view)
        dialog.window?.setBackgroundDrawable(ColorDrawable(android.graphics.Color.TRANSPARENT))
        val window = dialog.window
        val wlp = window?.attributes
        wlp?.width = 650.convertDp()
        wlp?.height = 400.convertDp()
        window?.attributes = wlp
        return dialog
    }

    private fun initView(view: View) {
        isCancelable = false
        val dataList = arguments?.getSerializable("data") as ArrayList<LeftUserCardListData>
        val tvPlayAgain = view.findViewById<TextView>(R.id.tv_confirm)
        val ivUser1Photo = view.findViewById<ImageView>(R.id.iv_user1)
        val ivUser2Photo = view.findViewById<ImageView>(R.id.iv_user2)
        val ivUser3Photo = view.findViewById<ImageView>(R.id.iv_user3)
        val ivUser4Photo = view.findViewById<ImageView>(R.id.iv_user4)
        ivUser1Photo.setImageResource(UserDataTool.getUserPhoto())
        ivUser2Photo.setImageResource(UserDataTool.getBot2Photo())
        ivUser3Photo.setImageResource(UserDataTool.getBot3Photo())
        ivUser4Photo.setImageResource(UserDataTool.getBot4Photo())



        val user1ListView = view.findViewById<LinearLayout>(R.id.user1_card_list_view)
        val user2ListView = view.findViewById<LinearLayout>(R.id.user2_card_list_view)
        val user3ListView = view.findViewById<LinearLayout>(R.id.user3_card_list_view)
        val user4ListView = view.findViewById<LinearLayout>(R.id.user4_card_list_view)

        val user1Win = view.findViewById<CardTextView>(R.id.tv_user1_win)
        val user2Win = view.findViewById<CardTextView>(R.id.tv_user2_win)
        val user3Win = view.findViewById<CardTextView>(R.id.tv_user3_win)
        val user4Win = view.findViewById<CardTextView>(R.id.tv_user4_win)

        var user1Amount = 0
        var user2Amount = 0
        var user3Amount = 0
        var user4Amount = 0


        for (data in dataList) {
            if (data.userNum == MINE) {
                user1Win.visibility = if (data.cardList.isNotEmpty()) View.GONE else View.VISIBLE
                user1Amount =
                    if (user1Win.visibility == View.VISIBLE) 0 else checkLostAmount(data.cardList)
                addView(user1ListView, data.cardList)
            }
            if (data.userNum == USER_2) {
                user2Win.visibility = if (data.cardList.isNotEmpty()) View.GONE else View.VISIBLE
                user2Amount =
                    if (user2Win.visibility == View.VISIBLE) 0 else checkLostAmount(data.cardList)
                addView(user2ListView, data.cardList)
            }
            if (data.userNum == USER_3) {
                user3Win.visibility = if (data.cardList.isNotEmpty()) View.GONE else View.VISIBLE
                user3Amount =
                    if (user3Win.visibility == View.VISIBLE) 0 else checkLostAmount(data.cardList)
                addView(user3ListView, data.cardList)
            }
            if (data.userNum == USER_4) {
                user4Win.visibility = if (data.cardList.isNotEmpty()) View.GONE else View.VISIBLE
                user4Amount =
                    if (user4Win.visibility == View.VISIBLE) 0 else checkLostAmount(data.cardList)
                addView(user4ListView, data.cardList)
            }
        }
        val isUser1Win = user1Win.visibility == View.VISIBLE
        val isUser2Win = user2Win.visibility == View.VISIBLE
        val isUser3Win = user3Win.visibility == View.VISIBLE
        val isUser4Win = user4Win.visibility == View.VISIBLE
        if (isUser1Win) {
            UserDataTool.saveUserCashAmount(user2Amount + user3Amount + user4Amount + UserDataTool.getUserCashAmount())
            UserDataTool.saveBot2CashAmount(UserDataTool.getBot2CashAmount() - user2Amount)
            UserDataTool.saveBot3CashAmount(UserDataTool.getBot3CashAmount() - user3Amount)
            UserDataTool.saveBot4CashAmount(UserDataTool.getBot4CashAmount() - user4Amount)
            user1Win.text =
                "${getString(R.string.win)} , +${Tool.formatThousand(abs(user2Amount + user3Amount + user4Amount))}"
        }
        if (isUser2Win) {
            UserDataTool.saveUserCashAmount(UserDataTool.getUserCashAmount() - user1Amount)
            UserDataTool.saveBot2CashAmount(user1Amount + user3Amount + user4Amount + UserDataTool.getBot2CashAmount())
            UserDataTool.saveBot3CashAmount(UserDataTool.getBot3CashAmount() - user3Amount)
            UserDataTool.saveBot4CashAmount(UserDataTool.getBot4CashAmount() - user4Amount)
            user2Win.text =
                "${getString(R.string.win)} , +${Tool.formatThousand(abs(user1Amount + user3Amount + user4Amount))}"
        }
        if (isUser3Win) {
            UserDataTool.saveUserCashAmount(UserDataTool.getUserCashAmount() - user1Amount)
            UserDataTool.saveBot2CashAmount(UserDataTool.getBot2CashAmount() - user2Amount)
            UserDataTool.saveBot3CashAmount(user1Amount + user2Amount + user4Amount + UserDataTool.getBot2CashAmount())
            UserDataTool.saveBot4CashAmount(UserDataTool.getBot4CashAmount() - user4Amount)
            user3Win.text =
                "${getString(R.string.win)} , +${Tool.formatThousand(abs(user1Amount + user2Amount + user4Amount))}"
        }
        if (isUser4Win) {
            UserDataTool.saveUserCashAmount(UserDataTool.getUserCashAmount() - user1Amount)
            UserDataTool.saveBot2CashAmount(UserDataTool.getBot2CashAmount() - user2Amount)
            UserDataTool.saveBot3CashAmount(UserDataTool.getBot3CashAmount() - user3Amount)
            UserDataTool.saveBot4CashAmount(user1Amount + user2Amount + user3Amount + UserDataTool.getBot2CashAmount())
            user4Win.text =
                "${getString(R.string.win)} , +${Tool.formatThousand(abs(user2Amount + user3Amount + user1Amount))}"
        }



        tvPlayAgain.setOnClickListener {
            onPlayAgainClickListener.playAgain()
            dismiss()
        }
    }

    private fun checkLostAmount(cardList: MutableList<CardData>): Int {
        val cardOverEightDouble = cardList.size >= 8
        var bigTwoCount = 0
        for (data in cardList) {
            if (data.cardValue == POKER_2) {
                bigTwoCount++
            }
        }
        var totalAmount = if (cardOverEightDouble) (cardList.size * 100) * 2 else cardList.size * 100
        totalAmount = if (bigTwoCount != 0) totalAmount * (bigTwoCount * 2) else totalAmount
        return totalAmount
    }

    private fun addView(user1ListView: LinearLayout, cardList: MutableList<CardData>) {
        for (data in cardList) {
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

    fun interface OnPlayAgainClickListener {
        fun playAgain()
    }

}