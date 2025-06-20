package com.ahmedadeltito.financetracker.ui.preview

import com.ahmedadeltito.financetracker.ui.model.TransactionCategoryUiModel
import com.ahmedadeltito.financetracker.ui.model.TransactionTypeUiModel
import com.ahmedadeltito.financetracker.ui.model.TransactionUiModel
import com.ahmedadeltito.financetracker.ui.theme.Bills
import com.ahmedadeltito.financetracker.ui.theme.Entertainment
import com.ahmedadeltito.financetracker.ui.theme.Groceries
import com.ahmedadeltito.financetracker.ui.theme.Health
import com.ahmedadeltito.financetracker.ui.theme.Investment
import com.ahmedadeltito.financetracker.ui.theme.Salary
import com.ahmedadeltito.financetracker.ui.theme.Shopping

object PreviewData {
    val sampleCategories = listOf(
        TransactionCategoryUiModel(
            id = "income_salary",
            name = "Salary",
            type = TransactionTypeUiModel.Income,
            color = Salary,
            icon = "S"
        ),
        TransactionCategoryUiModel(
            id = "income_investment",
            name = "Investment",
            type = TransactionTypeUiModel.Income,
            color = Investment,
            icon = "I"
        ),
        TransactionCategoryUiModel(
            id = "expense_groceries",
            name = "Groceries",
            type = TransactionTypeUiModel.Expense,
            color = Groceries,
            icon = "G"
        ),
        TransactionCategoryUiModel(
            id = "expense_bills",
            name = "Bills",
            type = TransactionTypeUiModel.Expense,
            color = Bills,
            icon = "B"
        ),
        TransactionCategoryUiModel(
            id = "expense_entertainment",
            name = "Entertainment",
            type = TransactionTypeUiModel.Expense,
            color = Entertainment,
            icon = "E"
        ),
        TransactionCategoryUiModel(
            id = "expense_health",
            name = "Health",
            type = TransactionTypeUiModel.Expense,
            color = Health,
            icon = "H"
        ),
        TransactionCategoryUiModel(
            id = "expense_shopping",
            name = "Shopping",
            type = TransactionTypeUiModel.Expense,
            color = Shopping,
            icon = "S"
        )
    )

    val sampleTransactions = listOf(
        TransactionUiModel(
            id = "1",
            amount = "1,500.00",
            formattedDate = "Jan 15, 2024",
            category = sampleCategories[0], // Salary
            note = "Monthly salary",
            type = TransactionTypeUiModel.Income
        ),
        TransactionUiModel(
            id = "2",
            amount = "50.00",
            formattedDate = "Jan 15, 2024",
            category = sampleCategories[2], // Groceries
            note = "Weekly groceries",
            type = TransactionTypeUiModel.Expense
        ),
        TransactionUiModel(
            id = "3",
            amount = "100.00",
            formattedDate = "Jan 14, 2024",
            category = sampleCategories[3], // Bills
            note = "Electricity bill",
            type = TransactionTypeUiModel.Expense
        ),
        TransactionUiModel(
            id = "4",
            amount = "500.00",
            formattedDate = "Jan 13, 2024",
            category = sampleCategories[1], // Investment
            note = "Stock dividends",
            type = TransactionTypeUiModel.Income
        )
    )
} 