package com.example.alexmelnikov.coinspace.model.repositories

import android.util.Log
import com.example.alexmelnikov.coinspace.model.entities.Account
import com.example.alexmelnikov.coinspace.model.entities.Operation
import com.example.alexmelnikov.coinspace.model.persistance.dao.AccountDao
import io.reactivex.Completable
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class DefaultAccountsRepository(private val accountDao: AccountDao) : AccountsRepository {

    override fun getAccountsOffline(): Single<List<Account>> {
        return Single.create<List<Account>> {
            it.onSuccess(accountDao.getAll())
        }
    }

    override fun findAccountByName(name: String): Single<Account> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun insertAccountOffline(name: String, currency: String, balance: Float, color: Int, operations: List<Operation>) {
        Completable.fromAction {
            val newAccount = Account(null, name, currency, balance, color, operations)
            accountDao.insert(newAccount)
        }.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ Log.d("mytag", "success") },
                        {Log.d("mytag", "error")})
    }

    override fun updateAccountOffline() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}