package com.example.alexmelnikov.coinspace.model.entities

import android.arch.persistence.room.*

@Entity(tableName = "accounts")
data class Account(@PrimaryKey(autoGenerate = true) var id: Long?,
                   var name: String,
                   var currency: String,
                   var balance: Float,
                   var color: Int,
                   @TypeConverters(OperationTypeConverters::class) var operations: List<Operation>)



