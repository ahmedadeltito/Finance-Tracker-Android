package com.ahmedadeltito.financetracker.feature.transactions.ui.list

import com.ahmedadeltito.financetracker.ui.model.TransactionUiModel

sealed interface TransactionListUiState {
    data object Loading : TransactionListUiState

    data class Success(
        val transactions: List<TransactionUiModel>,
        val totalIncome: String,
        val totalExpense: String,
        val balance: String
    ) : TransactionListUiState

    data class Error(
        val message: String
    ) : TransactionListUiState
}

sealed interface TransactionListEvent {
    data object Refresh : TransactionListEvent
    data class OnTransactionClick(val transactionId: String) : TransactionListEvent
    data object OnAddTransactionClick : TransactionListEvent
    data class SoftDeleteTransaction(val transactionId: String) : TransactionListEvent
    data class HardDeleteTransaction(val transactionId: String) : TransactionListEvent
    data object UndoDelete : TransactionListEvent
}

sealed interface TransactionListSideEffect {
    data class NavigateToTransactionDetails(val transactionId: String) : TransactionListSideEffect
    data object NavigateToAddTransaction : TransactionListSideEffect
    data class ShowUndoSnackbar(
        val transactionId: String?,
        val message: String
    ) : TransactionListSideEffect
} 