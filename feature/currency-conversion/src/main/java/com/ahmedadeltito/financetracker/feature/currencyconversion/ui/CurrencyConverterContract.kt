package com.ahmedadeltito.financetracker.feature.currencyconversion.ui

sealed interface CurrencyConverterState {
    data class Input(
        val amount: String = "",
        val fromCode: String? = null,
        val toCode: String? = null,
        val providerOptions: List<Pair<String, String>> = emptyList(),
        val selectedProviderId: String? = null,
    ) : CurrencyConverterState
    object Loading : CurrencyConverterState
    data class Result(val result: String) : CurrencyConverterState
    data class Error(val message: String) : CurrencyConverterState
}

sealed interface CurrencyConverterEvent {
    data class OnAmountChange(val amount: String) : CurrencyConverterEvent
    data class OnFromCurrencyChange(val currencyCode: String) : CurrencyConverterEvent
    data class OnToCurrencyChange(val currencyCode: String) : CurrencyConverterEvent
    data class OnProviderChange(val providerId: String) : CurrencyConverterEvent
    object OnConvertClick : CurrencyConverterEvent
    data object OnBackClick : CurrencyConverterEvent
}

sealed interface CurrencyConverterSideEffect {
    data object NavigateBack : CurrencyConverterSideEffect
    data class ShowSnackbar(val message: String) : CurrencyConverterSideEffect
}