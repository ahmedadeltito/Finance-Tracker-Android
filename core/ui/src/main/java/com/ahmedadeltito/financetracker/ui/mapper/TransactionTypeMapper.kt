package com.ahmedadeltito.financetracker.ui.mapper

import com.ahmedadeltito.financetracker.domain.entity.TransactionType
import com.ahmedadeltito.financetracker.ui.model.TransactionTypeUiModel

object TransactionTypeMapper {
    fun TransactionType.toUiModel(): TransactionTypeUiModel {
        return when (this) {
            TransactionType.INCOME -> TransactionTypeUiModel.Income
            TransactionType.EXPENSE -> TransactionTypeUiModel.Expense
        }
    }

    fun TransactionTypeUiModel.toDomainModel(): TransactionType {
        return when (this) {
            TransactionTypeUiModel.Income -> TransactionType.INCOME
            TransactionTypeUiModel.Expense -> TransactionType.EXPENSE
        }
    }
} 