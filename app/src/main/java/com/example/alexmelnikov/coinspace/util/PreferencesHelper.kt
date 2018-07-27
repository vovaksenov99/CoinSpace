package com.example.alexmelnikov.coinspace.util

import android.content.Context
import android.preference.PreferenceManager

class PreferencesHelper(private val context: Context) {

    fun saveString(key: String, value: String) {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = preferences.edit()
        editor.putString(key, value)
        editor.commit()
    }

    fun loadString(key: String): String {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        return preferences.getString(key, "")
    }
}