package com.ahmedadeltito.financetracker.ui.model

import androidx.compose.ui.graphics.Color

data class TransactionUiModel(
    val id: String,
    val amount: String, // Pre-formatted amount with currency symbol and +/- sign
    val formattedDate: String, // Pre-formatted date string
    val category: TransactionCategoryUiModel,
    val note: String?,
    val type: TransactionTypeUiModel
) {
    companion object {
        val EMPTY = TransactionUiModel(
            id = "",
            amount = "",
            formattedDate = "",
            category = TransactionCategoryUiModel(
                id = "",
                name = "",
                type = TransactionTypeUiModel.Income,
                color = Color.Unspecified,
                icon = ""
            ),
            note = null,
            type = TransactionTypeUiModel.Income
        )
    }
} 