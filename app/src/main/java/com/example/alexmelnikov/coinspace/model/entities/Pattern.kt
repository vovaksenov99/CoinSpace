package com.example.alexmelnikov.coinspace.model.entities

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.arch.persistence.room.TypeConverter
import android.arch.persistence.room.TypeConverters
import com.example.alexmelnikov.coinspace.model.Category
import com.example.alexmelnikov.coinspace.model.Currency
import com.example.alexmelnikov.coinspace.model.getCategoryByString
import com.example.alexmelnikov.coinspace.model.getCurrencyByString

@Entity(tableName = "patterns")
data class Pattern(var bill: Int,
                   @PrimaryKey(autoGenerate = true) var id: Int?,
                   var type: String?,
                   var description: String,
                   @TypeConverters(CurrencyConverter::class) var currency: Currency,
                   @TypeConverters(CategoryConverter::class) var category: Category) {

}

class CurrencyConverter {

    @TypeConverter
    fun stringToCurrency(currency: String): Currency = getCurrencyByString(currency)

    @TypeConverter
    fun currencyToString(currency: Currency) = currency.toString()
}

class CategoryConverter {

    @TypeConverter
    fun stringToCategory(category: String): Category {
        return getCategoryByString(category)
    }

    @TypeConverter
    fun categoryToString(category: Category): String = category.toString()
}