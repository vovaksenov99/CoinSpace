package com.example.alexmelnikov.coinspace.model

/**
 *  Created by Alexander Melnikov on 22.07.18.
 *  TODO: Edit class header comment
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

    fun updateBalance(newBalance: Float, currency: String) {
        when (currency) {
            "USD" -> balanceUsd = newBalance
            "RUB" -> balanceUsd = newBalance * FACTOR_RUB_TO_USD
            "EUR" -> balanceUsd = newBalance * FACTOR_EUR_TO_USD
        }
    }

    fun convertCurrencyFromTo(money: Float, from: String, to: String): Float {
        if (from != to) {
            when (from) {
                "USD" -> when (to) {
                    "RUB" -> return money * FACTOR_USD_TO_RUB
                    "EUR" -> return money * FACTOR_USD_TO_EUR
                }
                "EUR" -> {
                    when (to) {
                        "USD" -> return money * FACTOR_EUR_TO_USD
                        "RUB" -> return money * FACTOR_EUR_TO_RUB
                    }
                    when (to) {
                        "USD" -> return money * FACTOR_RUB_TO_USD
                        "EUR" -> return money * FACTOR_RUB_TO_EUR
                    }
                }
                "RUB" -> when (to) {
                    "USD" -> return money * FACTOR_RUB_TO_USD
                    "EUR" -> return money * FACTOR_RUB_TO_EUR
                }
            }
        }
        return money
    }

    fun addExpense(sum: Float, currency: String) {
        val sumUsd = convertCurrencyFromTo(sum, currency, "USD")
        balanceUsd -= sumUsd
    }

    fun addIncome(sum: Float, currency: String) {
        val sumUsd = convertCurrencyFromTo(sum, currency, "USD")
        balanceUsd += sumUsd
    }
}
