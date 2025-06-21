package com.ahmedadeltito.financetracker.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ahmedadeltito.financetracker.data.local.entity.TransactionCategoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionCategoryDao {
    @Query("SELECT * FROM transaction_categories WHERE isDeleted = 0 ORDER BY name ASC")
    fun getAllCategories(): Flow<List<TransactionCategoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: TransactionCategoryEntity): Long

    @Query("SELECT * FROM transaction_categories WHERE id = :id AND isDeleted = 0")
    suspend fun getCategoryById(id: String): TransactionCategoryEntity?
} 