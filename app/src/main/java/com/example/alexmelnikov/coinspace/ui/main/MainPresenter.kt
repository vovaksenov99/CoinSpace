package com.example.alexmelnikov.coinspace.ui.main

import android.view.View
import com.example.alexmelnikov.coinspace.ui.home.HomePresenter
import com.example.alexmelnikov.coinspace.ui.home.OperationFragment
import com.example.alexmelnikov.coinspace.ui.main.MainContract.Presenter.FragmentOnScreen

class MainPresenter : MainContract.Presenter {

    private lateinit var view: MainContract.View

    override lateinit var activeFragmentOnScreen: FragmentOnScreen


    override fun attach(view: MainContract.View) {
        this.view = view
    }

    override fun start(activeFragmentOnScreen: FragmentOnScreen) {
        when (activeFragmentOnScreen) {
            FragmentOnScreen.HOME -> openHomeFragmentRequest()
        }
    }

    override fun openHomeFragmentRequest() {
        activeFragmentOnScreen = FragmentOnScreen.HOME
        view.openHomeFragment()
    }

    override fun openAccountsFragmentRequest() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}