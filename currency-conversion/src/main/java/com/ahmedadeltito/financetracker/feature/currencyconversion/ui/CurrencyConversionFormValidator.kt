package com.ahmedadeltito.financetracker.feature.currencyconversion.ui

import java.math.BigDecimal

object CurrencyConversionFormValidator {

    fun validateProviderId(providerId: String?): String? = when {
        providerId == null || providerId.isBlank() -> "Currency provider is required"
        else -> null
    }

    fun validateFromCode(fromCode: String?): String? = when {
        fromCode == null || fromCode.isBlank() -> "From code is required"
        else -> null
    }

    fun validateToCode(toCode: String?): String? = when {
        toCode == null || toCode.isBlank() -> "To code is required"
        else -> null
    }

    fun validateAmount(amount: String): String? = when {
        amount.isBlank() -> "Amount is required"
        amount.toBigDecimalOrNull() == null -> "Invalid amount"
        amount.toBigDecimal() <= BigDecimal.ZERO -> "Amount must be greater than zero"
        else -> null
    }

    fun validateForm(
        formData: CurrencyConverterUiState.Form
    ): CurrencyConversionValidatorState = CurrencyConversionValidatorState(
        providerIdError = validateProviderId(formData.selectedProviderId),
        fromCodeError = validateFromCode(formData.fromCode),
        toCodeError = validateToCode(formData.toCode),
        amountError = validateAmount(formData.amount)
    )
} 