package com.example.alexmelnikov.coinspace.model.persistance.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy.REPLACE
import android.arch.persistence.room.Query
import com.example.alexmelnikov.coinspace.model.entities.Account

/**
 *  Created by Alexander Melnikov on 27.07.18.
 *  TODO: Edit class header comment
 */
@Dao
interface AccountDao {

    @Query("SELECT * from accounts")
    fun getAll(): List<Account>

    @Query("SELECT * FROM accounts WHERE name LIKE :name")
    fun findByName(name: String): Account

    @Insert(onConflict = REPLACE)
    fun insert(account: Account)

    @Query("DELETE from accounts")
    fun deleteAll()

}