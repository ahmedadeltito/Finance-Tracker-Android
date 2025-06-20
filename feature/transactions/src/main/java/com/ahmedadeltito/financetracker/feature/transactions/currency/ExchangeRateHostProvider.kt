package com.ahmedadeltito.financetracker.feature.transactions.currency

import com.ahmedadeltito.financetracker.common.Result
import com.ahmedadeltito.financetracker.feature.currencyconversion.domain.port.ExchangeRateProviderPort
import com.ahmedadeltito.financetracker.feature.transactions.currency.remote.ExchangeRateHostService
import java.math.BigDecimal
import javax.inject.Inject

class ExchangeRateHostProvider @Inject constructor(
    private val api: ExchangeRateHostService
) : ExchangeRateProviderPort {

    override val id: String = "exchange_rate_host"
    override val displayName: String = "ExchangeRate.host"

    override suspend fun getRate(fromCurrencyCode: String, toCurrencyCode: String): Result<BigDecimal> {
        return try {
            if (fromCurrencyCode.equals(toCurrencyCode, ignoreCase = true)) {
                return Result.success(BigDecimal.ONE)
            }
            val convert = api.convert(from = fromCurrencyCode, to = toCurrencyCode, amount = 1.0)
            if (!convert.success) {
                return Result.error(IllegalStateException("API call unsuccessful"))
            }
            val rate = if (convert.result != 0.0) convert.result else null
                ?: return Result.error(IllegalStateException("Rate missing in response"))
            Result.success(BigDecimal.valueOf(rate))
        } catch (e: Exception) {
            Result.error(e)
        }
    }
} 