package com.ahmedadeltito.financetracker.feature.currencyconversion.domain.entity

import java.math.BigDecimal

/**
 * Request parameters for a currency conversion operation.
 */
data class ConversionRequest(
    val providerId: String,
    val from: Currency,
    val to: Currency,
    val amount: BigDecimal,
) 