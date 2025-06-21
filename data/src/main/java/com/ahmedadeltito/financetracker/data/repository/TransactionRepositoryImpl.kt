package com.ahmedadeltito.financetracker.data.repository

import com.ahmedadeltito.financetracker.data.local.TransactionCategoryDao
import com.ahmedadeltito.financetracker.data.local.TransactionDao
import com.ahmedadeltito.financetracker.data.local.entity.TransactionCategoryEntity.Companion.toDomain
import com.ahmedadeltito.financetracker.data.local.entity.TransactionCategoryEntity.Companion.toEntity
import com.ahmedadeltito.financetracker.data.local.entity.TransactionEntity
import com.ahmedadeltito.financetracker.data.local.entity.TransactionEntity.Companion.toDomain
import com.ahmedadeltito.financetracker.data.local.entity.TransactionEntity.Companion.toEntity
import com.ahmedadeltito.financetracker.domain.entity.Transaction
import com.ahmedadeltito.financetracker.domain.entity.TransactionCategory
import com.ahmedadeltito.financetracker.domain.entity.TransactionType
import com.ahmedadeltito.financetracker.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.math.BigDecimal
import java.util.Date
import javax.inject.Inject

class TransactionRepositoryImpl @Inject constructor(
    private val transactionDao: TransactionDao,
    private val categoryDao: TransactionCategoryDao
) : TransactionRepository {

    override fun getTransactions(): Flow<List<Transaction>> =
        transactionDao.getAllTransactions()
            .map { transactions -> transactions.map { getTransactionWithCategory(it) } }

    override fun getTransactionsByDateRange(
        startDate: Date,
        endDate: Date
    ): Flow<List<Transaction>> = transactionDao.getTransactionsByDateRange(startDate, endDate)
        .map { transactions -> transactions.map { getTransactionWithCategory(it) } }

    override fun getTransactionsByType(
        type: TransactionType
    ): Flow<List<Transaction>> = transactionDao.getTransactionsByType(type)
        .map { transactions -> transactions.map { getTransactionWithCategory(it) } }

    override fun getTransactionsByCategory(
        categoryId: String
    ): Flow<List<Transaction>> = transactionDao.getTransactionsByCategory(categoryId)
        .map { transactions -> transactions.map { getTransactionWithCategory(it) } }

    override suspend fun getTransactionById(id: String): Transaction {
        val transaction = transactionDao.getTransactionById(id)
            ?: throw NoSuchElementException("Transaction not found")
        return getTransactionWithCategory(transaction)
    }

    override suspend fun addTransaction(transaction: Transaction): Transaction {
        try {
            val category = categoryDao.getCategoryById(transaction.category.id)
                ?: throw IllegalArgumentException("Category not found")

            if (category.isDeleted) {
                throw IllegalArgumentException("Cannot add transaction with deleted category")
            }

            val entity = transaction.toEntity()
            transactionDao.insertTransaction(entity)
            return transaction
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun updateTransaction(transaction: Transaction): Transaction {
        try {
            val category = categoryDao.getCategoryById(transaction.category.id)
                ?: throw IllegalArgumentException("Category not found")

            if (category.isDeleted) {
                throw IllegalArgumentException("Cannot update transaction with deleted category")
            }

            val entity = transaction.toEntity()
            transactionDao.updateTransaction(entity)
            return transaction
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun deleteTransaction(id: String) {
        val transaction = transactionDao.getTransactionById(id)
            ?: throw NoSuchElementException("Transaction not found")
        transactionDao.deleteTransaction(transaction)
    }

    override fun getTotalAmountByTypeAndDateRange(
        type: TransactionType,
        startDate: Date,
        endDate: Date
    ): Flow<BigDecimal> = transactionDao.getTransactionsByDateRange(startDate, endDate)
        .map { transactions ->
            transactions
                .filter { it.type == type }
                .fold(BigDecimal.ZERO) { acc, transaction -> acc + transaction.amount }
        }

    override fun getTransactionCategories(): Flow<List<TransactionCategory>> =
        categoryDao.getAllCategories()
            .map { categories -> categories.map { it.toDomain() } }

    override suspend fun addTransactionCategory(
        category: TransactionCategory
    ): TransactionCategory {
        val entity = category.toEntity()
        categoryDao.insertCategory(entity)
        return category
    }

    override suspend fun updateTransactionCategory(
        category: TransactionCategory
    ): TransactionCategory {
        val entity = category.toEntity()
        categoryDao.insertCategory(entity) // Using REPLACE strategy
        return category
    }

    override suspend fun deleteTransactionCategory(id: String) {
        val category = categoryDao.getCategoryById(id)
            ?: throw NoSuchElementException("Category not found")

        // Check if it's a default category
        if (TransactionCategory.DEFAULT_CATEGORIES.any { it.id == id }) {
            throw IllegalArgumentException("Cannot delete default category")
        }

        // Check if category has transactions
        val transactions = transactionDao.getTransactionsByCategory(id)
            .map { it.size }
            .first()

        if (transactions > 0) {
            throw IllegalStateException("Cannot delete category with existing transactions")
        }

        categoryDao.insertCategory(category.copy(isDeleted = true))
    }

    private suspend fun getTransactionWithCategory(entity: TransactionEntity): Transaction {
        val category = categoryDao.getCategoryById(entity.categoryId)
            ?: throw NoSuchElementException("Category not found for transaction")
        return entity.toDomain(category.toDomain())
    }
} 