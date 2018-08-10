package com.example.alexmelnikov.coinspace.model.repositories

import com.example.alexmelnikov.coinspace.model.entities.Account
import com.example.alexmelnikov.coinspace.model.entities.DeferOperation
import com.example.alexmelnikov.coinspace.model.entities.Operation
import io.reactivex.Single
import java.util.*

/**
 *  Created by Aksenov Vladimir 03.08.2018
 */
interface IDeferOperationsRepository{

    fun getAllOperations(): Single<List<DeferOperation>>

    fun addNewOperation(deferOperation: DeferOperation)

    fun removeOperation(deferOperation: DeferOperation)

    fun removeOperationsByAccountId(accountId: Long)

    fun removeAllOperation()

    fun getOperationById(id: Long): Single<DeferOperation>

    fun getOperationsByDay(day: Int, month: Int, year: Int): Single<List<DeferOperation>>
}