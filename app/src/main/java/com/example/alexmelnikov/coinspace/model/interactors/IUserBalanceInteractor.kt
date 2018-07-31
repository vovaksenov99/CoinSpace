package com.example.alexmelnikov.coinspace.model.interactors

import com.example.alexmelnikov.coinspace.model.entities.Operation
import com.example.alexmelnikov.coinspace.model.entities.UserBalance

/**
 *  Created by Alexander Melnikov on 29.07.18.
 */
interface IUserBalanceInteractor {

    /**
     * Called in application constructor
     */
    fun initCurrencyRates()

    fun getUserBalance(): UserBalance

    fun mainCurrencyChanged(currencyBeforeChange: String)
    /**
     * @return updated user balance
     */
    fun executeNewOperation(type: Operation.OperationType?,
                            sum: Float,
                            currency: String): UserBalance

    fun convertCurrencyFromTo(money: Float, from: String, to: String): Float
}