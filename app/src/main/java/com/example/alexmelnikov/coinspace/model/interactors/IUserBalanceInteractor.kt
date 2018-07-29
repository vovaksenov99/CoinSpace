package com.example.alexmelnikov.coinspace.model.interactors

import com.example.alexmelnikov.coinspace.model.entities.Operation

/**
 *  Created by Alexander Melnikov on 29.07.18.
 *  TODO: Edit class header comment
 */
interface IUserBalanceInteractor {

    fun getUserBalance(): Pair<Float, String>

    fun mainCurrencyChanged(currencyBeforeChange: String)
    /**
     * @return updated user balance
     */
    fun executeNewOperation(type: Operation.OperationType?,
                            sum: Float,
                            currency: String): Float

    fun convertCurrencyFromTo(money: Float, from: String, to: String): Float
}