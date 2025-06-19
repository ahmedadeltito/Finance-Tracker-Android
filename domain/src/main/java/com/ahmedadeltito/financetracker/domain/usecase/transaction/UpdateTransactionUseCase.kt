package com.ahmedadeltito.financetracker.domain.usecase.transaction

import com.ahmedadeltito.financetracker.common.Result
import com.ahmedadeltito.financetracker.common.UseCase
import com.ahmedadeltito.financetracker.common.di.IoDispatcher
import com.ahmedadeltito.financetracker.domain.entity.Transaction
import com.ahmedadeltito.financetracker.domain.repository.TransactionRepository
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class UpdateTransactionUseCase @Inject constructor(
    private val transactionRepository: TransactionRepository,
    @IoDispatcher dispatcher: CoroutineDispatcher
) : UseCase<UpdateTransactionUseCase.Params, Transaction>(dispatcher) {

    override suspend fun execute(parameters: Params): Transaction =
        when (val result = transactionRepository.updateTransaction(parameters.transaction)) {
            is Result.Success -> result.data
            is Result.Error -> throw result.exception
            is Result.Loading -> throw IllegalStateException("Loading state not supported for this use case")
        }

    data class Params(val transaction: Transaction)
} 