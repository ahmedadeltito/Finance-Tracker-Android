package com.ahmedadeltito.financetracker.domain.usecase.category

import com.ahmedadeltito.financetracker.common.FlowUseCase
import com.ahmedadeltito.financetracker.common.Result
import com.ahmedadeltito.financetracker.common.di.IoDispatcher
import com.ahmedadeltito.financetracker.domain.entity.TransactionCategory
import com.ahmedadeltito.financetracker.domain.repository.TransactionRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTransactionCategoriesUseCase @Inject constructor(
    private val transactionRepository: TransactionRepository,
    @IoDispatcher dispatcher: CoroutineDispatcher
) : FlowUseCase<GetTransactionCategoriesUseCase.Params, List<TransactionCategory>>(dispatcher) {

    override fun execute(parameters: Params): Flow<Result<List<TransactionCategory>>> {
        return transactionRepository.getTransactionCategories()
    }

    object Params
} 