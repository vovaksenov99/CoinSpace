package com.example.alexmelnikov.coinspace.model.entities

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.io.Serializable
import java.util.*

@Entity(tableName = "operations")
data class Operation(val type: String,
                     var sum: Float,
                     val currency: String,
                     val category: String,
                     val accountId: Long?,
                     @PrimaryKey(autoGenerate = true) var id: Long?,
                     val date: String)
enum class OperationType {
    EXPENSE, INCOME
}