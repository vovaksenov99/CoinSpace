package com.example.alexmelnikov.coinspace.model.interactors

import android.util.Log
import com.example.alexmelnikov.coinspace.BuildConfig.FIXER_API_KEY
import com.example.alexmelnikov.coinspace.model.api.ApiService
import com.example.alexmelnikov.coinspace.model.entities.ApiResponseRoot
import com.example.alexmelnikov.coinspace.model.entities.Operation
import com.example.alexmelnikov.coinspace.model.entities.UserBalance
import com.example.alexmelnikov.coinspace.model.repositories.AccountsRepository
import com.example.alexmelnikov.coinspace.util.ConnectionHelper
import com.example.alexmelnikov.coinspace.util.PreferencesHelper
import org.joda.time.DateTime
import org.joda.time.Hours
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 *  Created by Alexander Melnikov on 29.07.18.
 *  TODO: Edit class header comment
 */

class UserBalanceInteractor(private val preferencesHelper: PreferencesHelper,
                            private val connectionHelper: ConnectionHelper,
                            private val accountsRepository: AccountsRepository,
                            private val currencyService: ApiService) : IUserBalanceInteractor {

    companion object {
        private const val BALANCE_KEY = "BALANCE"
        private const val MAIN_CURRENCY_KEY = "MAINCUR"
        private const val LAST_CURRENCY_UPDATE_KEY = "LAST_CUR_UPDATE"
        private const val CUR_USD_RUB_KEY = "USD_RUB"
        private const val CUR_USD_EUR_KEY = "USD_EUR"
    }

    private var usdToRub: Float = 0f
    private var usdToEur: Float = 0f
    private var rubToEur: Float = 0f

    override fun initCurrencyRates() {
        val lastUpdateTimeStamp = preferencesHelper.loadLong(LAST_CURRENCY_UPDATE_KEY)
        if (lastUpdateTimeStamp != 0L) {
            //Unix time is in seconds, Java time is in milliseconds -> x1000L
            val lastUpdate = DateTime(lastUpdateTimeStamp * 1000L)
            val now = DateTime()

            //Check if since last update more than 10 hours passed, update currency rates with api if yes
            val hoursPassed = Hours.hoursBetween(lastUpdate.toLocalDateTime(), now.toLocalDateTime()).hours
            if (hoursPassed > 10 && connectionHelper.isOnline()) {
                getCurrencyRatesFromApi()
            } else {
                usdToRub = preferencesHelper.loadFloat(CUR_USD_RUB_KEY)
                usdToEur = preferencesHelper.loadFloat(CUR_USD_EUR_KEY)
                rubToEur = usdToEur/usdToRub
            }
        } else {
            if (connectionHelper.isOnline()) getCurrencyRatesFromApi()
            else {
                Log.d("mytag", "can't get initial currency rates from api (no connection)")
                //Assign default values as of 07/2018
                usdToRub = 63f
                usdToEur = 0.8547f
                rubToEur = usdToEur/usdToRub
            }
        }
    }

    private fun getCurrencyRatesFromApi() {
        currencyService.getRatesForCurrency(FIXER_API_KEY).enqueue(object : Callback<ApiResponseRoot> {

            override fun onResponse(call: Call<ApiResponseRoot>, response: Response<ApiResponseRoot>) {
                val timestamp = response.body()?.timestamp
                val usdRates = response.body()?.rates
                if (usdRates != null) {
                    usdToRub = usdRates["RUB"]!!
                    usdToEur = usdRates["EUR"]!!
                    rubToEur = usdToEur/usdToRub
                }

                preferencesHelper.saveFloat(CUR_USD_RUB_KEY, usdToRub)
                preferencesHelper.saveFloat(CUR_USD_EUR_KEY, usdToEur)
                preferencesHelper.saveLong(LAST_CURRENCY_UPDATE_KEY, timestamp ?: 0L)

                Log.d("mytag", "ApiHelper request succeeed ${response.toString()}")
            }

            override fun onFailure(call: Call<ApiResponseRoot>, t: Throwable) {
                Log.d("mytag", "ApiHelper request failed ${t.toString()}")
            }
        })
    }

    override fun getUserBalance(): UserBalance {
        //Check if currency preference value is empty and set RUB as default
        var currency = preferencesHelper.loadString(MAIN_CURRENCY_KEY)
        if (currency.isEmpty()) {
            currency = "RUB"
            preferencesHelper.saveString(MAIN_CURRENCY_KEY, currency)
        }

        val balance = preferencesHelper.loadFloat(BALANCE_KEY)
        val balanceUsd = if (currency == "USD") balance else convertCurrencyFromTo(balance, currency, "USD")
        return UserBalance(balance, currency, balanceUsd)
    }

    override fun mainCurrencyChanged(currencyBeforeChange: String) {
        val newCurrency = preferencesHelper.loadString(MAIN_CURRENCY_KEY)
        var balance = preferencesHelper.loadFloat(BALANCE_KEY)
        balance = convertCurrencyFromTo(balance, currencyBeforeChange, newCurrency)
        preferencesHelper.saveFloat(BALANCE_KEY, balance)
    }

    override fun executeNewOperation(type: Operation.OperationType?,
                                     sum: Float,
                                     currency: String): UserBalance {
        Log.d("mytag", "new operation\nrates: usd_rub=$usdToRub\nusd_eur=$usdToEur\nrub_eur=$rubToEur")
        if (type == null) {
            throw IllegalArgumentException("Operation type can't equal null")
        }
        val ub = getUserBalance()
        when (type) {
            Operation.OperationType.INCOME -> {
                ub.balance += if (ub.currency != currency) convertCurrencyFromTo(sum, currency, ub.currency)
                    else sum

            }
            Operation.OperationType.EXPENSE -> {
                ub.balance -= if (ub.currency != currency) convertCurrencyFromTo(sum, currency, ub.currency)
                    else sum
            }
        }

        ub.balanceUsd = convertCurrencyFromTo(ub.balance, ub.currency, "USD")
        preferencesHelper.saveFloat(BALANCE_KEY, ub.balance)
        return ub
    }

    override fun convertCurrencyFromTo(money: Float, from: String, to: String): Float {
        if (money == 0F) return 0F
        if (from == to) return money
        return when (Pair(from, to)) {
            Pair("USD", "RUB") -> money * usdToRub
            Pair("USD", "EUR") -> money * usdToEur
            Pair("RUB", "EUR") -> money * rubToEur
            Pair("RUB", "USD") -> money * (1f / usdToRub)
            Pair("EUR", "USD") -> money * (1f / usdToEur)
            Pair("EUR", "RUB") -> money * (1f / rubToEur)
            else -> money
        }
    }
}