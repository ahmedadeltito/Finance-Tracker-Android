package com.ahmedadeltito.financetracker.domain.usecase.category

import com.ahmedadeltito.financetracker.common.SuspendUseCase
import com.ahmedadeltito.financetracker.common.di.IoDispatcher
import com.ahmedadeltito.financetracker.domain.entity.TransactionCategory
import com.ahmedadeltito.financetracker.domain.repository.TransactionRepository
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class UpdateTransactionCategoryUseCase @Inject constructor(
    private val transactionRepository: TransactionRepository,
    @IoDispatcher override val dispatcher: CoroutineDispatcher
) : SuspendUseCase<UpdateTransactionCategoryUseCase.Params, TransactionCategory> {

    override suspend fun execute(params: Params): TransactionCategory =
        transactionRepository.updateTransactionCategory(params.category)

    data class Params(val category: TransactionCategory)
} 