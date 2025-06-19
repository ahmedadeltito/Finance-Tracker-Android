package com.ahmedadeltito.financetracker.ui.model

import androidx.compose.ui.graphics.Color

data class TransactionUiModel(
    val id: String,
    val amount: String, // Pre-formatted amount with currency symbol and +/- sign
    val formattedDate: String, // Pre-formatted date string
    val category: CategoryUiModel,
    val note: String?,
    val type: TransactionTypeUiModel
) {
    data class CategoryUiModel(
        val id: String,
        val name: String,
        val color: Color, // Already parsed Color instead of String
        val icon: String // First letter of category name, pre-computed
    )
} 