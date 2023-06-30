package com.michael.cardgame.base

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.michael.cardgame.dialog.MessageDialog
import com.michael.cardgame.tool.FirebaseDAO
import com.michael.cardgame.tool.SoundTool

open class BaseActivity : AppCompatActivity() {

    private val viewModelFactory = ViewModelFactory()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SoundTool.initSoundPool(this)

    }

    override fun onResume() {
        super.onResume()

    }

    override fun onStop() {
        super.onStop()
    }

    fun showMessageDialog(title:String,content:String,confirm:String,onMessageDialogItemClickListener: MessageDialog.OnMessageDialogItemClickListener){
        val dialog = MessageDialog.newInstance(title,content,confirm)
        dialog.show(supportFragmentManager,"dialog")
        dialog.setOnMessageDialogItemClickListener{
            onMessageDialogItemClickListener.onConfirmClick()
        }
    }

    fun <T : ViewModel> getViewModel(viewModel: Class<T>): T {
        return ViewModelProvider(this, viewModelFactory)[viewModel]
    }

    fun setBindingView(resId:Int) : ViewDataBinding{
        return DataBindingUtil.setContentView(this,resId)
    }

    fun <T : BaseActivity> goToPage(activity: Class<T>) {
        val intent = Intent(this, activity)
        startActivity(intent)
    }
}