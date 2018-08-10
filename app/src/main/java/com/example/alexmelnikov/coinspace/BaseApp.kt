package com.example.alexmelnikov.coinspace

import android.app.Application
import com.example.alexmelnikov.coinspace.di.component.ApplicationComponent
import com.example.alexmelnikov.coinspace.di.component.DaggerApplicationComponent
import com.example.alexmelnikov.coinspace.di.module.ApplicationModule
import com.example.alexmelnikov.coinspace.model.interactors.IUserBalanceInteractor
import com.example.alexmelnikov.coinspace.model.repositories.AccountsRepository
import com.facebook.stetho.Stetho
import com.hawkcatcherkotlin.akscorp.hawkcatcherkotlin.HawkExceptionCatcher
import javax.inject.Inject

class BaseApp : Application() {


    lateinit var component: ApplicationComponent

    @Inject
    lateinit var accountsRepository: AccountsRepository

    @Inject
    lateinit var userBalanceInteractor: IUserBalanceInteractor

    override fun onCreate() {
        super.onCreate()
        Stetho.initializeWithDefaults(this)

        instance = this
        setup()
        component.inject(this)

        //Init UserBalanceInteractor
        userBalanceInteractor.initCurrencyRates(applicationContext, {})

        //Init Hawk
        val exceptionCatcher = HawkExceptionCatcher(this, HAWK_TOKEN)
        try {
            exceptionCatcher.start()
        } catch (e: Exception) {
            e.printStackTrace()
        }
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