package com.example.alexmelnikov.coinspace.ui.main

import android.content.Context
import android.view.View
import com.example.alexmelnikov.coinspace.ui.BaseContract
import com.example.alexmelnikov.coinspace.ui.accounts.AccountsFragment
import com.example.alexmelnikov.coinspace.ui.home.HomeFragment
import com.example.alexmelnikov.coinspace.ui.home.HomePresenter
import com.example.alexmelnikov.coinspace.ui.home.OperationFragment
import com.example.alexmelnikov.coinspace.ui.statistics.StatisticsFragment

class MainContract {

    interface View : BaseContract.View {

        fun openSettingsActivityRequest()

        fun openAccountsFragmentRequest()

        fun openStatisticsFragmentRequest(animationCenter: android.view.View)

        fun openHomeFragment(): HomeFragment

        fun openAccountsFragment(): AccountsFragment

        fun openIntroductionDialog()

        fun openStatisticsFragment(animationCenter: android.view.View?): StatisticsFragment
    }

    interface Presenter : BaseContract.Presenter<MainContract.View> {

        var activeFragmentOnScreen: FragmentOnScreen

        /**
         * Presenter gets FragmentOnScreen instance to decide which fragments to open
         */
        fun start(activeFragmentOnScreen: FragmentOnScreen)

        fun openHomeFragmentRequest()

        fun openAccountsFragmentRequest()

        fun openStatisticsFragmentRequest(animationCenter: android.view.View?)

        fun openIntroductionDialog(context: Context)

    }
    enum class FragmentOnScreen {
        HOME, ACCOUNTS, STATISTICS
    }
}
