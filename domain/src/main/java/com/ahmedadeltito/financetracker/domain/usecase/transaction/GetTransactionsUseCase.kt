package com.ahmedadeltito.financetracker.domain.usecase.transaction

import com.ahmedadeltito.financetracker.common.FlowUseCase
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
    @IoDispatcher override val dispatcher: CoroutineDispatcher
) : FlowUseCase<GetTransactionsUseCase.Params, List<Transaction>> {

    override fun execute(params: Params): Flow<List<Transaction>> = when (params) {
        is Params.All -> transactionRepository.getTransactions()
        is Params.ByDateRange -> transactionRepository.getTransactionsByDateRange(params.startDate, params.endDate)
        is Params.ByType -> transactionRepository.getTransactionsByType(params.type)
        is Params.ByCategory -> transactionRepository.getTransactionsByCategory(params.categoryId)
    }

    sealed class Params {
        object All : Params()
        data class ByDateRange(val startDate: Date, val endDate: Date) : Params()
        data class ByType(val type: TransactionType) : Params()
        data class ByCategory(val categoryId: String) : Params()
    }
} 