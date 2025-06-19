package com.ahmedadeltito.financetracker.feature.transactions.ui.update

import com.ahmedadeltito.financetracker.feature.transactions.ui.model.ValidationState
import com.ahmedadeltito.financetracker.ui.model.TransactionCategoryUiModel
import com.ahmedadeltito.financetracker.ui.model.TransactionTypeUiModel
import com.ahmedadeltito.financetracker.ui.model.TransactionUiModel
import java.util.Date

sealed interface UpdateTransactionState {
    data object Loading : UpdateTransactionState

    data class Success(
        val transaction: TransactionUiModel = TransactionUiModel.EMPTY,
        val categories: List<TransactionCategoryUiModel> = emptyList(),
        val validation: ValidationState = ValidationState()
    ) : UpdateTransactionState

    data class Error(
        val message: String
    ) : UpdateTransactionState
}

sealed interface UpdateTransactionEvent {
    data class OnAmountChange(val amount: String) : UpdateTransactionEvent
    data class OnDescriptionChange(val description: String) : UpdateTransactionEvent
    data class OnDateChange(val date: Date) : UpdateTransactionEvent
    data class OnTypeChange(val type: TransactionTypeUiModel) : UpdateTransactionEvent
    data class OnCategorySelect(val categoryId: String) : UpdateTransactionEvent
    data object OnUpdateClick : UpdateTransactionEvent
    data object OnBackClick : UpdateTransactionEvent
}

sealed interface UpdateTransactionSideEffect {
    data object NavigateBack : UpdateTransactionSideEffect
    data class ShowSuccess(val message: String) : UpdateTransactionSideEffect
}