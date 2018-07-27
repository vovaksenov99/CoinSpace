package com.example.alexmelnikov.coinspace.ui.accounts

class AccountsPresenter : AccountsContract.Presenter {

    private lateinit var view: AccountsContract.AccountsView

    override fun attach(view: AccountsContract.AccountsView) {
        this.view = view
    }

    override fun addNewAccountButtonClick() {
        view.openAddAccountFragment()
    }

}