package com.example.alexmelnikov.coinspace

import android.app.Application
import android.util.Log
import androidx.work.PeriodicWorkRequest
import androidx.work.State
import androidx.work.WorkManager
import com.example.alexmelnikov.coinspace.di.component.ApplicationComponent
import com.example.alexmelnikov.coinspace.di.component.DaggerApplicationComponent
import com.example.alexmelnikov.coinspace.di.module.ApplicationModule
import com.example.alexmelnikov.coinspace.model.interactors.IUserBalanceInteractor
import com.example.alexmelnikov.coinspace.model.interactors.defaultCurrency
import com.example.alexmelnikov.coinspace.model.repositories.AccountsRepository
import com.example.alexmelnikov.coinspace.util.PreferencesHelper
import com.hawkcatcherkotlin.akscorp.hawkcatcherkotlin.HawkExceptionCatcher
import uk.co.chrisjenx.calligraphy.CalligraphyConfig
import java.util.concurrent.TimeUnit
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
            defaultCurrency.toString(),
            resources.getColor(R.color.colorPrimary),
            resources.getString(R.string.card_account_name))

        //Init UserBalanceInteractor
        userBalanceInteractor.initCurrencyRates(applicationContext, {})

        CalligraphyConfig.initDefault(CalligraphyConfig.Builder()
            .setDefaultFontPath(BASE_FONT)
            .setFontAttrId(R.attr.fontPath)
            .build())

        //Init Hawk
        val exceptionCatcher = HawkExceptionCatcher(this, HAWK_TOKEN)
        try {
            exceptionCatcher.start()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        initCurrenciesWorkManager()

        component.inject(this)
    }

    private fun setup() {
        component = DaggerApplicationComponent.builder()
            .applicationModule(ApplicationModule(this))
            .build()
    }

    private fun initCurrenciesWorkManager() {

        //WorkManager.getInstance().cancelAllWorkByTag(CurrenciesRateWorker.TAG)
        WorkManager.getInstance().getStatusesByTag(CurrenciesRateWorker.TAG).observeForever {
            for (work in it!!) {
                if (!work.state.isFinished) {
                    return@observeForever
                }
            }

            val currencyUpdater = PeriodicWorkRequest
                .Builder(CurrenciesRateWorker::class.java, 8, TimeUnit.HOURS)
                .addTag(CurrenciesRateWorker.TAG)
                .build()

            Log.i(::BaseApp.name, "Currency update work manager start")
            WorkManager.getInstance().enqueue(currencyUpdater)

        }
    }


    companion object {
        lateinit var instance: BaseApp private set
    }

}