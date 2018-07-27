package com.example.alexmelnikov.coinspace.ui.add_new_account

import com.example.alexmelnikov.coinspace.ui.BaseContract
import com.example.alexmelnikov.coinspace.ui.accounts.AccountsContract

class AddAccountContract {

    interface View : BaseContract.View {

        var presenter: AddAccountContract.Presenter

    }

    //interface View
    interface Presenter : BaseContract.Presenter<View> {



    }
}