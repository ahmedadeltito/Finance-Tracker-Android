package com.ahmedadeltito.financetracker.feature.currencyconversion.data.remote

import com.ahmedadeltito.financetracker.common.Result
import com.ahmedadeltito.financetracker.feature.currencyconversion.domain.port.ExchangeRateProviderPort
import java.math.BigDecimal
import javax.inject.Inject

class FrankfurterExchangeRateProvider @Inject constructor(
    private val api: ExchangeRateApiService
) : ExchangeRateProviderPort {

    override val id: String = "frankfurter"
    override val displayName: String = "Frankfurter.dev"

    override suspend fun getRate(fromCurrencyCode: String, toCurrencyCode: String): Result<BigDecimal> {
        return try {
            if (fromCurrencyCode.equals(toCurrencyCode, ignoreCase = true)) {
                return Result.success(BigDecimal.ONE)
            }
            val response = api.getLatestRates(base = fromCurrencyCode, symbols = toCurrencyCode)
            val rateValue = response.rates[toCurrencyCode]
                ?: return Result.error(IllegalStateException("No rate for $toCurrencyCode"))
            Result.success(BigDecimal.valueOf(rateValue))
        } catch (e: Exception) {
            Result.error(e)
        }
    }
} 