package com.example.alexmelnikov.coinspace.model.entities

import android.arch.persistence.room.*
import java.sql.Time
import java.util.*
import java.util.concurrent.TimeUnit

@Entity(tableName = "deferOperations")
data class DeferOperation(@PrimaryKey(autoGenerate = true) var id: Long?,
                          var description: String,
                          var nextRepeatDay: Int,
                          var currency: String,
                          var accountId: Long,
                          var nextRepeatMonth: Int,
                          var nextRepeatYear: Int,
                          var repeatDays: Int,
                          var moneyCount: Float,
                          var category: String)



