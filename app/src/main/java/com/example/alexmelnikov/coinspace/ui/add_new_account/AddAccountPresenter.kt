package com.example.alexmelnikov.coinspace.ui.add_new_account

class AddAccountPresenter : AddAccountContract.Presenter {

    lateinit var view: AddAccountContract.View

    override fun attach(view: AddAccountContract.View) {
       this.view = view
    }
}