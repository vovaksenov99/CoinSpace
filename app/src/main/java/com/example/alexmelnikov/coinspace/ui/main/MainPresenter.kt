package com.example.alexmelnikov.coinspace.ui.main

import com.example.alexmelnikov.coinspace.ui.home.HomeContract

class MainPresenter : MainContract.Presenter {

    private lateinit var view: MainContract.View

    override fun attach(view: MainContract.View) {
        this.view = view
    }

}