package com.example.alexmelnikov.coinspace.model.repositories

import android.util.Log
import com.example.alexmelnikov.coinspace.model.entities.Operation
import com.example.alexmelnikov.coinspace.model.persistance.dao.OperationsDao
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class OperationsRepository(val operationsDao: OperationsDao) : IOperationsRepository {
    override fun getOperationByAccountId(accountId: Long): Single<List<Operation>> {
        return Single.create<List<Operation>> {
            it.onSuccess(operationsDao.findByAccountId(accountId))
        }
    }

    override fun deleteAll() {
        Completable.fromAction {
            operationsDao.deleteAll()
        }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                Log.d("OperationsRepository", "deleteAll success")
            },
                { Log.d("OperationsRepository", "deleteAll error!") })
    }

    override fun removeOperationsByAccountId(accountId: Long) {
        Completable.fromAction {
            operationsDao.deleteByAccountId(accountId)
        }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                Log.d("OperationsRepository", "removeOperationsByAccountId success")
            },
                { Log.d("OperationsRepository", "removeOperationsByAccountId error!") })
    }

    override fun removeOperation(operationId: Long) {
        Completable.fromAction {
            operationsDao.deleteById(operationId)
        }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                Log.d("OperationsRepository", "removeOperation success")
            },
                { Log.d("OperationsRepository", "removeOperation error!") })
    }

    override fun getAllOperations(): Single<List<Operation>> {
        return Single.create<List<Operation>> {
            it.onSuccess(operationsDao.getAll())
        }
    }

    override fun getOperationById(id: Long): Single<Operation> {
        return Single.create<Operation> {
            it.onSuccess(operationsDao.findById(id))
        }
    }

    override fun insertOperation(operation: Operation, callback: (id: Long) -> Unit) {
        Single.create<Long> {
            it.onSuccess(operationsDao.insert(operation))
        }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                Log.d("OperationsRepository", "insertOperation success")
                callback(it)
            },
                { Log.d("OperationsRepository", "insertOperation error!") })
    }

}