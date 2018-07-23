package com.example.alexmelnikov.coinspace.ui

interface BaseContract {

    interface View {

    }

    interface Presenter<in T> {
        fun attach(view: T)
    }
}

