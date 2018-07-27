package com.example.alexmelnikov.coinspace.model

import com.example.alexmelnikov.coinspace.model.entities.Operation.Currency

/**
 *  Created by Alexander Melnikov on 22.07.18.
 *  Accountant is the base model class for finance management operations
 */

class Accountant {

    private var balanceUsd: Float = 0.00f

    private val FACTOR_USD_TO_RUB = 63f
    private val FACTOR_RUB_TO_USD = 0.015873f
    private val FACTOR_EUR_TO_RUB = 74f
    private val FACTOR_RUB_TO_EUR = 0.01351351f
    private val FACTOR_USD_TO_EUR = 0.8547f
    private val FACTOR_EUR_TO_USD = 1.17f

    fun getBalanceUsd(): Float {
        return balanceUsd
    }

    fun updateBalance(newBalance: Float, currency: Currency) {
        balanceUsd = when (currency) {
            Currency.USD -> newBalance
            Currency.RUB -> newBalance * FACTOR_RUB_TO_USD
            Currency.EUR -> newBalance * FACTOR_EUR_TO_USD
        }
    }

    fun convertCurrencyFromTo(money: Float, from: Currency, to: Currency) =
            when (Pair(from, to)) {
                 Pair(Currency.USD, Currency.RUB) -> money * FACTOR_USD_TO_RUB
                 Pair(Currency.USD, Currency.EUR) -> money * FACTOR_USD_TO_EUR
                 Pair(Currency.EUR, Currency.USD) -> money * FACTOR_EUR_TO_USD
                 Pair(Currency.EUR, Currency.RUB) -> money * FACTOR_EUR_TO_RUB
                 Pair(Currency.RUB, Currency.USD) -> money * FACTOR_RUB_TO_USD
                 Pair(Currency.RUB, Currency.EUR) -> money * FACTOR_RUB_TO_EUR
                 else -> money
             }


    fun addExpense(sum: Float, currency: Currency) {
        val sumUsd = convertCurrencyFromTo(sum, currency, Currency.USD)
        balanceUsd -= sumUsd
    }

    fun addIncome(sum: Float, currency: Currency) {
        val sumUsd = convertCurrencyFromTo(sum, currency, Currency.USD)
        balanceUsd += sumUsd
    }
}
