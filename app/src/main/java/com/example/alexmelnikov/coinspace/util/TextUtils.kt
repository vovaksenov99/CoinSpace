package com.example.alexmelnikov.coinspace.util

import java.text.NumberFormat
import java.util.*

/**
 *  Created by Alexander Melnikov on 22.07.18.
 *  TODO: Edit class header comment
 */

fun formatToMoneyString(sum: Float, currency: String): String {
    val format: NumberFormat = when (currency) {
        "RUB" -> NumberFormat.getCurrencyInstance(Locale.getDefault())
        "USD" -> NumberFormat.getCurrencyInstance(Locale.US)
        "EUR" -> NumberFormat.getCurrencyInstance(Locale.GERMANY)
        else -> NumberFormat.getCurrencyInstance(Locale.getDefault())
    }
    return format.format(sum)
}