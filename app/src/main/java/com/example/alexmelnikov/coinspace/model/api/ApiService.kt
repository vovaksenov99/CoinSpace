package com.example.alexmelnikov.coinspace.model.api

import com.example.alexmelnikov.coinspace.model.entities.ApiResponseRoot
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

/**
 *  Created by Alexander Melnikov on 29.07.18.
 *  API: https://openexchangerates.org/api/
 */
interface ApiService {

    @GET("latest.json")
    fun getRatesForCurrency(@Query("app_id") key: String,
                            @Query("base") base: String = "USD") : Call<ApiResponseRoot>

    //https://openexchangerates.org/api/latest.json/?app_id=API_KEY&base=USD
}