package com.ahmedadeltito.financetracker.feature.currencyconversion.ui

data class CurrencyConversionValidatorState(
    val providerIdError: String? = null,
    val fromCodeError: String? = null,
    val toCodeError: String? = null,
    val amountError: String? = null
) {
    val hasErrors = providerIdError != null || fromCodeError != null || toCodeError != null || amountError != null
}