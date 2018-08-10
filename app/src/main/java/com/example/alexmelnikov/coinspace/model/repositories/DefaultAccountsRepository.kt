package com.example.alexmelnikov.coinspace.model.repositories

import android.util.Log
import com.example.alexmelnikov.coinspace.model.entities.Account
import com.example.alexmelnikov.coinspace.model.entities.Operation
import com.example.alexmelnikov.coinspace.model.persistance.Database
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class DefaultAccountsRepository(val database: Database) : AccountsRepository {
    override fun removeByAccountId(accountId: Long, callback: () -> Unit) {
        Single.create<Long> {
            database.accountDao().deleteByAccountId(accountId)
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    Log.d("mytag", "initAddTwoMainAccountsIfTableEmptyAsync success")
                    callback()
                },
                { Log.d("mytag", "initAddTwoMainAccountsIfTableEmptyAsync error!") })
    }

    override fun findAccountById(id: Long): Single<Account> {
        return Single.create<Account> {
            it.onSuccess(database.accountDao().findById(id))
        }
    }

    override fun getAccountsOffline(): Single<List<Account>> {
        return Single.create<List<Account>> {
            val accounts = database.accountDao().getAll()
            for (account in accounts) {
                account.operations =
                        database.operationsDao().findByAccountId(account.id!!).toMutableList()
            }
            it.onSuccess(accounts)
        }
        //TODO: На ревью сказали заменить create на just
        //Но данная реализация почему-то не работает
        //return Single.just<List<Account>>(database.getAll())
    }

    override fun findAccountByName(name: String): Single<Account> {
        return Single.create<Account> {
            it.onSuccess(database.accountDao().findByName(name))
        }
        //TODO: На ревью сказали заменить create на just
        //return Single.just<Account>(database.findByName(name))
    }

    override fun insertAccountOfflineAsync(name: String, currency: String, balance: Float,
                                           color: Int, operations: List<Operation>,
                                           callback: () -> Unit) {
        Completable.fromAction {
            val newAccount = Account(null, name, currency, balance, color)
            database.accountDao().insert(newAccount)
        }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                Log.d("mytag", "insertAccountOfflineAsync success")
                callback()
            },
                { Log.d("mytag", "insertAccountOfflineAsync error!") })
    }

    override fun insertAccountOfflineAsync(account: Account) {
        Completable.fromAction {
            database.accountDao().insert(account)
        }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ Log.d("mytag", "insertAccountOfflineAsync success") },
                { Log.d("mytag", "insertAccountOfflineAsync error!") })
    }

    override fun updateAccountOfflineAsync(account: Account) {
        Completable.fromAction {
            database.accountDao().updateAccounts(account)
        }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ Log.d("mytag", "updateAccountOfflineAsync success") },
                { Log.d("mytag", "updateAccountOfflineAsync error!") })
    }

    override fun deleteAll() {
        Completable.fromAction {
            database.accountDao().deleteAll()
        }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ Log.d("mytag", "deleteAll success") },
                { Log.d("mytag", "deleteAll error!") })
    }
}