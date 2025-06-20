package com.ahmedadeltito.financetracker.domain.usecase.transaction

import com.ahmedadeltito.financetracker.common.FlowUseCase
import com.ahmedadeltito.financetracker.common.di.IoDispatcher
import com.ahmedadeltito.financetracker.domain.entity.TransactionType
import com.ahmedadeltito.financetracker.domain.repository.TransactionRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.math.BigDecimal
import javax.inject.Inject

class GetTransactionStatsUseCase @Inject constructor(
    private val transactionRepository: TransactionRepository,
    @IoDispatcher override val dispatcher: CoroutineDispatcher
) : FlowUseCase<GetTransactionStatsUseCase.Params, GetTransactionStatsUseCase.TransactionStats> {

    override fun execute(params: Params): Flow<TransactionStats> {
        val incomeFlow = transactionRepository.getTotalAmountByTypeAndDateRange(
            TransactionType.INCOME,
            params.startDate,
            params.endDate
        )

        val expenseFlow = transactionRepository.getTotalAmountByTypeAndDateRange(
            TransactionType.EXPENSE,
            params.startDate,
            params.endDate
        )

        return combine(incomeFlow, expenseFlow) { income, expense ->
            TransactionStats(
                totalIncome = income,
                totalExpense = expense,
                balance = income.subtract(expense)
            )
        }
    }

    data class Params(
        val startDate: java.util.Date,
        val endDate: java.util.Date
    )

    data class TransactionStats(
        val totalIncome: BigDecimal,
        val totalExpense: BigDecimal,
        val balance: BigDecimal
    )
} 