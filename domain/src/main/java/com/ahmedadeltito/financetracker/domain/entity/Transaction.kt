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
        val SALARY = TransactionCategory(
            id = "income_salary",
            name = "Salary",
            type = TransactionType.INCOME,
            color = "#1E88E5" // Blue
        )
        val INVESTMENT = TransactionCategory(
            id = "income_investment",
            name = "Investment",
            type = TransactionType.INCOME,
            color = "#7CB342" // Light Green
        )
        val BONUS = TransactionCategory(
            id = "income_bonus",
            name = "Bonus",
            type = TransactionType.INCOME,
            color = "#FFB300" // Amber
        )

        // Predefined categories for expenses
        val GROCERIES = TransactionCategory(
            id = "expense_groceries",
            name = "Groceries",
            type = TransactionType.EXPENSE,
            color = "#E53935" // Red
        )
        val BILLS = TransactionCategory(
            id = "expense_bills",
            name = "Bills",
            type = TransactionType.EXPENSE,
            color = "#5E35B1" // Deep Purple
        )
        val ENTERTAINMENT = TransactionCategory(
            id = "expense_entertainment",
            name = "Entertainment",
            type = TransactionType.EXPENSE,
            color = "#FF7043" // Deep Orange
        )
        val TRAVEL = TransactionCategory(
            id = "expense_travel",
            name = "Travel",
            type = TransactionType.EXPENSE,
            color = "#039BE5" // Light Blue
        )
        val SHOPPING = TransactionCategory(
            id = "expense_shopping",
            name = "Shopping",
            type = TransactionType.EXPENSE,
            color = "#EC407A" // Pink
        )
        val HEALTH = TransactionCategory(
            id = "expense_health",
            name = "Health",
            type = TransactionType.EXPENSE,
            color = "#00ACC1" // Cyan
        )
        val TRANSPORT = TransactionCategory(
            id = "expense_transport",
            name = "Transport",
            type = TransactionType.EXPENSE,
            color = "#43A047" // Green
        )
        val OTHER = TransactionCategory(
            id = "other",
            name = "Other",
            type = TransactionType.INCOME,
            color = "#757575" // Grey
        )

        // Default categories list
        val DEFAULT_CATEGORIES = listOf(
            SALARY,
            INVESTMENT,
            BONUS,
            OTHER,
            GROCERIES,
            BILLS,
            ENTERTAINMENT,
            TRAVEL,
            SHOPPING,
            HEALTH,
            TRANSPORT
        )
    }
} 