package com.michael.cardgame.base

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

open class BaseActivity : AppCompatActivity() {

    private val viewModelFactory = ViewModelFactory()

    fun <T : ViewModel> getViewModel(viewModel :Class<T>) : T{
        return ViewModelProvider(this,viewModelFactory)[viewModel]
    }
}