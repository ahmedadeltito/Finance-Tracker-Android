package com.ahmedadeltito.financetracker.ui.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

sealed class TransactionTypeUiModel(
    val displayText: String,
    val color: Color,
    val icon: ImageVector
) {
    data object Income : TransactionTypeUiModel(
        displayText = "Income",
        color = com.ahmedadeltito.financetracker.ui.theme.Income,
        icon = Icons.AutoMirrored.Filled.ArrowForward
    )

    data object Expense : TransactionTypeUiModel(
        displayText = "Expense",
        color = com.ahmedadeltito.financetracker.ui.theme.Expense,
        icon = Icons.AutoMirrored.Filled.ArrowBack
    )
} 