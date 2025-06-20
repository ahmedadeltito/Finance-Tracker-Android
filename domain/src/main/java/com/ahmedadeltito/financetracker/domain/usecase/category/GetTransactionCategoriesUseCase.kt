package com.ahmedadeltito.financetracker.domain.usecase.category

import com.ahmedadeltito.financetracker.common.FlowUseCase
import com.ahmedadeltito.financetracker.common.NoParameters
import com.ahmedadeltito.financetracker.common.di.IoDispatcher
import com.ahmedadeltito.financetracker.domain.entity.TransactionCategory
import com.ahmedadeltito.financetracker.domain.repository.TransactionRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTransactionCategoriesUseCase @Inject constructor(
    private val transactionRepository: TransactionRepository,
    @IoDispatcher override val dispatcher: CoroutineDispatcher
) : FlowUseCase<NoParameters, List<TransactionCategory>> {

    override fun execute(params: NoParameters): Flow<List<TransactionCategory>> =
        transactionRepository.getTransactionCategories()
} 