package com.example.alexmelnikov.coinspace

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.support.v7.preference.PreferenceManager.getDefaultSharedPreferences
import com.example.alexmelnikov.coinspace.util.IPreferenceHelper
import com.example.alexmelnikov.coinspace.util.PreferencesHelper
import com.nhaarman.mockito_kotlin.whenever
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.Mockito

class PreferenceHelperTests
{

    val sharedPrefs = Mockito.mock(SharedPreferences::class.java)
    val editor = Mockito.mock(SharedPreferences.Editor::class.java)

    val context = Mockito.mock(Context::class.java)

    @Test
    fun saveStringSharedPefs()
    {
        val h = PreferencesHelper(context)
        whenever(context.getSharedPreferences("Prefs",Context.MODE_PRIVATE)).thenReturn(sharedPrefs)
        whenever(sharedPrefs.edit()).thenReturn(editor)
        h.saveString("query","К а к  ж е  я  л ю б л ю  а в т о т е с т ы  в  3:16 AM")
        whenever(sharedPrefs.getString("query","")).thenReturn("К а к  ж е  я  л ю б л ю  а в т о т е с т ы  в  3:16 AM")
        assertEquals("К а к  ж е  я  л ю б л ю  а в т о т е с т ы  в  3:16 AM",h.loadString("query"))
    }


    @Test
    fun saveFloatSharedPefs()
    {
        val h = PreferencesHelper(context)
        whenever(context.getSharedPreferences("Prefs",Context.MODE_PRIVATE)).thenReturn(sharedPrefs)
        whenever(sharedPrefs.edit()).thenReturn(editor)
        h.saveFloat("query",34f)
        whenever(sharedPrefs.getFloat("query",0f)).thenReturn(34f)
        assertEquals(34f,h.loadFloat("query"))
    }

    @Test
    fun saveLongSharedPefs()
    {
        val h = PreferencesHelper(context)
        whenever(context.getSharedPreferences("Prefs",Context.MODE_PRIVATE)).thenReturn(sharedPrefs)
        whenever(sharedPrefs.edit()).thenReturn(editor)
        h.saveLong("query",34L)
        whenever(sharedPrefs.getLong("query",0L)).thenReturn(34L)
        assertEquals(34L,h.loadLong("query"))
    }


}