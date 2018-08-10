package com.example.alexmelnikov.coinspace

import com.example.alexmelnikov.coinspace.model.Currency
import com.example.alexmelnikov.coinspace.model.getCurrencyByString
import com.example.alexmelnikov.coinspace.model.interactors.CurrencyConverter
import com.example.alexmelnikov.coinspace.model.interactors.Money
import com.example.alexmelnikov.coinspace.util.find
import com.example.alexmelnikov.coinspace.util.formatToMoneyString
import com.example.alexmelnikov.coinspace.util.getDate
import com.example.alexmelnikov.coinspace.util.prefixFunction
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.rules.ExpectedException

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class UnitTests {
    /**
     * Данные тесты не являются достаточным минимум покрытия кода. Я просто не успел добавить больше
     * TODO
     */
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }


    @Test
    fun formatToMoneyStringTest() {
        val list = mutableListOf(
            Money(10f, Currency.USD),
            Money(10.23456f, Currency.USD),
            Money(9999999f, Currency.GBP),
            Money(0f, Currency.EUR))

        val listRez = mutableListOf("10 USD", "10,23 USD", "9 999 999 GBP", "0 EUR")

        for (i in 0 until list.size)
            assertEquals(listRez[i], formatToMoneyString(list[i]))
    }

    @Test
    fun getCurrencyByStringTest() {
        val list = mutableListOf(
            "EUR", "USD", "RUB")

        val listRez = mutableListOf(Currency.EUR, Currency.USD, Currency.RUR)

        for (i in 0 until list.size)
            assertEquals(listRez[i], getCurrencyByString(list[i]))
    }

    @Test(expected = Exception::class)
    fun getCurrencyByStringTestBad() {
      getCurrencyByString("NONE")
    }

    @Test
    fun currencyConverterTest() {
        val currencyConverter = CurrencyConverter()
        assertEquals(0.8611f,
            currencyConverter.convertCurrency(Money(1f, Currency.USD), Currency.EUR).count)
        assertEquals(0.372f,
            currencyConverter.convertCurrency(Money(23.45f, Currency.RUR), Currency.USD).count,
            0.001f)
    }

    @Test
    fun kmpTest()
    {
        assertEquals(true,find("a","aa"))
        assertEquals(true,find("","defrtgyr"))
        assertEquals(false,find("i","asddb"))
    }

    @Test
    fun kmpTestPrecalc()
    {
        assertEquals(true,find("a","aa",prefixFunction("a#aa")))
        assertEquals(true,find("","defrtgyr",prefixFunction("frt#defrtgyr")))
        assertEquals(false,find("i","asddb",prefixFunction("i#asddb")))
    }

    @Test
    fun prefixFunctionTestPrecalc()
    {
        assertEquals(listOf(0,0,1,1), prefixFunction("a#aa"))
        assertEquals(listOf(0,0,0,0,0,0,1,2,3,0,0,0), prefixFunction("frt#defrtgyr"))
        assertEquals(listOf(0,0,0,0,0,0,0), prefixFunction("i#asddb"))
    }

    @Test
    fun timestampToStringDate()
    {
        assertEquals("30.11.1973", getDate(123456787654))
        assertEquals("05.08.2713", getDate(23465478457483))
        assertEquals("01.01.1970", getDate(-23526))
    }


}
