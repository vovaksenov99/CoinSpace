package com.example.alexmelnikov.coinspace.model.persistance.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy.REPLACE
import android.arch.persistence.room.Query
import android.arch.persistence.room.Update
import com.example.alexmelnikov.coinspace.model.entities.Operation

/**
 *  Created by Alexander Melnikov on 27.07.18.
 */
@Dao
interface OperationsDao {

    @Query("SELECT * from operations")
    fun getAll(): List<Operation>

    @Query("SELECT * FROM operations WHERE id LIKE :id")
    fun findById(id: Long): Operation

    @Query("SELECT * FROM operations WHERE accountId LIKE :accountId")
    fun findByAccountId(accountId: Long): List<Operation>

    @Insert(onConflict = REPLACE)
    fun insert(operation: Operation): Long

    @Query("DELETE from operations")
    fun deleteAll()

    @Query("DELETE from operations WHERE id = :id")
    fun deleteById(id: Long)

    @Query("DELETE from operations WHERE accountId = :accountId")
    fun deleteByAccountId(accountId: Long)


    @Update
    fun updateAccounts(vararg operation: Operation)

}