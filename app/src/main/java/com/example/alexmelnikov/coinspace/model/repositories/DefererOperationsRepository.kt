package com.example.alexmelnikov.coinspace.model.repositories

import android.util.Log
import com.example.alexmelnikov.coinspace.model.entities.Account
import com.example.alexmelnikov.coinspace.model.entities.DeferOperation
import com.example.alexmelnikov.coinspace.model.persistance.dao.DeferOperationsDao
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.*

class DeferOperationsRepository(private val deferOperationsDao: DeferOperationsDao) : DeferOperations {


    override fun getOperationsByDay(day: Int, month: Int, year: Int): Single<List<DeferOperation>> {
        return Single.create<List<DeferOperation>> {
            it.onSuccess(deferOperationsDao.getByDate(day, month, year))
        }
    }

    val TAG = "DeferOperationsRep"
    override fun addNewOperation(deferOperation: DeferOperation) {
        Completable.fromAction {
            deferOperationsDao.insert(deferOperation)
        }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ Log.d(TAG, "addNewOperation success") },
                {Log.d(TAG, "addNewOperation error!")})
    }

    override fun removeOperation(deferOperation: DeferOperation) {
        Completable.fromAction {
            deferOperationsDao.deleteById(deferOperation.id!!)
        }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ Log.d(TAG, "removeOperation success") },
                {Log.d(TAG, "removeOperation error!")})
    }

    override fun removeAllOperation() {
        Completable.fromAction {
            deferOperationsDao.deleteAll()
        }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ Log.d(TAG, "removeAllOperation success") },
                {Log.d(TAG, "removeAllOperation error!")})
    }

    override fun getAllOperations(): Single<List<DeferOperation>> {
        return Single.create<List<DeferOperation>> {
            it.onSuccess(deferOperationsDao.getAll())
        }
    }

    override fun getOperationById(id: Long): Single<DeferOperation> {
        return Single.create<DeferOperation> {
            it.onSuccess(deferOperationsDao.getById(id))
        }
    }

}