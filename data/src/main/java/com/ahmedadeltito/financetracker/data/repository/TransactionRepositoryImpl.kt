package com.ahmedadeltito.financetracker.data.repository

import com.ahmedadeltito.financetracker.common.Result
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
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.math.BigDecimal
import java.util.Date
import javax.inject.Inject

class TransactionRepositoryImpl @Inject constructor(
    private val transactionDao: TransactionDao,
    private val categoryDao: TransactionCategoryDao
) : TransactionRepository {

    override fun getTransactions(): Flow<Result<List<Transaction>>> =
        transactionDao.getAllTransactions()
            .map { transactions -> transactions.map { getTransactionWithCategory(it) } }
            .map { Result.Success(it) }
            .catch { Result.Error(it) }

    override fun getTransactionsByDateRange(
        startDate: Date,
        endDate: Date
    ): Flow<Result<List<Transaction>>> =
        transactionDao.getTransactionsByDateRange(startDate, endDate)
            .map { transactions -> transactions.map { getTransactionWithCategory(it) } }
            .map { Result.Success(it) }
            .catch { Result.Error(it) }

    override fun getTransactionsByType(type: TransactionType): Flow<Result<List<Transaction>>> =
        transactionDao.getTransactionsByType(type)
            .map { transactions -> transactions.map { getTransactionWithCategory(it) } }
            .map { Result.Success(it) }
            .catch { Result.Error(it) }

    override fun getTransactionsByCategory(categoryId: String): Flow<Result<List<Transaction>>> =
        transactionDao.getTransactionsByCategory(categoryId)
            .map { transactions -> transactions.map { getTransactionWithCategory(it) } }
            .map { Result.Success(it) }
            .catch { Result.Error(it) }

    override suspend fun getTransactionById(id: String): Result<Transaction> = try {
        val transaction = transactionDao.getTransactionById(id)
            ?: return Result.Error(NoSuchElementException("Transaction not found"))
        Result.Success(getTransactionWithCategory(transaction))
    } catch (e: Exception) {
        Result.Error(e)
    }

    override suspend fun addTransaction(transaction: Transaction): Result<Transaction> {
        return try {
            val category = categoryDao.getCategoryById(transaction.category.id)
                ?: return Result.Error(IllegalArgumentException("Category not found"))

            if (category.isDeleted) {
                return Result.Error(IllegalArgumentException("Cannot add transaction with deleted category"))
            }

            val entity = transaction.toEntity()
            transactionDao.insertTransaction(entity)
            Result.Success(transaction)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun updateTransaction(transaction: Transaction): Result<Transaction> {
        return try {
            val category = categoryDao.getCategoryById(transaction.category.id)
                ?: return Result.Error(IllegalArgumentException("Category not found"))

            if (category.isDeleted) {
                return Result.Error(IllegalArgumentException("Cannot update transaction with deleted category"))
            }

            val entity = transaction.toEntity()
            transactionDao.updateTransaction(entity)
            Result.Success(transaction)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun deleteTransaction(id: String): Result<Unit> = try {
        val transaction = transactionDao.getTransactionById(id)
            ?: return Result.Error(NoSuchElementException("Transaction not found"))
        transactionDao.deleteTransaction(transaction)
        Result.Success(Unit)
    } catch (e: Exception) {
        Result.Error(e)
    }

    override fun getTotalAmountByTypeAndDateRange(
        type: TransactionType,
        startDate: Date,
        endDate: Date
    ): Flow<Result<BigDecimal>> = transactionDao.getTransactionsByDateRange(startDate, endDate)
        .map { transactions ->
            transactions
                .filter { it.type == type }
                .fold(BigDecimal.ZERO) { acc, transaction -> acc + transaction.amount }
        }
        .map { Result.Success(it) }
        .catch { Result.Error(it) }

    override fun getTransactionCategories(): Flow<Result<List<TransactionCategory>>> =
        categoryDao.getAllCategories()
            .map { categories -> categories.map { it.toDomain() } }
            .map { Result.Success(it) }
            .catch { Result.Error(it) }

    override suspend fun addTransactionCategory(
        category: TransactionCategory
    ): Result<TransactionCategory> = try {
        val entity = category.toEntity()
        categoryDao.insertCategory(entity)
        Result.Success(category)
    } catch (e: Exception) {
        Result.Error(e)
    }

    override suspend fun updateTransactionCategory(
        category: TransactionCategory
    ): Result<TransactionCategory> = try {
        val entity = category.toEntity()
        categoryDao.insertCategory(entity) // Using REPLACE strategy
        Result.Success(category)
    } catch (e: Exception) {
        Result.Error(e)
    }

    override suspend fun deleteTransactionCategory(id: String): Result<Unit> = try {
        val category = categoryDao.getCategoryById(id)
            ?: return Result.Error(NoSuchElementException("Category not found"))

        // Check if it's a default category
        if (TransactionCategory.DEFAULT_CATEGORIES.any { it.id == id }) {
            Result.Error(IllegalArgumentException("Cannot delete default category"))
        }

        // Check if category has transactions
        val transactions = transactionDao.getTransactionsByCategory(id)
            .map { it.size }
            .first()

        if (transactions > 0) {
            Result.Error(IllegalStateException("Cannot delete category with existing transactions"))
        }

        categoryDao.insertCategory(category.copy(isDeleted = true))
        Result.Success(Unit)
    } catch (e: Exception) {
        Result.Error(e)
    }

    private suspend fun getTransactionWithCategory(entity: TransactionEntity): Transaction {
        val category = categoryDao.getCategoryById(entity.categoryId)
            ?: throw NoSuchElementException("Category not found for transaction")
        return entity.toDomain(category.toDomain())
    }
} 