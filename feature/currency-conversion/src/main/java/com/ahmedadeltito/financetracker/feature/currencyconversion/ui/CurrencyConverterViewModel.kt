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
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
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

    private val _sideEffect = Channel<CurrencyConverterSideEffect>()
    val sideEffect = _sideEffect.receiveAsFlow()

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
            is CurrencyConverterEvent.OnBackClick ->
                sendSideEffect(CurrencyConverterSideEffect.NavigateBack)
        }
    }

    private fun sendSideEffect(sideEffect: CurrencyConverterSideEffect){
        viewModelScope.launch(dispatchers.io) { _sideEffect.send(sideEffect) }
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