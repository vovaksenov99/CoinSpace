package com.example.alexmelnikov.coinspace.util

import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*

/**
 *  Created by Alexander Melnikov on 22.07.18.
 *  TODO: Edit class header comment
 */

class TextUtils {

    companion object {
        fun formatToMoneyString(sum: Float, currency: String): String {
            var format: NumberFormat = NumberFormat.getCurrencyInstance(Locale.getDefault())
            when (currency) {
                "RUB" -> format = NumberFormat.getCurrencyInstance(Locale.getDefault())
                "USD" -> format = NumberFormat.getCurrencyInstance(Locale.US)
                "EUR" -> format = NumberFormat.getCurrencyInstance(Locale.GERMANY)
            }
            return format.format(sum)
        }

        fun currencyIcon(currency: String): String {
            when (currency) {
                "USD" -> return "$"
                "RUB" -> return "\u20BD"
                "EUR" -> return "€"
            }
            return ""
        }
    }
}