package com.ahmedadeltito.financetracker.feature.currencyconversion.domain.entity

import java.math.BigDecimal

/**
 * The outcome of a currency conversion.
 */
data class ConversionResult(
    val convertedAmount: BigDecimal,
) 