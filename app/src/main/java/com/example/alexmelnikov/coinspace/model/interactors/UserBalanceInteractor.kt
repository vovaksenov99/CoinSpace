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
import java.io.Serializable
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols

/**
 *  Created by Alexander Melnikov on 29.07.18.
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
        val converter = CurrencyConverter()
        val money = Money(money.toDouble(),getCurrencyByString(from))
        val convertedMoney = converter.convertCurrency(money, getCurrencyByString(to))
        return convertedMoney.count.toFloat()
    }
}
val defaultCurrency = Currency.USD


data class Money(var count: Double, var currency: Currency) : Serializable {
    fun normalizeCountString(): String? {
        val format = DecimalFormat.getInstance() as DecimalFormat
        val custom = DecimalFormatSymbols()
        custom.decimalSeparator = custom.decimalSeparator
        format.decimalFormatSymbols = custom
        val f = String.format("%.2f", count)
        if (count.isNaN())
            return "0.0"
        return format.format(format.parse(f))
    }
}

/**
 * Main currency is defaultCurrency [EUR]
 */
class CurrencyConverter() {

    /**
     * Convert [money] to another [currency]
     */
    private fun fromDefaultCurrencyToCurrency(money: Money, currency: Currency) =
        Money(money.count * currency.rate, currency)

    /**
     * @param money - money to convert
     * @param toCurrency - Currency to convert [money]
     */
    fun convertCurrency(money: Money, toCurrency: Currency): Money {
        val defCur = toDefaultCurrency(money)
        return fromDefaultCurrencyToCurrency(defCur, toCurrency)
    }

    /**
     * @param balance - balance to convert
     * @param currencies - currencies for convert
     *
     * @return list with balance which converted to all [currencies]
     */
    fun currentBalanceToAnotherCurrencies(balance: Money, currencies: List<Currency> = listOf(
        Currency.USD,
        Currency.EUR,
        Currency.RUR,
        Currency.GBP)): List<Money> {
        val rez = mutableListOf<Money>()

        for (currency in currencies) {
            if (balance.currency == currency)
                continue
            rez.add(convertCurrency(balance, currency))
        }
        return rez
    }

    /**
     * Convert money to [defaultCurrency]
     */
    fun toDefaultCurrency(money: Money): Money =
        Money(money.count / money.currency.rate, defaultCurrency)
}



enum class Currency {
    USD {
        override val currencySymbol = "$"
        override var rate: Double = 1.0
        override fun toString(): String {
            return "USD"
        }
    },
    RUR {
        override val currencySymbol = "\u20BD"
        override var rate: Double = 63.0
        override fun toString(): String {
            return "RUB"
        }
    },

    EUR {
        override val currencySymbol = "€"
        override var rate: Double = 0.8611
        override fun toString(): String {
            return "EUR"
        }
    },

    GBP {
        override val currencySymbol = "£"
        override var rate: Double = 0.76
        override fun toString(): String {
            return "GBP"
        }
    };

    abstract var rate: Double
    abstract override fun toString(): String
    abstract val currencySymbol: String
}

fun getCurrencyByString(currencyIndex: String): Currency {
    for(currency in Currency.values())
        if(currency.toString() == currencyIndex)
            return currency
    throw Exception("Not valid currency index. Invalid string '$currencyIndex'")
}
