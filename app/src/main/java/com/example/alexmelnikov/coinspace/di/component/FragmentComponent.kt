package com.example.alexmelnikov.coinspace.di.component

import android.support.v4.app.DialogFragment
import com.example.alexmelnikov.coinspace.BaseApp
import com.example.alexmelnikov.coinspace.di.module.FragmentModule
import com.example.alexmelnikov.coinspace.ui.PeriodicDialog
import com.example.alexmelnikov.coinspace.ui.accounts.AccountsFragment
import com.example.alexmelnikov.coinspace.ui.add_new_account.AddAccountFragment
import com.example.alexmelnikov.coinspace.ui.home.OperationFragment
import com.example.alexmelnikov.coinspace.ui.home.HomeFragment
import com.example.alexmelnikov.coinspace.ui.statistics.StatisticsFragment
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [FragmentModule::class])
interface FragmentComponent {

    fun inject(homeFragment: HomeFragment)

    fun inject(operationFragment: OperationFragment)

    fun inject(accountsFragment: AccountsFragment)

    fun inject(addAccountFragment: AddAccountFragment)

    fun inject(statisticsFragment: StatisticsFragment)

    fun inject(periodicFragment: PeriodicDialog)
}
