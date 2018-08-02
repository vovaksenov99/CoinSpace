package com.example.alexmelnikov.coinspace.util

import com.example.alexmelnikov.coinspace.model.interactors.Currency
import com.example.alexmelnikov.coinspace.model.interactors.Money
import com.example.alexmelnikov.coinspace.model.interactors.getCurrencyByString


fun formatToMoneyString(money: Money): String {
    return "${money.normalizeCountString()} ${money.currency}"
}