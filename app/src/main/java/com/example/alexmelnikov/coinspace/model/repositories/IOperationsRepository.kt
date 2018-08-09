package com.example.alexmelnikov.coinspace.model.repositories

import com.example.alexmelnikov.coinspace.model.entities.Account
import com.example.alexmelnikov.coinspace.model.entities.Operation
import com.example.alexmelnikov.coinspace.model.entities.Pattern
import io.reactivex.Single


interface IOperationsRepository {

    fun deleteAll()

    fun removeOperation(operationId: Long)

    fun getAllOperations(): Single<List<Operation>>

    fun getOperationById(id: Long): Single<Operation>

    fun getOperationByAccountId(accountId: Long): Single<List<Operation>>

    fun removeOperationsByAccountId(accountId: Long)


    fun insertOperation(operation: Operation, callback: (id: Long)->Unit = {})
}