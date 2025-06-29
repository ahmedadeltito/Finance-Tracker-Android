package com.ahmedadeltito.financetracker.feature.transactions.common

import com.ahmedadeltito.financetracker.ui.model.TransactionFormValidationState
import com.ahmedadeltito.financetracker.ui.model.TransactionCategoryUiModel
import com.ahmedadeltito.financetracker.ui.model.TransactionUiModel

interface TransactionFormSuccess {
    val transaction: TransactionUiModel
    val categories: List<TransactionCategoryUiModel>
    val validation: TransactionFormValidationState
} 