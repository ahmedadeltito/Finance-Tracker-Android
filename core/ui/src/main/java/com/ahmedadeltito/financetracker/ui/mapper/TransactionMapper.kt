package com.ahmedadeltito.financetracker.ui.mapper

import androidx.compose.ui.graphics.Color
import androidx.core.graphics.toColorInt
import androidx.core.net.ParseException
import com.ahmedadeltito.financetracker.domain.entity.Transaction
import com.ahmedadeltito.financetracker.domain.entity.TransactionCategory
import com.ahmedadeltito.financetracker.domain.entity.TransactionType
import com.ahmedadeltito.financetracker.ui.mapper.TransactionTypeMapper.toUiModel
import com.ahmedadeltito.financetracker.ui.model.TransactionCategoryUiModel
import com.ahmedadeltito.financetracker.ui.model.TransactionTypeUiModel
import com.ahmedadeltito.financetracker.ui.model.TransactionUiModel
import com.ahmedadeltito.financetracker.ui.theme.Other
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Locale
import java.math.BigDecimal
import java.util.Date

object TransactionMapper {
    fun Transaction.toUiModel(): TransactionUiModel {
        val transactionType = TransactionTypeMapper.run { type.toUiModel() }
        return TransactionUiModel(
            id = id,
            amount = formatAmount(amount, transactionType),
            formattedDate = formatDate(date),
            category = category.toUiModel(),
            note = notes.orEmpty(),
            type = transactionType,
            isSoftDeleted = false
        )
    }

    fun formatDate(date: Date): String {
        val dateFormatter = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        return dateFormatter.format(date)
    }

    fun parseDateString(dateString: String): Date {
        val dateFormatter = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        return try {
            dateFormatter.parse(dateString)
        } catch (e: ParseException) {
            println("Error parsing date string: '$dateString'. ${e.message}")
            Date()
        }
    }

    fun TransactionCategory.toUiModel(): TransactionCategoryUiModel {
        return TransactionCategoryUiModel(
            id = id,
            name = name,
            type = type.toUiModel(),
            color = parseColor(color),
            icon = iconUrl ?: name.first().toString()
        )
    }

    private fun formatAmount(amount: BigDecimal, type: TransactionTypeUiModel): String {
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

    fun fromFormData(
        id: String,
        amount: BigDecimal,
        description: String,
        date: Date,
        type: TransactionTypeUiModel,
        categoryId: String,
        currency: String = "USD"
    ): Transaction {
        val type = when (type) {
            TransactionTypeUiModel.Income -> TransactionType.INCOME
            TransactionTypeUiModel.Expense -> TransactionType.EXPENSE
        }
        return Transaction(
            id = id,
            type = type,
            amount = amount,
            currency = currency,
            category = TransactionCategory(
                id = categoryId,
                name = "", // This will be filled by the repository
                type = type
            ),
            date = date,
            notes = description
        )
    }
}