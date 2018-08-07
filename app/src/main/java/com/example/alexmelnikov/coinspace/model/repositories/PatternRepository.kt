package com.example.alexmelnikov.coinspace.model.repositories

import com.example.alexmelnikov.coinspace.model.entities.Account
import com.example.alexmelnikov.coinspace.model.entities.Operation
import com.example.alexmelnikov.coinspace.model.entities.Pattern
import io.reactivex.Single


interface PatternRepository {

    fun deleteAll()

    fun removePattern(patternId: Int)

    fun getAllPatterns(): Single<List<Pattern>>

    fun getPatternById(id: Int): Single<Pattern>

    fun insertPattern(pattern: Pattern, callback: ()->Unit = {})
}