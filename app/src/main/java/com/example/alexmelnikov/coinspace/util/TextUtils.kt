package com.example.alexmelnikov.coinspace.util

import com.example.alexmelnikov.coinspace.model.interactors.Money
import java.text.SimpleDateFormat
import java.util.*


fun formatToMoneyString(money: Money): String {
    return "${money.normalizeCountString()} ${money.currency}"
}

fun find(query: String, source: String): Boolean {
    val pl = query.length
    val tl = source.length
    val p = prefixFunction("$query#$source")
    for (i in 0 until tl) {
        if (p[pl + i + 1] == pl)
            return true
    }
    return false
}

fun find(query: String, source: String, prefixF: List<Int>): Boolean {
    val pl = query.length
    val tl = source.length
    val p = prefixF
    for (i in 0 until tl) {
        if (p[pl + i + 1] == pl)
            return true
    }
    return false
}

fun prefixFunction(s: String): List<Int> {
    val p = mutableListOf<Int>()
    repeat(s.length) { p.add(0) }

    p[0] = 0
    for (i in 1 until s.length) {
        var k = p[i - 1]
        while (k > 0 && s[i] != s[k])
            k = p[k - 1]
        if (s[i] == s[k])
            k++
        p[i] = k
    }
    return p
}

fun getDate(timeStamp: Long): String {

    try {
        val sdf = SimpleDateFormat("dd.MM.yyyy")
        val netDate = Date(timeStamp)
        return sdf.format(netDate)
    } catch (ex: Exception) {
        return "xx"
    }

}