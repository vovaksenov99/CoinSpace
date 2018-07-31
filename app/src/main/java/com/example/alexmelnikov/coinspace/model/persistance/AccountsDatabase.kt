package com.example.alexmelnikov.coinspace.model.persistance

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import android.content.Context
import com.example.alexmelnikov.coinspace.model.entities.Account
import com.example.alexmelnikov.coinspace.model.entities.OperationTypeConverters
import com.example.alexmelnikov.coinspace.model.persistance.dao.AccountDao


@Database(entities = arrayOf(Account::class), version = 1, exportSchema = false)
@TypeConverters(OperationTypeConverters::class)
abstract class AccountsDatabase : RoomDatabase() {

    abstract fun accountDao(): AccountDao

}