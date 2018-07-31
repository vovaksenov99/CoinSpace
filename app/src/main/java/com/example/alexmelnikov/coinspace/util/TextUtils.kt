package com.example.alexmelnikov.coinspace.util

import java.text.NumberFormat
import java.util.*


fun formatToMoneyString(sum: Float, currency: String): String {
    //TODO: заменить getDefault и вообще все переделать
    val format: NumberFormat = when (currency) {
        "RUB" -> NumberFormat.getCurrencyInstance(Locale.getDefault())
        "USD" -> NumberFormat.getCurrencyInstance(Locale.US)
        "EUR" -> NumberFormat.getCurrencyInstance(Locale.GERMANY)
        else -> NumberFormat.getCurrencyInstance(Locale.getDefault())
    }
    return format.format(sum)
}