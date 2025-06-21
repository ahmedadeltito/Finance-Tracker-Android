package com.ahmedadeltito.financetracker.feature.transactions.common

import com.ahmedadeltito.financetracker.ui.model.TransactionFormValidationState
import com.ahmedadeltito.financetracker.ui.model.TransactionUiModel
import java.math.BigDecimal

/**
 * Shared validation helper used by both Add and Update Transaction forms.
 */
object TransactionFormValidator {

    fun validateAmount(amount: String): String? = when {
        amount.isBlank() -> "Amount is required"
        amount.toBigDecimalOrNull() == null -> "Invalid amount"
        amount.toBigDecimal() <= BigDecimal.ZERO -> "Amount must be greater than zero"
        else -> null
    }

    fun validateDescription(description: String): String? = when {
        description.isBlank() -> "Description is required"
        description.length < 3 -> "Description must be at least 3 characters"
        else -> null
    }

    fun validateCategory(categoryId: String?): String? = when {
        categoryId == null || categoryId.isBlank() -> "Category is required"
        else -> null
    }

    fun validateDate(formattedDate: String?): String? = when {
        formattedDate == null || formattedDate.isBlank() -> "Date is required"
        else -> null
    }

    fun validateForm(formData: TransactionUiModel): TransactionFormValidationState = TransactionFormValidationState(
        amountError = validateAmount(formData.amount),
        descriptionError = validateDescription(formData.note ?: ""),
        categoryError = validateCategory(formData.category.id),
        dateError = validateDate(formData.formattedDate)
    )
} 