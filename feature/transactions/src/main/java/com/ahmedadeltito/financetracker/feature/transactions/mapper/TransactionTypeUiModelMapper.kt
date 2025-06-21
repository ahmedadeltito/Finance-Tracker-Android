package com.ahmedadeltito.financetracker.feature.transactions.mapper

import com.ahmedadeltito.financetracker.domain.entity.TransactionType
import com.ahmedadeltito.financetracker.ui.model.TransactionTypeUiModel

fun TransactionType.toUiModel(): TransactionTypeUiModel {
    return when (this) {
        TransactionType.INCOME -> TransactionTypeUiModel.Income
        TransactionType.EXPENSE -> TransactionTypeUiModel.Expense
    }
}