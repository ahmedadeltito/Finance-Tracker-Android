package com.ahmedadeltito.financetracker.domain.usecase.transaction

import com.ahmedadeltito.financetracker.common.SuspendUseCase
import com.ahmedadeltito.financetracker.common.di.IoDispatcher
import com.ahmedadeltito.financetracker.domain.entity.Transaction
import com.ahmedadeltito.financetracker.domain.repository.TransactionRepository
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class UpdateTransactionUseCase @Inject constructor(
    private val transactionRepository: TransactionRepository,
    @IoDispatcher override val dispatcher: CoroutineDispatcher
) : SuspendUseCase<UpdateTransactionUseCase.Params, Transaction> {

    override suspend fun execute(params: Params): Transaction =
        transactionRepository.updateTransaction(params.transaction)

    data class Params(val transaction: Transaction)
} 