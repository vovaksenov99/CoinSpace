package com.example.alexmelnikov.coinspace.ui.main

import com.example.alexmelnikov.coinspace.ui.BaseContract
import com.example.alexmelnikov.coinspace.ui.accounts.AccountsFragment
import com.example.alexmelnikov.coinspace.ui.home.HomeFragment
import com.example.alexmelnikov.coinspace.ui.home.HomePresenter
import com.example.alexmelnikov.coinspace.ui.home.OperationFragment

class MainContract {

    interface View : BaseContract.View {

        fun openSettingsActivityRequest()

        fun openHomeFragment(): HomeFragment

        fun openAccountsFragment(): AccountsFragment
    }

    interface Presenter : BaseContract.Presenter<MainContract.View> {

        var activeFragmentOnScreen: FragmentOnScreen

        /**
         * Presenter gets FragmentOnScreen instance to decide which fragments to open
         */
        fun start(activeFragmentOnScreen: FragmentOnScreen)

        fun openHomeFragmentRequest()

        fun openAccountsFragmentRequest()

        enum class FragmentOnScreen {
            HOME, ACCOUNTS
        }

    }
}
