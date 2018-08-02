package com.example.alexmelnikov.coinspace.model.interactors

import android.content.Context
import android.util.Log
import com.example.alexmelnikov.coinspace.BuildConfig.FIXER_API_KEY
import com.example.alexmelnikov.coinspace.R
import com.example.alexmelnikov.coinspace.model.api.ApiService
import com.example.alexmelnikov.coinspace.model.entities.ApiResponseRoot
import com.example.alexmelnikov.coinspace.model.entities.Operation
import com.example.alexmelnikov.coinspace.model.repositories.AccountsRepository
import com.example.alexmelnikov.coinspace.util.ConnectionHelper
import com.example.alexmelnikov.coinspace.util.PreferencesHelper
import org.joda.time.DateTime
import org.joda.time.Hours
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.Serializable
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols

/**
 *  Created by Alexander Melnikov on 29.07.18.
 */

class UserBalanceInteractor(private val preferencesHelper: PreferencesHelper,
                            private val connectionHelper: ConnectionHelper,
                            private val accountsRepository: AccountsRepository,
                            private val currencyService: ApiService) : IUserBalanceInteractor {

    var currencyConverter = CurrencyConverter()

    companion object {
        private const val BALANCE_KEY = "BALANCE"
        private const val MAIN_CURRENCY_KEY = "MAINCUR"
        private const val LAST_CURRENCY_UPDATE_KEY = "LAST_CUR_UPDATE"
        private const val CUR_USD_RUB_KEY = "USD_RUB"
        private const val CUR_USD_EUR_KEY = "USD_EUR"
    }

    private var usdToRub: Float = 0f
    private var usdToEur: Float = 0f
    private var rubToEur: Float = 0f

    override fun initCurrencyRates() {
        val lastUpdateTimeStamp = preferencesHelper.loadLong(LAST_CURRENCY_UPDATE_KEY)
        if (lastUpdateTimeStamp != 0L) {
            //Unix time is in seconds, Java time is in milliseconds -> x1000L
            val lastUpdate = DateTime(lastUpdateTimeStamp * 1000L)
            val now = DateTime()

            //Check if since last update more than 10 hours passed, update currency rates with api if yes
            val hoursPassed =
                Hours.hoursBetween(lastUpdate.toLocalDateTime(), now.toLocalDateTime()).hours
            if (hoursPassed > 10 && connectionHelper.isOnline()) {
                getCurrencyRatesFromApi()
            }
            else {
                usdToRub = preferencesHelper.loadFloat(CUR_USD_RUB_KEY)
                usdToEur = preferencesHelper.loadFloat(CUR_USD_EUR_KEY)
                rubToEur = usdToEur / usdToRub
            }
        }
        else {
            if (connectionHelper.isOnline()) getCurrencyRatesFromApi()
            else {
                Log.d("mytag", "can't get initial currency rates from api (no connection)")
                //Assign default values as of 07/2018
                usdToRub = 63f
                usdToEur = 0.8547f
                rubToEur = usdToEur / usdToRub
            }
        }
    }

    private fun getCurrencyRatesFromApi() {
        currencyService.getRatesForCurrency(FIXER_API_KEY)
            .enqueue(object : Callback<ApiResponseRoot> {

                override fun onResponse(call: Call<ApiResponseRoot>,
                                        response: Response<ApiResponseRoot>) {
                    val timestamp = response.body()?.timestamp
                    val usdRates = response.body()?.rates
                    if (usdRates != null) {
                        usdToRub = usdRates["RUB"]!!
                        usdToEur = usdRates["EUR"]!!
                        rubToEur = usdToEur / usdToRub
                    }

                    preferencesHelper.saveFloat(CUR_USD_RUB_KEY, usdToRub)
                    preferencesHelper.saveFloat(CUR_USD_EUR_KEY, usdToEur)
                    preferencesHelper.saveLong(LAST_CURRENCY_UPDATE_KEY, timestamp ?: 0L)

                    Log.d("mytag", "ApiHelper request succeeed ${response.toString()}")
                }

                override fun onFailure(call: Call<ApiResponseRoot>, t: Throwable) {
                    Log.d("mytag", "ApiHelper request failed ${t.toString()}")
                }
            })
    }

    override fun getUserBalance(): Money {
        var currency = preferencesHelper.loadString(MAIN_CURRENCY_KEY)
        if (currency.isEmpty()) {
            currency = defaultCurrency.toString()
            preferencesHelper.saveString(MAIN_CURRENCY_KEY, currency)
        }

        val balance = preferencesHelper.loadFloat(BALANCE_KEY)
        return Money(balance, getCurrencyByString(currency))
    }

    override fun mainCurrencyChanged(currencyBeforeChange: String) {
        val newCurrency = preferencesHelper.loadString(MAIN_CURRENCY_KEY)
        var balanceCount = preferencesHelper.loadFloat(BALANCE_KEY)
        val balance = currencyConverter.convertCurrency(Money(balanceCount,
            getCurrencyByString(currencyBeforeChange)), getCurrencyByString(newCurrency))
        preferencesHelper.saveFloat(BALANCE_KEY, balance.count)
    }

    override fun executeNewOperation(type: Operation.OperationType?, money: Money): Money {
        if (type == null) {
            throw IllegalArgumentException("Operation type can't equal null")
        }

        var ub = currencyConverter.convertCurrency(getUserBalance(), defaultCurrency)

        when (type) {
            Operation.OperationType.INCOME -> {
                ub.count += currencyConverter.convertCurrency(money, defaultCurrency).count

            }
            Operation.OperationType.EXPENSE -> {
                ub.count -= currencyConverter.convertCurrency(money, defaultCurrency).count
            }
        }

        preferencesHelper.saveFloat(BALANCE_KEY, ub.count)
        return ub
    }
}

val defaultCurrency = Currency.USD


data class Money(var count: Float, var currency: Currency) : Serializable {
    fun normalizeCountString(): String? {
        val format = DecimalFormat.getInstance() as DecimalFormat
        val custom = DecimalFormatSymbols()
        custom.decimalSeparator = custom.decimalSeparator
        format.decimalFormatSymbols = custom
        val f = String.format("%.2f", count)
        if (count.isNaN())
            return "0.0"
        return format.format(format.parse(f))
    }
}

/**
 * Main currency is defaultCurrency [EUR]
 */
class CurrencyConverter() {

    /**
     * Convert [money] to another [currency]
     */
    private fun fromDefaultCurrencyToCurrency(money: Money, currency: Currency) =
        Money((money.count * currency.rate).toFloat(), currency)

    /**
     * @param money - money to convert
     * @param toCurrency - Currency to convert [money]
     */
    fun convertCurrency(money: Money, toCurrency: Currency): Money {
        val defCur = toDefaultCurrency(money)
        return fromDefaultCurrencyToCurrency(defCur, toCurrency)
    }

    /**
     * @param balance - balance to convert
     * @param currencies - currencies for convert
     *
     * @return list with balance which converted to all [currencies]
     */
    fun currentBalanceToAnotherCurrencies(balance: Money, currencies: List<Currency> = listOf(
        Currency.USD,
        Currency.EUR,
        Currency.RUR,
        Currency.GBP)): List<Money> {
        val rez = mutableListOf<Money>()

        for (currency in currencies) {
            if (balance.currency == currency)
                continue
            rez.add(convertCurrency(balance, currency))
        }
        return rez
    }

    /**
     * Convert money to [defaultCurrency]
     */
    fun toDefaultCurrency(money: Money): Money =
        Money((money.count / money.currency.rate).toFloat(), defaultCurrency)
}


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
enum class Category {
    MEAL {
        override fun getStringResource(): Int {
            return R.string.meal
        }

        override fun getIconResource(): Int {
            return R.drawable.meal
        }

    },
    EDUCATION {
        override fun getStringResource(): Int {
            return R.string.education
        }

        override fun getIconResource(): Int {
            return R.drawable.educational
        }
    },
    OTHER {
        override fun getStringResource(): Int {
            return R.string.other
        }

        override fun getIconResource(): Int {
            return R.drawable.other
        }
    },
    TRAVEL {
        override fun getStringResource(): Int {
            return R.string.travel
        }

        override fun getIconResource(): Int {
            return R.drawable.travel
        }
    },
    FOOD {
        override fun getStringResource(): Int {
            return R.string.food
        }

        override fun getIconResource(): Int {
            return R.drawable.food
        }
    },
    HOUSE {
        override fun getStringResource(): Int {
            return R.string.house
        }

        override fun getIconResource(): Int {
            return R.drawable.house
        }
    },
    CLOTH {
        override fun getStringResource(): Int {
            return R.string.cloth
        }

        override fun getIconResource(): Int {
            return R.drawable.cloth
        }
    },
    FUN {
        override fun getStringResource(): Int {
            return R.string.funy
        }

        override fun getIconResource(): Int {
            return R.drawable.funy
        }
    },
    TRANSPORT {
        override fun getStringResource(): Int {
            return R.string.transport
        }

        override fun getIconResource(): Int {
            return R.drawable.transport
        }
    },
    HEALTH {
        override fun getStringResource(): Int {
            return R.string.health
        }

        override fun getIconResource(): Int {
            return R.drawable.health
        }
    };

    abstract fun getIconResource(): Int
    abstract fun getStringResource(): Int
}
fun getCurrencyByString(currencyIndex: String): Currency {
    for (currency in Currency.values())
        if (currency.toString() == currencyIndex)
            return currency
    throw Exception("Not valid currency index. Invalid string '$currencyIndex'")
}

fun getCategoryByString(context:Context, categoryIndex: String): Category {
    for (category in Category.values())
        if (context.getString(category.getStringResource()) == categoryIndex)
            return category
    throw Exception("Not valid currency index. Invalid string '$categoryIndex'")
}
