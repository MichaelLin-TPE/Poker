package com.michael.cardgame.base

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.michael.cardgame.big_two.BigTwoActivity

open class BaseActivity : AppCompatActivity() {

    private val viewModelFactory = ViewModelFactory()

    fun <T : ViewModel> getViewModel(viewModel: Class<T>): T {
        return ViewModelProvider(this, viewModelFactory)[viewModel]
    }

    fun <T : BaseActivity> goToPage(activity: Class<T>) {
        val intent = Intent(this, activity)
        startActivity(intent)
    }
}