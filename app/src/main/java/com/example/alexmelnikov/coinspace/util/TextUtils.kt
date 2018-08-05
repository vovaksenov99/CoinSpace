package com.example.alexmelnikov.coinspace.util

import com.example.alexmelnikov.coinspace.model.interactors.Money


fun formatToMoneyString(money: Money): String {
    return "${money.normalizeCountString()} ${money.currency}"
}