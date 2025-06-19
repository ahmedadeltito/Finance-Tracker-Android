package com.ahmedadeltito.financetracker.ui.mapper

import androidx.compose.ui.graphics.Color
import androidx.core.graphics.toColorInt
import com.ahmedadeltito.financetracker.domain.entity.Transaction
import com.ahmedadeltito.financetracker.domain.entity.TransactionCategory
import com.ahmedadeltito.financetracker.ui.model.TransactionTypeUiModel
import com.ahmedadeltito.financetracker.ui.model.TransactionUiModel
import com.ahmedadeltito.financetracker.ui.theme.Other
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Locale

object TransactionMapper {
    fun Transaction.toUiModel(): TransactionUiModel {
        val transactionType = TransactionTypeMapper.run { type.toUiModel() }
        return TransactionUiModel(
            id = id,
            amount = formatAmount(amount, transactionType),
            formattedDate = formatDate(date),
            category = category.toUiModel(),
            note = notes.orEmpty(),
            type = transactionType
        )
    }

    private fun TransactionCategory.toUiModel(): TransactionUiModel.CategoryUiModel {
        return TransactionUiModel.CategoryUiModel(
            id = id,
            name = name,
            color = parseColor(color),
            icon = iconUrl ?: name.first().toString()
        )
    }

    private fun formatDate(date: java.util.Date): String {
        val dateFormatter = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        return dateFormatter.format(date)
    }

    private fun formatAmount(amount: java.math.BigDecimal, type: TransactionTypeUiModel): String {
        val currencyFormatter = NumberFormat.getCurrencyInstance(Locale.getDefault()).apply {
            maximumFractionDigits = 2
            minimumFractionDigits = 2
        }
        return currencyFormatter.format(amount.abs()).let { formattedAmount ->
            "${type.amountPrefix}$formattedAmount"
        }
    }

    private fun parseColor(colorString: String?): Color = try {
        val color = when {
            colorString == null -> return Other
            colorString.startsWith("#") -> colorString
            else -> "#$colorString"
        }
        Color(color.toColorInt())
    } catch (e: IllegalArgumentException) {
        Other
    }
}