package com.ahmedadeltito.financetracker.feature.transactions.mapper

import com.ahmedadeltito.financetracker.domain.entity.Transaction
import com.ahmedadeltito.financetracker.domain.entity.TransactionCategory
import com.ahmedadeltito.financetracker.domain.entity.TransactionType
import com.ahmedadeltito.financetracker.ui.model.TransactionTypeUiModel
import java.math.BigDecimal
import java.text.NumberFormat
import java.util.Date
import java.util.Locale

object TransactionFormMapper {
    fun formatAmount(amount: BigDecimal): String {
        val currencyFormatter = NumberFormat.getInstance(Locale.getDefault()).apply {
            maximumFractionDigits = 2
            minimumFractionDigits = 2
        }
        return currencyFormatter.format(amount.abs()).let { formattedAmount ->
            "$formattedAmount"
        }
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
