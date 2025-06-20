package com.ahmedadeltito.financetracker.feature.currencyconversion.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ahmedadeltito.financetracker.common.NoParameters
import com.ahmedadeltito.financetracker.common.Result
import com.ahmedadeltito.financetracker.common.di.CoroutineDispatchers
import com.ahmedadeltito.financetracker.feature.currencyconversion.domain.usecase.ConvertCurrencyUseCase
import com.ahmedadeltito.financetracker.feature.currencyconversion.domain.usecase.ExchangeRatesProvidersUseCase
import com.ahmedadeltito.financetracker.feature.currencyconversion.ui.CurrencyConverterState.Input
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.math.BigDecimal
import javax.inject.Inject

@HiltViewModel
class CurrencyConverterViewModel @Inject constructor(
    private val convertCurrencyUseCase: ConvertCurrencyUseCase,
    private val exchangeRatesProvidersUseCase: ExchangeRatesProvidersUseCase,
    private val dispatchers: CoroutineDispatchers
) : ViewModel() {

    private val _state = MutableStateFlow<CurrencyConverterState>(CurrencyConverterState.Loading)
    val state: StateFlow<CurrencyConverterState> = _state

    init {
        getExchangeRatesProviders()
    }

    private fun getExchangeRatesProviders() {
        viewModelScope.launch(dispatchers.io) {
            val exchangeRatesProviders: Result<List<Pair<String, String>>> =
                exchangeRatesProvidersUseCase(NoParameters)
            when(exchangeRatesProviders){
                is Result.Loading -> _state.value = CurrencyConverterState.Loading
                is Result.Success -> {
                    _state.value = Input(
                        providerOptions = exchangeRatesProviders.data.map { it.first to it.second },
                        selectedProviderId = exchangeRatesProviders.data.firstOrNull()?.first
                    )
                }
                is Result.Error -> _state.value = CurrencyConverterState.Error(
                    exchangeRatesProviders.exception.message ?: "Unknown error"
                )
            }
        }
    }

    fun onEvent(event: CurrencyConverterEvent) {
        when (event) {
            is CurrencyConverterEvent.OnAmountChange ->
                _state.value = currentInput().copy(amount = event.amount)
            is CurrencyConverterEvent.OnFromCurrencyChange ->
                _state.value = currentInput().copy(fromCode = event.currencyCode)
            is CurrencyConverterEvent.OnToCurrencyChange ->
                _state.value = currentInput().copy(toCode = event.currencyCode)
            is CurrencyConverterEvent.OnProviderChange ->
                _state.value = currentInput().copy(selectedProviderId = event.providerId)
            is CurrencyConverterEvent.OnConvertClick -> convert()
        }
    }

    private fun currentInput(): Input = _state.value as Input

    private fun convert() {
        val input = currentInput()
        val providerId = input.selectedProviderId ?: return
        val fromCode = input.fromCode ?: return
        val toCode = input.toCode ?: return
        val amount = input.amount.toBigDecimalOrNull() ?: return

        val params = ConvertCurrencyUseCase.Params(
            providerId = providerId,
            fromCode = fromCode,
            toCode = toCode,
            amount = amount
        )

        viewModelScope.launch(dispatchers.io) {
            val rateResult: Result<BigDecimal> = convertCurrencyUseCase(params)
            when (rateResult) {
                is Result.Loading -> _state.value = CurrencyConverterState.Loading
                is Result.Success -> {
                    val converted = amount.multiply(rateResult.data)
                    _state.value = CurrencyConverterState.Result(converted.toPlainString())
                }
                is Result.Error -> _state.value = CurrencyConverterState.Error(rateResult.exception.message ?: "Unknown error")
            }
        }
    }
}

sealed interface CurrencyConverterEvent {
    data class OnAmountChange(val amount: String) : CurrencyConverterEvent
    data class OnFromCurrencyChange(val currencyCode: String) : CurrencyConverterEvent
    data class OnToCurrencyChange(val currencyCode: String) : CurrencyConverterEvent
    data class OnProviderChange(val providerId: String) : CurrencyConverterEvent
    object OnConvertClick : CurrencyConverterEvent
}

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