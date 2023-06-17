package com.michael.cardgame.tool

import android.content.Context
import android.content.SharedPreferences

object CacheTool {

    private var sharedPreferences: SharedPreferences? = null

    private fun getSharedPreferences(): SharedPreferences {
        if (sharedPreferences == null) {
            sharedPreferences =
                Tool.getContext().getSharedPreferences("user_data", Context.MODE_PRIVATE)
            return sharedPreferences!!
        }
        return sharedPreferences!!
    }

    fun getBoolean(key: String, defaultValue: Boolean): Boolean {
        return getSharedPreferences().getBoolean(key, defaultValue)
    }

    fun getString(key: String, defaultValue: String): String {
        return getSharedPreferences().getString(key, defaultValue)!!
    }

    fun getInt(key: String, defaultValue: Int): Int {
        return getSharedPreferences().getInt(key, defaultValue)
    }

    fun putString(key: String, value: String) {
        getSharedPreferences().edit().putString(key, value).apply()
    }

    fun putBoolean(key: String, value: Boolean) {
        getSharedPreferences().edit().putBoolean(key, value).apply()
    }

    fun putInt(key: String, value: Int) {
        getSharedPreferences().edit().putInt(key, value).apply()
    }


}