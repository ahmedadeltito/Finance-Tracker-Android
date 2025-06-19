package com.ahmedadeltito.financetracker.feature.transactions.ui.add

import com.ahmedadeltito.financetracker.feature.transactions.ui.model.ValidationState
import com.ahmedadeltito.financetracker.ui.model.TransactionCategoryUiModel
import com.ahmedadeltito.financetracker.ui.model.TransactionTypeUiModel
import com.ahmedadeltito.financetracker.ui.model.TransactionUiModel
import java.util.Date

sealed interface AddTransactionState {
    data object Loading : AddTransactionState

    data class Success(
        val transaction: TransactionUiModel = TransactionUiModel.EMPTY,
        val categories: List<TransactionCategoryUiModel> = emptyList(),
        val validation: ValidationState = ValidationState()
    ) : AddTransactionState

    data class Error(
        val message: String
    ) : AddTransactionState
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
    data class ShowSuccess(val message: String) : AddTransactionSideEffect
}