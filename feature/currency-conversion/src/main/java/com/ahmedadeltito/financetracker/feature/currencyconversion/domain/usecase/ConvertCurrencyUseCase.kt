package com.ahmedadeltito.financetracker.feature.currencyconversion.domain.usecase

import com.ahmedadeltito.financetracker.common.Result
import com.ahmedadeltito.financetracker.common.SuspendUseCase
import com.ahmedadeltito.financetracker.common.di.IoDispatcher
import com.ahmedadeltito.financetracker.feature.currencyconversion.domain.repository.ExchangeRateRepository
import kotlinx.coroutines.CoroutineDispatcher
import java.math.BigDecimal
import javax.inject.Inject

class ConvertCurrencyUseCase @Inject constructor(
    private val repository: ExchangeRateRepository,
    @IoDispatcher override val dispatcher: CoroutineDispatcher
) : SuspendUseCase<ConvertCurrencyUseCase.Params, BigDecimal> {

    override suspend fun execute(params: Params): BigDecimal =
        repository.convert(
            providerId = params.providerId,
            fromCurrencyCode = params.fromCode,
            toCurrencyCode = params.toCode,
            amount = params.amount
        ).let { result ->
            when (result) {
                is Result.Success -> result.data
                is Result.Error -> throw result.exception
                is Result.Loading -> throw IllegalStateException("Loading state should not be returned from repository in suspend use case")
            }
        }

    data class Params(
        val providerId: String,
        val fromCode: String,
        val toCode: String,
        val amount: BigDecimal
    )
} 