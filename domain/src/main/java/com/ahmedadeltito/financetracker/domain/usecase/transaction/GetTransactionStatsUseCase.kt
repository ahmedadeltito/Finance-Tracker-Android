package com.ahmedadeltito.financetracker.domain.usecase.transaction

import com.ahmedadeltito.financetracker.common.FlowUseCase
import com.ahmedadeltito.financetracker.common.Result
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
    @IoDispatcher dispatcher: CoroutineDispatcher
) : FlowUseCase<GetTransactionStatsUseCase.Params, GetTransactionStatsUseCase.TransactionStats>(dispatcher) {

    override fun execute(parameters: Params): Flow<Result<TransactionStats>> {
        val incomeFlow = transactionRepository.getTotalAmountByTypeAndDateRange(
            TransactionType.INCOME,
            parameters.startDate,
            parameters.endDate
        )

        val expenseFlow = transactionRepository.getTotalAmountByTypeAndDateRange(
            TransactionType.EXPENSE,
            parameters.startDate,
            parameters.endDate
        )

        return combine(incomeFlow, expenseFlow) { incomeResult, expenseResult ->
            when {
                incomeResult is Result.Error -> Result.error(incomeResult.exception)
                expenseResult is Result.Error -> Result.error(expenseResult.exception)
                incomeResult is Result.Success && expenseResult is Result.Success -> {
                    val income = incomeResult.data
                    val expense = expenseResult.data
                    Result.success(
                        TransactionStats(
                            totalIncome = income,
                            totalExpense = expense,
                            balance = income.subtract(expense)
                        )
                    )
                }
                else -> Result.loading()
            }
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