package com.ahmedadeltito.financetracker.feature.currencyconversion.data.remote

import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Retrofit definitions for the free [Frankfurter](https://www.frankfurter.app/) exchange rate API.
 */
interface ExchangeRateApiService {

    /**
     * Fetch the latest rate(s) from Frankfurter.
     *
     * Docs: https://frankfurter.dev/#latest
     *   • [base] – ISO-4217 code for the base currency (e.g. "USD").
     *   • [symbols] – Comma-separated list of target currency codes (e.g. "EUR,GBP").
     */
    @GET("latest")
    suspend fun getLatestRates(
        @Query("base") base: String,
        @Query("symbols") symbols: String
    ): FrankfurterLatestResponse
} 