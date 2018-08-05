package com.example.alexmelnikov.coinspace.model.interactors

import android.content.Context
import com.example.alexmelnikov.coinspace.model.entities.Operation

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
    fun executeNewOperation(type: Operation.OperationType?, money: Money): Money

}