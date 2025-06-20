package com.ahmedadeltito.financetracker.feature.currencyconversion.domain.repository

import com.ahmedadeltito.financetracker.common.Result
import com.ahmedadeltito.financetracker.feature.currencyconversion.domain.port.ExchangeRateProviderPort
import java.math.BigDecimal

interface ExchangeRateRepository {

    suspend fun convert(
        providerId: String,
        fromCurrencyCode: String,
        toCurrencyCode: String,
        amount: BigDecimal,
    ): Result<BigDecimal>

    suspend fun getExchangeRatesProviders(): Result<List<Pair<String, String>>>
} 