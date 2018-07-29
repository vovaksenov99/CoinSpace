package com.example.alexmelnikov.coinspace.di.module

import com.example.alexmelnikov.coinspace.ui.accounts.AccountsContract
import com.example.alexmelnikov.coinspace.ui.accounts.AccountsPresenter
import com.example.alexmelnikov.coinspace.ui.add_new_account.AddAccountContract
import com.example.alexmelnikov.coinspace.ui.add_new_account.AddAccountPresenter
import com.example.alexmelnikov.coinspace.ui.home.HomeContract
import com.example.alexmelnikov.coinspace.ui.home.HomePresenter
import com.example.alexmelnikov.coinspace.ui.statistics.StatisticsContract
import com.example.alexmelnikov.coinspace.ui.statistics.StatisticsPresenter
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class FragmentModule {

    @Provides
    fun provideHomePresenter(): HomeContract.Presenter = HomePresenter()


    @Provides
    fun provideAccountsPresenter(): AccountsContract.Presenter = AccountsPresenter()


    @Provides
    fun provideAddAccountPresenter(): AddAccountContract.Presenter = AddAccountPresenter()

    @Provides
    fun provadeStatisticsPresenter(): StatisticsContract.Presenter = StatisticsPresenter()

}