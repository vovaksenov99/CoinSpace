package com.example.alexmelnikov.coinspace.ui.settings

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v7.preference.PreferenceFragmentCompat
import android.support.v7.preference.PreferenceManager
import android.view.View
import com.example.alexmelnikov.coinspace.BaseApp
import com.example.alexmelnikov.coinspace.R
import com.example.alexmelnikov.coinspace.model.interactors.IUserBalanceInteractor
import javax.inject.Inject


class SettingsFragment : PreferenceFragmentCompat(), SharedPreferences.OnSharedPreferenceChangeListener {

    @Inject
    lateinit var userBalanceInteractor: IUserBalanceInteractor

    private lateinit var currencyBeforeChange: String

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        BaseApp.instance.component.inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        currencyBeforeChange = PreferenceManager.getDefaultSharedPreferences(activity as SettingsActivity)
                .getString(resources.getString(R.string.sp_key_main_currency), "")
    }

    override fun onSharedPreferenceChanged(sp: SharedPreferences?, key: String?) {
        if (key == resources.getString(R.string.sp_key_main_currency))
            userBalanceInteractor.mainCurrencyChanged(currencyBeforeChange)
    }

    override fun onResume() {
        super.onResume()
        preferenceScreen.sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onPause() {
        super.onPause()
        preferenceScreen.sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
    }
}
