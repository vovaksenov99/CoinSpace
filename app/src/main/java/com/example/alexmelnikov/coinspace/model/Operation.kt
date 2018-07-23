package com.example.alexmelnikov.coinspace.model

import java.util.*

/**
 *  Created by Alexander Melnikov on 22.07.18.
 *  TODO: Edit class header comment
 */
data class Operation(val type: Operation.OperationType,
                     val sum: Float,
                     val currency: String,
                     val date: Date) {

    enum class OperationType {
        Expense, Income
    }
}