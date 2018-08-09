package com.example.alexmelnikov.coinspace.model.interactors

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.example.alexmelnikov.coinspace.model.workers.CurrenciesRateWorker
import com.example.alexmelnikov.coinspace.R
import com.example.alexmelnikov.coinspace.model.Currency
import com.example.alexmelnikov.coinspace.model.entities.Operation
import com.example.alexmelnikov.coinspace.model.entities.OperationType
import com.example.alexmelnikov.coinspace.model.getCurrencyByString
import com.example.alexmelnikov.coinspace.util.PreferencesHelper
import java.io.Serializable
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols

/**
 *  Created by Alexander Melnikov on 29.07.18.
 */

class UserBalanceInteractor(private val preferencesHelper: PreferencesHelper) : IUserBalanceInteractor {

    var currencyConverter = CurrencyConverter()

    companion object {
        private const val BALANCE_KEY = "BALANCE"
        private const val MAIN_CURRENCY_KEY = "MAINCUR"
    }

    override fun initCurrencyRates(context: Context, callback: () -> Unit) {
        val pref =
            context.getSharedPreferences(CurrenciesRateWorker.CurrenciesStorage,
                Context.MODE_PRIVATE)

        synchronized(pref) {


            val spChanged =
                SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences, key ->

                    for (currency in Currency.values()) {
                        if (currency.toString() == key) {
                            currency.rate =
                                    sharedPreferences.getFloat(currency.toString(),
                                        currency.rate.toFloat()).toDouble()
                            callback()
                            break
                        }
                    }
                }

            pref.registerOnSharedPreferenceChangeListener(spChanged)

            for (currency in Currency.values()) {
                currency.rate =
                        pref.getFloat(currency.toString(), currency.rate.toFloat()).toDouble()
            }

        }

    }

    override fun getUserBalance(): Money {
        var currency = preferencesHelper.loadString(MAIN_CURRENCY_KEY)
        if (currency.isEmpty()) {
            currency = defaultCurrency.toString()
            preferencesHelper.saveString(MAIN_CURRENCY_KEY, currency)
        }

        val balance = preferencesHelper.loadFloat(BALANCE_KEY)
        return Money(balance, getCurrencyByString(currency))
    }

    override fun mainCurrencyChanged(currencyBeforeChange: String) {
        val newCurrency = preferencesHelper.loadString(MAIN_CURRENCY_KEY)
        var balanceCount = preferencesHelper.loadFloat(BALANCE_KEY)
        val balance = currencyConverter.convertCurrency(Money(balanceCount,
            getCurrencyByString(currencyBeforeChange)), getCurrencyByString(newCurrency))
        preferencesHelper.saveFloat(BALANCE_KEY, balance.count)
    }

    override fun executeNewOperation(type: OperationType?, money: Money): Money {
        if (type == null) {
            throw IllegalArgumentException("Operation type can't equal null")
        }

        var ub = currencyConverter.convertCurrency(getUserBalance(), defaultCurrency)

        when (type) {
            OperationType.INCOME -> {
                ub.count += currencyConverter.convertCurrency(money, defaultCurrency).count

            }
            OperationType.EXPENSE -> {
                ub.count -= currencyConverter.convertCurrency(money, defaultCurrency).count
            }
        }

        preferencesHelper.saveFloat(BALANCE_KEY, ub.count)

        return ub
    }


    override fun setBalance(money: Money) {
        val money= currencyConverter.convertCurrency(money, defaultCurrency)
        Log.i("Updated balance","$money")
        preferencesHelper.saveFloat(BALANCE_KEY, money.count)
    }
}

val defaultCurrency = Currency.EUR


data class Money(var count: Float, var currency: Currency) : Serializable {
    fun normalizeCountString(): String? {
        val format = DecimalFormat.getInstance() as DecimalFormat
        val custom = DecimalFormatSymbols()
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
        Money((money.count * currency.rate).toFloat(), currency)

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
        Money((money.count / money.currency.rate).toFloat(), defaultCurrency)
}


