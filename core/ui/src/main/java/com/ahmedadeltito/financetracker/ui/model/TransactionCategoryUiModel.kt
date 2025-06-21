package com.ahmedadeltito.financetracker.ui.model

import androidx.compose.ui.graphics.Color

data class TransactionCategoryUiModel(
    val id: String,
    val name: String,
    val type: TransactionTypeUiModel,
    val color: Color, // Already parsed Color instead of String
    val icon: String // First letter of category name, pre-computed
)