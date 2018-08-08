package com.example.alexmelnikov.coinspace.model.persistance.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy.REPLACE
import android.arch.persistence.room.Query
import android.arch.persistence.room.Update
import com.example.alexmelnikov.coinspace.model.entities.DeferOperation
import com.example.alexmelnikov.coinspace.model.entities.Pattern

/**
 *  Created by Alexander Melnikov on 27.07.18.
 */
@Dao
interface PatternsDao {

    @Query("SELECT * from patterns")
    fun getAll(): List<Pattern>

    @Query("SELECT * from patterns WHERE id = :mid")
    fun getById(mid: Int): Pattern

    @Query("DELETE from patterns WHERE bill = :accountId")
    fun deleteByAccountId(accountId: Long)

    @Insert(onConflict = REPLACE)
    fun insert(pattern: Pattern)

    @Query("DELETE from patterns")
    fun deleteAll()

    @Query("DELETE from patterns WHERE id = :mid")
    fun deleteById(mid: Long)

    @Update
    fun updateAccounts(vararg pattern: Pattern)

}