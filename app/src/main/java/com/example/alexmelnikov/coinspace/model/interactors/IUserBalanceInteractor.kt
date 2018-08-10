package com.example.alexmelnikov.coinspace.model.interactors

import android.content.Context
import com.example.alexmelnikov.coinspace.model.entities.Operation
import com.example.alexmelnikov.coinspace.model.entities.OperationType

/**
 *  Created by Alexander Melnikov on 29.07.18.
 */
interface IUserBalanceInteractor {

    /**
     * Called in application constructor
     */
    fun initCurrencyRates(context: Context, callback: () -> Unit)

    fun getUserBalance(): Money

    fun mainCurrencyChanged(currencyBeforeChange: String)
    /**
     * @return updated user balance
     */
    fun executeNewOperation(type: OperationType?, money: Money): Money

    fun setBalance(money: Money)

}