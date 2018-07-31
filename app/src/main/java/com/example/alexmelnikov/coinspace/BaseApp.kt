package com.example.alexmelnikov.coinspace

import android.app.Activity
import android.app.Application
import android.support.v4.app.Fragment
import com.example.alexmelnikov.coinspace.di.component.*
import com.example.alexmelnikov.coinspace.di.module.ApplicationModule
import com.example.alexmelnikov.coinspace.model.interactors.IUserBalanceInteractor
import com.example.alexmelnikov.coinspace.model.repositories.AccountsRepository
import com.example.alexmelnikov.coinspace.util.PreferencesHelper
import uk.co.chrisjenx.calligraphy.CalligraphyConfig
import javax.inject.Inject

class BaseApp : Application() {

    val BASE_FONT = "fonts/Roboto-Regular.ttf"

    lateinit var component: ApplicationComponent

    @Inject
    lateinit var accountsRepository: AccountsRepository

    @Inject
    lateinit var preferencesHelper: PreferencesHelper

    @Inject
    lateinit var userBalanceInteractor: IUserBalanceInteractor

    override fun onCreate() {
        super.onCreate()

        instance = this
        setup()
        component.inject(this)

        //Init accounts table in db
        accountsRepository.initAddTwoMainAccountsIfTableEmptyAsync(
                resources.getString(R.string.cash_account_name),
                resources.getString(R.string.main_currency),
                resources.getColor(R.color.colorPrimary),
                resources.getString(R.string.card_account_name))

        //Init UserBalanceInteractor
        userBalanceInteractor.initCurrencyRates()

        CalligraphyConfig.initDefault(CalligraphyConfig.Builder()
                .setDefaultFontPath(BASE_FONT)
                .setFontAttrId(R.attr.fontPath)
                .build())

        component.inject(this)
    }

    private fun setup() {
        component = DaggerApplicationComponent.builder()
                .applicationModule(ApplicationModule(this))
                .build()
    }


    companion object {
        lateinit var instance: BaseApp private set
    }

}