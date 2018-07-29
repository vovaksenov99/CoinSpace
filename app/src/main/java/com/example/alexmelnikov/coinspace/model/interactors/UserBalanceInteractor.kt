package com.example.alexmelnikov.coinspace.model.interactors

import android.util.Log
import com.example.alexmelnikov.coinspace.model.entities.Operation
import com.example.alexmelnikov.coinspace.model.repositories.AccountsRepository
import com.example.alexmelnikov.coinspace.util.PreferencesHelper

/**
 *  Created by Alexander Melnikov on 29.07.18.
 *  TODO: Edit class header comment
 */

class UserBalanceInteractor(private val preferencesHelper: PreferencesHelper,
                            private val accountsRepository: AccountsRepository) : IUserBalanceInteractor {

    private val BALANCE_KEY = "BALANCE"
    private val MAIN_CURRENCY_KEY = "MAINCUR"

    private val FACTOR_USD_TO_RUB = 63f
    private val FACTOR_RUB_TO_USD = 0.015873f
    private val FACTOR_EUR_TO_RUB = 74f
    private val FACTOR_RUB_TO_EUR = 0.01351351f
    private val FACTOR_USD_TO_EUR = 0.8547f
    private val FACTOR_EUR_TO_USD = 1.17f

    /**
     * @return Pair(balance, default_currency)
     */
    override fun getUserBalance(): Pair<Float, String> {
        //Check if currency preference value is empty and set RUB as default
        var currency = preferencesHelper.loadString(MAIN_CURRENCY_KEY)
        if (currency.isEmpty()) {
            currency = "RUB"
            preferencesHelper.saveString(MAIN_CURRENCY_KEY, currency)
        }

        return Pair(preferencesHelper.loadFloat(BALANCE_KEY), currency)
    }

    override fun mainCurrencyChanged(currencyBeforeChange: String) {
        val newCurrency = preferencesHelper.loadString(MAIN_CURRENCY_KEY)
        var balance = preferencesHelper.loadFloat(BALANCE_KEY)
        Log.d("mytag", "beforeChange:$currencyBeforeChange; afterChange:$newCurrency")
        balance = convertCurrencyFromTo(balance, currencyBeforeChange, newCurrency)
        preferencesHelper.saveFloat(BALANCE_KEY, balance)
    }

    override fun executeNewOperation(type: Operation.OperationType?,
                                     sum: Float,
                                     currency: String): Float {
        if (type == null) {
            throw IllegalArgumentException("Operation type can't equal null")
        }
        var (balance, mainCur) = getUserBalance()
        when (type) {
            Operation.OperationType.INCOME -> {
                balance += if (mainCur != currency) convertCurrencyFromTo(sum, currency, mainCur)
                    else sum

            }
            Operation.OperationType.EXPENSE -> {
                balance -= if (mainCur != currency) convertCurrencyFromTo(sum, currency, mainCur)
                else sum
            }
        }
        preferencesHelper.saveFloat(BALANCE_KEY, balance)
        return balance
    }

    override fun convertCurrencyFromTo(money: Float, from: String, to: String) =
            when (Pair(from, to)) {
                Pair("USD", "RUB") -> money * FACTOR_USD_TO_RUB
                Pair("USD", "EUR") -> money * FACTOR_USD_TO_EUR
                Pair("EUR", "USD") -> money * FACTOR_EUR_TO_USD
                Pair("EUR", "RUB") -> money * FACTOR_EUR_TO_RUB
                Pair("RUB", "USD") -> money * FACTOR_RUB_TO_USD
                Pair("RUB", "EUR") -> money * FACTOR_RUB_TO_EUR
                else -> money
            }
}