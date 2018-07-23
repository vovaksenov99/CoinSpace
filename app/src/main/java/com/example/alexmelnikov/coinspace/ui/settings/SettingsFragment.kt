package com.example.alexmelnikov.coinspace.ui.settings

import com.example.alexmelnikov.coinspace.R.xml.preferences
import android.os.Bundle
import android.support.v7.preference.PreferenceFragmentCompat
import android.util.Log
import android.util.TypedValue
import com.example.alexmelnikov.coinspace.R


class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
    }

}
