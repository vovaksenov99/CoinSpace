package com.example.alexmelnikov.coinspace.model.persistance.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy.REPLACE
import android.arch.persistence.room.Query
import android.arch.persistence.room.Update
import com.example.alexmelnikov.coinspace.model.entities.Account
import com.example.alexmelnikov.coinspace.model.entities.DeferOperation

/**
 *  Created by Alexander Melnikov on 27.07.18.
 */
@Dao
interface DeferOperationsDao {

    @Query("SELECT * from deferOperations")
    fun getAll(): List<DeferOperation>

    @Query("SELECT * from deferOperations WHERE id = :mid")
    fun getById(mid: Long): DeferOperation

    @Query("SELECT * from deferOperations WHERE nextRepeatDay = :day AND nextRepeatMonth = :month AND nextRepeatYear = :year")
    fun getByDate(day: Int, month: Int, year: Int): List<DeferOperation>

    @Insert(onConflict = REPLACE)
    fun insert(deferOperation: DeferOperation)

    @Query("DELETE from deferOperations")
    fun deleteAll()

    @Query("DELETE from deferOperations WHERE id = :mid")
    fun deleteById(mid: Long)

    @Query("DELETE from deferOperations WHERE accountId = :accountId")
    fun deleteByAccountId(accountId: Long)


    @Update
    fun updateAccounts(vararg deferOperation: DeferOperation)

}