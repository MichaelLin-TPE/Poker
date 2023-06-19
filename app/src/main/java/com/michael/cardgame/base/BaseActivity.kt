package com.michael.cardgame.base

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

open class BaseActivity : AppCompatActivity() {

    private val viewModelFactory = ViewModelFactory()

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