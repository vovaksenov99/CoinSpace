package com.example.alexmelnikov.coinspace.di.component

import com.example.alexmelnikov.coinspace.BaseApp
import com.example.alexmelnikov.coinspace.di.module.ApplicationModule
import com.example.alexmelnikov.coinspace.model.interactors.CurrencyConverter
import com.example.alexmelnikov.coinspace.ui.accounts.AccountsPresenter
import com.example.alexmelnikov.coinspace.ui.add_new_account.AddAccountPresenter
import com.example.alexmelnikov.coinspace.ui.home.HomePresenter
import com.example.alexmelnikov.coinspace.ui.settings.SettingsFragment
import com.example.alexmelnikov.coinspace.ui.statistics.StatisticsPresenter
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(ApplicationModule::class))
interface ApplicationComponent {

    fun inject(application: BaseApp)

    fun inject(homePresenter: HomePresenter)

    fun inject(addAccountPresenter: AddAccountPresenter)

    fun inject(accountsPresenter: AccountsPresenter)

    fun inject(statisticsPresenter: StatisticsPresenter)

    fun inject(settingsFragment: SettingsFragment)

    fun inject(converter: CurrencyConverter)
}