package com.ahmedadeltito.financetracker.feature.transactions.ui.add

import com.ahmedadeltito.financetracker.feature.transactions.common.TransactionFormSuccess
import com.ahmedadeltito.financetracker.ui.model.TransactionFormValidationState
import com.ahmedadeltito.financetracker.ui.model.TransactionCategoryUiModel
import com.ahmedadeltito.financetracker.ui.model.TransactionTypeUiModel
import com.ahmedadeltito.financetracker.ui.model.TransactionUiModel
import java.util.Date

sealed interface AddTransactionUiState {
    data object Loading : AddTransactionUiState

    data class Success(
        override val transaction: TransactionUiModel = TransactionUiModel.EMPTY,
        override val categories: List<TransactionCategoryUiModel> = emptyList(),
        override val validation: TransactionFormValidationState = TransactionFormValidationState()
    ) : AddTransactionUiState, TransactionFormSuccess

    data class Error(
        val message: String
    ) : AddTransactionUiState
}

sealed interface AddTransactionEvent {
    data class OnAmountChange(val amount: String) : AddTransactionEvent
    data class OnDescriptionChange(val description: String) : AddTransactionEvent
    data class OnDateChange(val date: Date) : AddTransactionEvent
    data class OnTypeChange(val type: TransactionTypeUiModel) : AddTransactionEvent
    data class OnCategorySelect(val categoryId: String) : AddTransactionEvent
    data object OnSaveClick : AddTransactionEvent
    data object OnBackClick : AddTransactionEvent
}

sealed interface AddTransactionSideEffect {
    data object NavigateBack : AddTransactionSideEffect
    data class ShowSnackbar(val message: String) : AddTransactionSideEffect
}