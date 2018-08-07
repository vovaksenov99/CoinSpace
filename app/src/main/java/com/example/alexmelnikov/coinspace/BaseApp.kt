package com.example.alexmelnikov.coinspace

import android.app.Application
import android.util.Log
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.example.alexmelnikov.coinspace.di.component.ApplicationComponent
import com.example.alexmelnikov.coinspace.di.component.DaggerApplicationComponent
import com.example.alexmelnikov.coinspace.di.module.ApplicationModule
import com.example.alexmelnikov.coinspace.model.Category
import com.example.alexmelnikov.coinspace.model.Currency
import com.example.alexmelnikov.coinspace.model.entities.Operation
import com.example.alexmelnikov.coinspace.model.entities.Pattern
import com.example.alexmelnikov.coinspace.model.interactors.IUserBalanceInteractor
import com.example.alexmelnikov.coinspace.model.interactors.defaultCurrency
import com.example.alexmelnikov.coinspace.model.repositories.AccountsRepository
import com.example.alexmelnikov.coinspace.model.repositories.PatternRepository
import com.example.alexmelnikov.coinspace.model.repositories.PatternsRepository
import com.example.alexmelnikov.coinspace.model.workers.CurrenciesRateWorker
import com.example.alexmelnikov.coinspace.model.workers.PeriodicOperationsWorker
import com.hawkcatcherkotlin.akscorp.hawkcatcherkotlin.HawkExceptionCatcher
import uk.co.chrisjenx.calligraphy.CalligraphyConfig
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class BaseApp : Application() {


    lateinit var component: ApplicationComponent

    @Inject
    lateinit var accountsRepository: AccountsRepository

    @Inject
    lateinit var userBalanceInteractor: IUserBalanceInteractor

    override fun onCreate() {
        super.onCreate()

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