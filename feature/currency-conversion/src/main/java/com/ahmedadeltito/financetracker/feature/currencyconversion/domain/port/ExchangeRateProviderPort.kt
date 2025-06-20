package com.ahmedadeltito.financetracker.feature.currencyconversion.domain.port

import com.ahmedadeltito.financetracker.common.Result
import java.math.BigDecimal

/**
 * Driven port that supplies raw exchange rates.
 *
 * The main application is expected to provide a concrete implementation of this interface
 * (via Dependency Injection) so that the currency-conversion plugin remains agnostic about
 * where the data comes from (REST API, local DB, mock, etc.).
 */
interface ExchangeRateProviderPort {

    /** Stable programmatic identifier (can be stored in preferences). */
    val id: String

    /** Human-readable label to show in the UI. */
    val displayName: String

    /**
     * Returns the conversion *rate* for converting one unit of [fromCurrencyCode] into [toCurrencyCode].
     * E.g. if 1 USD == 0.93 EUR then this function returns **0.93** for ("USD", "EUR").
     */
    suspend fun getRate(
        fromCurrencyCode: String,
        toCurrencyCode: String
    ): Result<BigDecimal>
} 