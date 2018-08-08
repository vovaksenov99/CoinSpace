package com.example.alexmelnikov.coinspace.model.repositories

import android.util.Log
import com.example.alexmelnikov.coinspace.model.entities.Pattern
import com.example.alexmelnikov.coinspace.model.persistance.dao.PatternsDao
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class PatternsRepository(private val patternsDao: PatternsDao) : IPatternsRepository {
    override fun removePatternByAccountId(accountId: Long) {
        Completable.fromAction {
            patternsDao.deleteByAccountId(accountId)
        }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                Log.d("mytag", "removePattern done!")
            },
                {
                    Log.d("mytag", "removePattern error!")
                })
    }

    override fun removePattern(patternId: Int) {
        Completable.fromAction {
            patternsDao.deleteById(patternId.toLong())
        }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                Log.d("mytag", "removePattern done!")
            },
                {
                    Log.d("mytag", "removePattern error!")
                })
    }

    override fun deleteAll() {
        Completable.fromAction {
            patternsDao.deleteAll()
        }.subscribeOn(Schedulers.io())
    }

    override fun getAllPatterns(): Single<List<Pattern>> {
        return Single.create<List<Pattern>> {
            it.onSuccess(patternsDao.getAll())
        }
    }

    override fun getPatternById(id: Int): Single<Pattern> {
        return Single.create<Pattern> {
            it.onSuccess(patternsDao.getById(id))
        }
    }

    override fun insertPattern(pattern: Pattern, callback: () -> Unit) {
        Completable.fromAction {
            patternsDao.insert(pattern)
        }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ callback() },
                {
                    Log.d("mytag", "updateAccountOfflineAsync error!")
                })

    }

}