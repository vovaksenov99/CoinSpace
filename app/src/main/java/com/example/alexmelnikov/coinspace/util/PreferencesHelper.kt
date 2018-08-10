package com.example.alexmelnikov.coinspace.util

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager

interface IPreferenceHelper {
    fun saveString(key: String, value: String)
    fun loadString(key: String): String
    fun saveFloat(key: String, value: Float)
    fun loadFloat(key: String): Float
    fun saveLong(key: String, value: Long)
    fun loadLong(key: String): Long
}

class PreferencesHelper(private val context: Context) : IPreferenceHelper {

    override fun saveString(key: String, value: String) {
        val preferences = context.getSharedPreferences("Prefs",Context.MODE_PRIVATE)
        val editor = preferences.edit()
        editor.putString(key, value)
        editor.apply()
    }

    override fun loadString(key: String): String {
        val preferences = context.getSharedPreferences("Prefs",Context.MODE_PRIVATE)
        return preferences.getString(key, "")
    }

    override fun saveFloat(key: String, value: Float) {
        val preferences = context.getSharedPreferences("Prefs",Context.MODE_PRIVATE)
        val editor = preferences.edit()
        editor.putFloat(key, value)
        editor.apply()
    }

    override fun loadFloat(key: String): Float {
        val preferences = context.getSharedPreferences("Prefs",Context.MODE_PRIVATE)
        return preferences.getFloat(key, 0F)
    }

    override fun saveLong(key: String, value: Long) {
        val preferences = context.getSharedPreferences("Prefs",Context.MODE_PRIVATE)
        val editor = preferences.edit()
        editor.putLong(key, value)
        editor.apply()
    }

    override fun loadLong(key: String): Long {
        val preferences = context.getSharedPreferences("Prefs",Context.MODE_PRIVATE)
        return preferences.getLong(key, 0L)
    }

}