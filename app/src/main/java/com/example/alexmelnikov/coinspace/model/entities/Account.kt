package com.example.alexmelnikov.coinspace.model.entities

import android.arch.persistence.room.*
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.io.Serializable

@Entity(tableName = "accounts")
data class Account(@PrimaryKey(autoGenerate = true) var id: Long?,
                   var name: String,
                   var currency: String,
                   var balance: Float,
                   var color: Int)
{
    @Ignore
    var operations: MutableList<Operation> = mutableListOf()
}



