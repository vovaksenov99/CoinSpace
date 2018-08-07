package com.example.alexmelnikov.coinspace.model.workers

import android.content.Context
import android.util.Log
import androidx.work.Worker
import com.example.alexmelnikov.coinspace.FIXEL_ENDPOINT
import com.example.alexmelnikov.coinspace.FIXEL_TOKEN
import com.example.alexmelnikov.coinspace.HAWK_TOKEN
import com.example.alexmelnikov.coinspace.model.Currency
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.hawkcatcherkotlin.akscorp.hawkcatcherkotlin.HawkExceptionCatcher
import okhttp3.OkHttpClient
import okhttp3.Request


class CurrenciesRateWorker : Worker() {

    /**
     * Put [currencies] in [CurrenciesStorage] sharedPref.
     *
     * @param currencies
     * Format ex.:
     *   "rates":{
     *   "AED":4.287568,
     *   "AFN":85.340437
     *   }
     *
     * @param date string format "2018-07-29"
     */
    private fun saveCurrencies(currencies: JsonObject, date: String) {
        val pref = applicationContext.getSharedPreferences(CurrenciesStorage, Context.MODE_PRIVATE)
        for (currency in Currency.values()) {
            pref.edit()
                .putFloat(currency.toString(), currencies[currency.toString()].asFloat)
                .apply()
        }

        pref.edit().putString("lastUpdateDate", date).apply()
    }

    override fun doWork(): Result {
        Log.i(::CurrenciesRateWorker.name, "Worker run " + System.currentTimeMillis())

        try {
            val client = OkHttpClient()
            val url =
                "$FIXEL_ENDPOINT?access_key=$FIXEL_TOKEN&symbols=${Currency.values().joinToString(",")}"
            val request = Request.Builder()
                .url(url)
                .build()

            val response = client.newCall(request).execute()
            val rawResponse = response.body()?.string()


            val parser = JsonParser()

            val responseObject = parser.parse(rawResponse).asJsonObject
            val success = responseObject["success"].asBoolean

            if (!success)
                throw Exception("API Exception:$rawResponse")

            val date = responseObject["date"].asString

            val rates = responseObject["rates"].asJsonObject

            saveCurrencies(rates, date)
        } catch (e: Exception) {
            val exceptionCatcher = HawkExceptionCatcher(applicationContext,
                HAWK_TOKEN)
            exceptionCatcher.logException(e)

            return Result.RETRY
        }

        return Result.SUCCESS
    }

    companion object {
        internal val TAG = "FIXER_GET_CURRENCY_SERVICE"
        internal val CurrenciesStorage = "CURRENCIES_STORAGE"
    }
}