package com.example.alexmelnikov.coinspace.util

import com.example.alexmelnikov.coinspace.model.interactors.Money
import com.example.alexmelnikov.coinspace.model.interactors.getCurrencyByString


fun formatToMoneyString(sum: Float, currency: String): String {
    val money = Money(sum.toDouble(), getCurrencyByString(currency))
    return "${money.normalizeCountString()} ${money.currency}"
}