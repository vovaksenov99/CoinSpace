package com.example.alexmelnikov.coinspace.model.entities

import android.arch.persistence.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.*

/**
 *  Created by Alexander Melnikov on 22.07.18.
 *  Operation represents financial transaction
 */
data class Operation(val type: OperationType,
                     val sum: Float,
                     val currency: String,
                     val date: Date) {

    enum class OperationType {
        EXPENSE, INCOME
    }
}

class OperationTypeConverters {

    private val gson: Gson = Gson()

    @TypeConverter
    fun stringToOperationList(data: String?): List<Operation> =
            if (data == null) Collections.emptyList()
            else {
                val listType = object : TypeToken<List<Operation>>() {}.type
                gson.fromJson(data, listType)
            }

    @TypeConverter
    fun operationListToString(operations: List<Operation>) = gson.toJson(operations)!!
}