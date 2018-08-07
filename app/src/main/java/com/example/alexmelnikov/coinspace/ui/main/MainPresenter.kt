package com.example.alexmelnikov.coinspace.ui.main

import android.content.Context
import android.view.View
import com.example.alexmelnikov.coinspace.PreferenceStructure
import com.example.alexmelnikov.coinspace.ui.home.HomePresenter
import com.example.alexmelnikov.coinspace.ui.home.OperationFragment
import com.example.alexmelnikov.coinspace.util.PreferencesHelper

class MainPresenter : MainContract.Presenter {

    private lateinit var view: MainContract.View

    override lateinit var activeFragmentOnScreen: MainContract.FragmentOnScreen


    override fun attach(view: MainContract.View) {
        this.view = view
    }

    override fun start(activeFragmentOnScreen: MainContract.FragmentOnScreen) {
        when (activeFragmentOnScreen) {
            MainContract.FragmentOnScreen.HOME -> openHomeFragmentRequest()
            MainContract.FragmentOnScreen.ACCOUNTS -> openAccountsFragmentRequest()
            MainContract.FragmentOnScreen.STATISTICS -> openStatisticsFragmentRequest(null)
        }
    }

    override fun openHomeFragmentRequest() {
        activeFragmentOnScreen = MainContract.FragmentOnScreen.HOME
        view.openHomeFragment()
    }

    override fun openAccountsFragmentRequest() {
        activeFragmentOnScreen = MainContract.FragmentOnScreen.ACCOUNTS
        view.openAccountsFragment()
    }

    override fun openStatisticsFragmentRequest(animationCenter: View?) {
        activeFragmentOnScreen = MainContract.FragmentOnScreen.STATISTICS
        view.openStatisticsFragment(animationCenter)
    }

    override fun openIntroductionDialog(context: Context)
    {
        val pref = PreferencesHelper(context)
        if(pref.loadString(PreferenceStructure.LAST_APP_LOGIN) == "") {
            view.openIntroductionDialog()
        }
    }
}