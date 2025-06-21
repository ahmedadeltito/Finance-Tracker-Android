package com.ahmedadeltito.financetracker.feature.transactions.currency.remote

import retrofit2.http.GET
import retrofit2.http.Query

interface ExchangeRateHostService {

    /**
     * GET https://api.exchangerate.host/convert?from=USD&to=EUR&amount=1.0&access_key=bcdd2c275133da3eae5d8e1c5cd2a867
     */
    @GET("convert")
    suspend fun convert(
        @Query("from") from: String,
        @Query("to") to: String,
        @Query("amount") amount: Double = 1.0,
        @Query("access_key") accessKey: String = "bcdd2c275133da3eae5d8e1c5cd2a867",
    ): ExchangeRateHostConvertResponse
} 