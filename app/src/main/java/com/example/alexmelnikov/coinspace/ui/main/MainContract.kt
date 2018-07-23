package com.example.alexmelnikov.coinspace.ui.main

import com.example.alexmelnikov.coinspace.ui.BaseContract

class MainContract {

    interface View : BaseContract.View {

    }

    interface Presenter : BaseContract.Presenter<MainContract.View> {

    }
}
