package com.example.alexmelnikov.coinspace.model.entities

/**
 *  Created by Alexander Melnikov on 29.07.18.
 */

data class UserBalance(var balance: Float,
                        var currency: String,
                        var balanceUsd: Float)