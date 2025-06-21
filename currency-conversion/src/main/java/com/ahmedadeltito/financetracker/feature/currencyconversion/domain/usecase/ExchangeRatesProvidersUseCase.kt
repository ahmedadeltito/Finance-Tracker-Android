package com.ahmedadeltito.financetracker.feature.currencyconversion.domain.usecase

import com.ahmedadeltito.financetracker.common.NoParameters
import com.ahmedadeltito.financetracker.common.Result.Error
import com.ahmedadeltito.financetracker.common.Result.Loading
import com.ahmedadeltito.financetracker.common.Result.Success
import com.ahmedadeltito.financetracker.common.SuspendUseCase
import com.ahmedadeltito.financetracker.common.di.IoDispatcher
import com.ahmedadeltito.financetracker.feature.currencyconversion.domain.repository.ExchangeRateRepository
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class ExchangeRatesProvidersUseCase @Inject constructor(
    private val repository: ExchangeRateRepository,
    @IoDispatcher override val dispatcher: CoroutineDispatcher
) : SuspendUseCase<NoParameters, List<Pair<String, String>>> {

    override suspend fun execute(
        params: NoParameters
    ): List<Pair<String, String>> = repository.getExchangeRatesProviders().let { result ->
        when (result) {
            is Success -> result.data
            is Error -> throw result.exception
            is Loading -> throw IllegalStateException("Loading state should not be returned from repository in suspend use case")
        }
    }
}