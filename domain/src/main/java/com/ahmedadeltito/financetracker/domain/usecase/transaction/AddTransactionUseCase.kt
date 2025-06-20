package com.ahmedadeltito.financetracker.domain.usecase.transaction

import com.ahmedadeltito.financetracker.common.SuspendUseCase
import com.ahmedadeltito.financetracker.common.di.IoDispatcher
import com.ahmedadeltito.financetracker.domain.entity.Transaction
import com.ahmedadeltito.financetracker.domain.repository.TransactionRepository
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class AddTransactionUseCase @Inject constructor(
    private val transactionRepository: TransactionRepository,
    @IoDispatcher override val dispatcher: CoroutineDispatcher
) : SuspendUseCase<AddTransactionUseCase.Params, Transaction> {

    override suspend fun execute(params: Params): Transaction =
        transactionRepository.addTransaction(params.transaction)

    data class Params(val transaction: Transaction)
} 