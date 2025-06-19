package com.ahmedadeltito.financetracker.domain.usecase.category

import com.ahmedadeltito.financetracker.common.Result
import com.ahmedadeltito.financetracker.common.UseCase
import com.ahmedadeltito.financetracker.common.di.IoDispatcher
import com.ahmedadeltito.financetracker.domain.entity.TransactionCategory
import com.ahmedadeltito.financetracker.domain.repository.TransactionRepository
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class AddTransactionCategoryUseCase @Inject constructor(
    private val transactionRepository: TransactionRepository,
    @IoDispatcher dispatcher: CoroutineDispatcher
) : UseCase<AddTransactionCategoryUseCase.Params, TransactionCategory>(dispatcher) {

    override suspend fun execute(parameters: Params): TransactionCategory {
        return when (val result = transactionRepository.addTransactionCategory(parameters.category)) {
            is Result.Success -> result.data
            is Result.Error -> throw result.exception
            is Result.Loading -> throw IllegalStateException("Loading state not supported for this use case")
        }
    }

    data class Params(val category: TransactionCategory)
} 