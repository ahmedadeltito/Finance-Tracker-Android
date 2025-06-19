package com.ahmedadeltito.financetracker.domain.entity

import java.math.BigDecimal
import java.util.Date

/**
 * Represents a financial transaction in the system.
 */
data class Transaction(
    val id: String,
    val type: TransactionType,
    val amount: BigDecimal,
    val currency: String,
    val category: TransactionCategory,
    val date: Date,
    val notes: String? = null,
    val createdAt: Date = Date(),
    val updatedAt: Date = Date()
)

/**
 * Represents the type of transaction.
 */
enum class TransactionType {
    INCOME,
    EXPENSE
}

/**
 * Represents the category of a transaction.
 * This can be extended based on user preferences.
 */
data class TransactionCategory(
    val id: String,
    val name: String,
    val type: TransactionType,
    val iconUrl: String? = null,
    val color: String? = null
) {
    companion object {
        // Predefined categories for income
        val SALARY = TransactionCategory("income_salary", "Salary", TransactionType.INCOME)
        val INVESTMENT = TransactionCategory("income_investment", "Investment", TransactionType.INCOME)
        val BONUS = TransactionCategory("income_bonus", "Bonus", TransactionType.INCOME)
        val OTHER_INCOME = TransactionCategory("income_other", "Other", TransactionType.INCOME)

        // Predefined categories for expenses
        val GROCERIES = TransactionCategory("expense_groceries", "Groceries", TransactionType.EXPENSE)
        val BILLS = TransactionCategory("expense_bills", "Bills", TransactionType.EXPENSE)
        val ENTERTAINMENT = TransactionCategory("expense_entertainment", "Entertainment", TransactionType.EXPENSE)
        val TRAVEL = TransactionCategory("expense_travel", "Travel", TransactionType.EXPENSE)
        val SHOPPING = TransactionCategory("expense_shopping", "Shopping", TransactionType.EXPENSE)
        val HEALTH = TransactionCategory("expense_health", "Health", TransactionType.EXPENSE)
        val TRANSPORT = TransactionCategory("expense_transport", "Transport", TransactionType.EXPENSE)
        val OTHER_EXPENSE = TransactionCategory("expense_other", "Other", TransactionType.EXPENSE)

        // Default categories list
        val DEFAULT_CATEGORIES = listOf(
            SALARY, INVESTMENT, BONUS, OTHER_INCOME,
            GROCERIES, BILLS, ENTERTAINMENT, TRAVEL, SHOPPING, HEALTH, TRANSPORT, OTHER_EXPENSE
        )
    }
} 