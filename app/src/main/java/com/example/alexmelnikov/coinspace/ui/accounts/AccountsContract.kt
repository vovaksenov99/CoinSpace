package com.example.alexmelnikov.coinspace.ui.accounts

import com.example.alexmelnikov.coinspace.ui.BaseContract

class AccountsContract {

    interface AccountsView : BaseContract.View {

        var presenter: AccountsContract.Presenter

        fun openAddAccountFragment()

    }

    //interface View
    interface Presenter : BaseContract.Presenter<AccountsView> {

        fun addNewAccountButtonClick()

    }
    interface View
}