package com.ahmedadeltito.financetracker.domain.repository

import com.ahmedadeltito.financetracker.common.Result
import com.ahmedadeltito.financetracker.domain.entity.Transaction
import com.ahmedadeltito.financetracker.domain.entity.TransactionCategory
import com.ahmedadeltito.financetracker.domain.entity.TransactionType
import kotlinx.coroutines.flow.Flow
import java.math.BigDecimal
import java.util.Date

/**
 * Repository interface for managing transactions.
 * This interface defines the contract that any transaction repository implementation must fulfill.
 */
interface TransactionRepository {
    /**
     * Get all transactions as a Flow, ordered by date descending.
     */
    fun getTransactions(): Flow<Result<List<Transaction>>>

    /**
     * Get transactions for a specific time period.
     */
    fun getTransactionsByDateRange(startDate: Date, endDate: Date): Flow<Result<List<Transaction>>>

    /**
     * Get transactions by type (Income or Expense).
     */
    fun getTransactionsByType(type: TransactionType): Flow<Result<List<Transaction>>>

    /**
     * Get transactions by category.
     */
    fun getTransactionsByCategory(categoryId: String): Flow<Result<List<Transaction>>>

    /**
     * Get a single transaction by ID.
     */
    suspend fun getTransactionById(id: String): Result<Transaction>

    /**
     * Add a new transaction.
     */
    suspend fun addTransaction(transaction: Transaction): Result<Transaction>

    /**
     * Update an existing transaction.
     */
    suspend fun updateTransaction(transaction: Transaction): Result<Transaction>

    /**
     * Delete a transaction.
     */
    suspend fun deleteTransaction(id: String): Result<Unit>

    /**
     * Get total amount for a specific transaction type in a date range.
     */
    fun getTotalAmountByTypeAndDateRange(
        type: TransactionType,
        startDate: Date,
        endDate: Date
    ): Flow<Result<BigDecimal>>

    /**
     * Get all transaction categories.
     */
    fun getTransactionCategories(): Flow<Result<List<TransactionCategory>>>

    /**
     * Add a new custom category.
     */
    suspend fun addTransactionCategory(category: TransactionCategory): Result<TransactionCategory>

    /**
     * Update an existing category.
     */
    suspend fun updateTransactionCategory(category: TransactionCategory): Result<TransactionCategory>

    /**
     * Delete a custom category.
     * Note: Default categories cannot be deleted.
     */
    suspend fun deleteTransactionCategory(id: String): Result<Unit>
} 