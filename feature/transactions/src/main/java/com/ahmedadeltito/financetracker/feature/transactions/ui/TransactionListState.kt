package com.ahmedadeltito.financetracker.feature.transactions.ui

import com.ahmedadeltito.financetracker.ui.model.TransactionUiModel
import java.math.BigDecimal

sealed interface TransactionListState {
    data object Loading : TransactionListState

    data class Success(
        val transactions: List<TransactionUiModel>,
        val totalIncome: String,
        val totalExpense: String,
        val balance: String
    ) : TransactionListState

    data class Error(
        val message: String
    ) : TransactionListState
}

sealed interface TransactionListEvent {
    data object Refresh : TransactionListEvent
    data class OnTransactionClick(val transactionId: String) : TransactionListEvent
    data object OnAddTransactionClick : TransactionListEvent
    data class DeleteTransaction(val transactionId: String) : TransactionListEvent
}

sealed interface TransactionListSideEffect {
    data class NavigateToTransactionDetails(val transactionId: String) : TransactionListSideEffect
    data object NavigateToAddTransaction : TransactionListSideEffect
    data class ShowSnackbar(val message: String) : TransactionListSideEffect
} 