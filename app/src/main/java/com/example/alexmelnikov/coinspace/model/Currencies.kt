package com.example.alexmelnikov.coinspace.model


enum class Currency {
    USD {
        override val currencySymbol = "$"
        override var rate: Double = 1.0
        override fun toString(): String {
            return "USD"
        }
    },
    RUR {
        override val currencySymbol = "\u20BD"
        override var rate: Double = 63.0
        override fun toString(): String {
            return "RUB"
        }
    },

    EUR {
        override val currencySymbol = "€"
        override var rate: Double = 0.8611
        override fun toString(): String {
            return "EUR"
        }
    },

    GBP {
        override val currencySymbol = "£"
        override var rate: Double = 0.76
        override fun toString(): String {
            return "GBP"
        }
    };

    abstract var rate: Double
    abstract override fun toString(): String
    abstract val currencySymbol: String
}

fun getCurrencyByString(currencyIndex: String): Currency {
    for (currency in Currency.values())
        if (currency.toString() == currencyIndex)
            return currency
    throw Exception("Not valid currency index. Invalid string '$currencyIndex'")
}