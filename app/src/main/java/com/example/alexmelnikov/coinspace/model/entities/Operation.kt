package com.example.alexmelnikov.coinspace.model.entities

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "operations")
data class Operation(val type: String,
                     var sum: Float,
                     val currency: String,
                     val description: String,
                     val category: String,
                     val accountId: Long?,
                     @PrimaryKey(autoGenerate = true) var id: Long?,
                     val date: Long) {
}

enum class OperationType {
    EXPENSE, INCOME
}