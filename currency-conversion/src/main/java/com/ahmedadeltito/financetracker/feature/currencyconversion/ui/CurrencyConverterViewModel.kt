package com.ahmedadeltito.financetracker.feature.currencyconversion.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ahmedadeltito.financetracker.common.NoParameters
import com.ahmedadeltito.financetracker.common.Result
import com.ahmedadeltito.financetracker.common.di.CoroutineDispatchers
import com.ahmedadeltito.financetracker.feature.currencyconversion.domain.usecase.ConvertCurrencyUseCase
import com.ahmedadeltito.financetracker.feature.currencyconversion.domain.usecase.ExchangeRatesProvidersUseCase
import com.ahmedadeltito.financetracker.feature.currencyconversion.ui.CurrencyConverterSideEffect.ShowConversionResultDialog
import com.ahmedadeltito.financetracker.feature.currencyconversion.ui.CurrencyConverterSideEffect.ShowSnackbar
import com.ahmedadeltito.financetracker.feature.currencyconversion.ui.CurrencyConverterUiState.Form
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

    private val _state =
        MutableStateFlow<CurrencyConverterUiState>(CurrencyConverterUiState.Loading)
    val state: StateFlow<CurrencyConverterUiState> = _state

    private val _sideEffect = Channel<CurrencyConverterSideEffect>()
    val sideEffect = _sideEffect.receiveAsFlow()

    init {
        getExchangeRatesProviders()
    }

    fun onEvent(event: CurrencyConverterEvent) {
        when (event) {
            is CurrencyConverterEvent.OnAmountChange ->
                _state.value = currentInput().let { input ->
                    val updated = input.copy(amount = event.amount)
                    val validation = input.validation.copy(
                        amountError = CurrencyConversionFormValidator.validateAmount(event.amount)
                    )
                    updated.copy(validation = validation)
                }
            is CurrencyConverterEvent.OnFromCurrencyChange ->
                _state.value = currentInput().let { input ->
                    val updated = input.copy(fromCode = event.currencyCode)
                    val validation = input.validation.copy(
                        fromCodeError = CurrencyConversionFormValidator.validateFromCode(event.currencyCode)
                    )
                    updated.copy(validation = validation)
                }
            is CurrencyConverterEvent.OnToCurrencyChange ->
                _state.value = currentInput().let { input ->
                    val updated = input.copy(toCode = event.currencyCode)
                    val validation = input.validation.copy(
                        toCodeError = CurrencyConversionFormValidator.validateToCode(event.currencyCode)
                    )
                    updated.copy(validation = validation)
                }
            is CurrencyConverterEvent.OnProviderChange ->
                _state.value = currentInput().let { input ->
                    val updated = input.copy(selectedProviderId = event.providerId)
                    val validation = input.validation.copy(
                        providerIdError = CurrencyConversionFormValidator.validateProviderId(event.providerId)
                    )
                    updated.copy(validation = validation)
                }
            is CurrencyConverterEvent.OnConvertClick -> convert()
            is CurrencyConverterEvent.OnBackClick -> sendSideEffect(CurrencyConverterSideEffect.NavigateBack)
        }
    }

    private fun sendSideEffect(sideEffect: CurrencyConverterSideEffect) {
        viewModelScope.launch(dispatchers.io) { _sideEffect.send(sideEffect) }
    }

    private fun currentInput(): Form = _state.value as Form

    private fun getExchangeRatesProviders() {
        viewModelScope.launch(dispatchers.io) {
            val exchangeRatesProviders: Result<List<Pair<String, String>>> =
                exchangeRatesProvidersUseCase(NoParameters)
            when (exchangeRatesProviders) {
                is Result.Loading -> _state.value = CurrencyConverterUiState.Loading
                is Result.Success -> {
                    _state.value = Form(
                        providerOptions = exchangeRatesProviders.data.map { it.first to it.second },
                        selectedProviderId = exchangeRatesProviders.data.firstOrNull()?.first
                    )
                }
                is Result.Error ->
                    sendSideEffect(ShowSnackbar(exchangeRatesProviders.exception.message ?: "Unknown error"))
            }
        }
    }

    private fun convert() {
        val input = currentInput()

        val validation = CurrencyConversionFormValidator.validateForm(input)
        if (validation.hasErrors) {
            _state.value = input.copy(validation = validation)
            return
        }

        val providerId = input.selectedProviderId!!
        val fromCode = input.fromCode!!.trim()
        val toCode = input.toCode!!.trim()
        val amount = input.amount.toBigDecimal()

        val params = ConvertCurrencyUseCase.Params(
            providerId = providerId,
            fromCode = fromCode,
            toCode = toCode,
            amount = amount
        )

        viewModelScope.launch(dispatchers.io) {
            val rateResult: Result<BigDecimal> = convertCurrencyUseCase(params)
            when (rateResult) {
                is Result.Loading -> _state.value = CurrencyConverterUiState.Loading
                is Result.Success -> {
                    val converted = rateResult.data
                    val providerName =
                        input.providerOptions.firstOrNull { it.first == providerId }?.second
                    val result = "Currency conversion" +
                            "\nFrom $fromCode to $toCode " +
                            "\nUsing $providerName : " +
                            "\n\n${converted.toPlainString()} $toCode"
                    sendSideEffect(ShowConversionResultDialog(conversionResult = result))
                }
                is Result.Error -> sendSideEffect(ShowSnackbar(rateResult.exception.message ?: "Unknown error"))
            }
        }
    }
}