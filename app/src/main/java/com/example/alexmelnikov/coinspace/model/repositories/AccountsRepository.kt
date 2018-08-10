package com.example.alexmelnikov.coinspace.model.repositories

import com.example.alexmelnikov.coinspace.model.entities.Account
import com.example.alexmelnikov.coinspace.model.entities.Operation
import io.reactivex.Single

/**
 *  Created by Alexander Melnikov on 27.07.18.
 *  TODO: Edit class header comment
 */
interface AccountsRepository {

    fun getAccountsOffline(): Single<List<Account>>


    fun findAccountByName(name: String): Single<Account>

    fun findAccountById(id: Long): Single<Account>

    fun insertAccountOfflineAsync(name: String, currency: String, balance: Float = 0f, color: Int,
                                  operations: List<Operation> = ArrayList(),callback: ()->Unit = {})

    fun insertAccountOfflineAsync(account: Account)

    fun updateAccountOfflineAsync(account: Account)

    fun deleteAll()

    fun removeByAccountId(accountId: Long, callback: () -> Unit = {})
}