package com.ahmedadeltito.financetracker.feature.currencyconversion.data.repository

import com.ahmedadeltito.financetracker.common.Result
import com.ahmedadeltito.financetracker.common.map
import com.ahmedadeltito.financetracker.feature.currencyconversion.domain.port.ExchangeRateProviderPort
import com.ahmedadeltito.financetracker.feature.currencyconversion.domain.repository.ExchangeRateRepository
import java.math.BigDecimal
import javax.inject.Inject

class ExchangeRateRepositoryImpl @Inject constructor(
    private val providers: Set<@JvmSuppressWildcards ExchangeRateProviderPort>,
) : ExchangeRateRepository {

    override suspend fun convert(
        providerId: String,
        fromCurrencyCode: String,
        toCurrencyCode: String,
        amount: BigDecimal,
    ): Result<BigDecimal> {
        val provider = providers.firstOrNull { it.id == providerId } ?: providers.first()
        return provider.getRate(
            fromCurrencyCode = fromCurrencyCode,
            toCurrencyCode = toCurrencyCode
        ).map { rate -> amount.multiply(rate) }
    }

    override suspend fun getExchangeRatesProviders(): Result<List<Pair<String, String>>> =
        Result.success(data = providers.map { it.id to it.displayName })
} 