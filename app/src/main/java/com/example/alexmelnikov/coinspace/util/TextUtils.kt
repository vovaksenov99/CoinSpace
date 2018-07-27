package com.example.alexmelnikov.coinspace.util

import com.example.alexmelnikov.coinspace.model.Accountant
import com.example.alexmelnikov.coinspace.model.Operation
import com.example.alexmelnikov.coinspace.model.Operation.Currency
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*

/**
 *  Created by Alexander Melnikov on 22.07.18.
 *  TODO: Edit class header comment
 */

class TextUtils {

    companion object {
        fun formatToMoneyString(sum: Float, currency: Currency): String {
            val format: NumberFormat = when (currency) {
                Currency.RUB -> NumberFormat.getCurrencyInstance(Locale.getDefault())
                Currency.USD -> NumberFormat.getCurrencyInstance(Locale.US)
                Currency.EUR -> NumberFormat.getCurrencyInstance(Locale.GERMANY)
            }
            return format.format(sum)
        }

        fun currencyIcon(currency: Currency) =
            when (currency) {
                Currency.USD -> "$"
                Currency.RUB -> "\u20BD"
                Currency.EUR -> "€"
            }

        fun stringToCurrency(string: String) =
                when (string) {
                    "USD" -> Currency.USD
                    "RUB" -> Currency.RUB
                    "EUR" -> Currency.EUR
                    else -> throw IllegalArgumentException("Unknown string currency representation")
                }

        fun currencyToString(currency: Operation.Currency) =
                when (currency) {
                    Currency.USD -> "USD"
                    Currency.RUB -> "RUB"
                    Currency.EUR -> "EUR"
                    else -> throw IllegalArgumentException("Unknown currency")
                }
    }
}