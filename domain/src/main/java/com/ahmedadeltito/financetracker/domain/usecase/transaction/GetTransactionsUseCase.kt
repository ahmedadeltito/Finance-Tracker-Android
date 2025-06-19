package com.ahmedadeltito.financetracker.domain.usecase.transaction

import com.ahmedadeltito.financetracker.common.FlowUseCase
import com.ahmedadeltito.financetracker.common.Result
import com.ahmedadeltito.financetracker.common.di.IoDispatcher
import com.ahmedadeltito.financetracker.domain.entity.Transaction
import com.ahmedadeltito.financetracker.domain.entity.TransactionType
import com.ahmedadeltito.financetracker.domain.repository.TransactionRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import java.util.Date
import javax.inject.Inject

class GetTransactionsUseCase @Inject constructor(
    private val transactionRepository: TransactionRepository,
    @IoDispatcher dispatcher: CoroutineDispatcher
) : FlowUseCase<GetTransactionsUseCase.Params, List<Transaction>>(dispatcher) {

    override fun execute(parameters: Params): Flow<Result<List<Transaction>>> = when (parameters) {
        is Params.All -> transactionRepository.getTransactions()
        is Params.ByDateRange -> transactionRepository.getTransactionsByDateRange(
            parameters.startDate,
            parameters.endDate
        )
        is Params.ByType -> transactionRepository.getTransactionsByType(parameters.type)
        is Params.ByCategory -> transactionRepository.getTransactionsByCategory(parameters.categoryId)
    }

    sealed class Params {
        object All : Params()
        data class ByDateRange(
            val startDate: Date,
            val endDate: Date
        ) : Params()
        data class ByType(val type: TransactionType) : Params()
        data class ByCategory(val categoryId: String) : Params()
    }
} 