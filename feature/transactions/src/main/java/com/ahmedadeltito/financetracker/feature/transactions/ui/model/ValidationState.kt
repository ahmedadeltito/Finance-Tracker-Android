package com.ahmedadeltito.financetracker.feature.transactions.ui.model


data class ValidationState(
    val amountError: String? = null,
    val descriptionError: String? = null,
    val categoryError: String? = null,
    val dateError: String? = null
) {
    val hasErrors = amountError != null || descriptionError != null || categoryError != null || dateError != null
}