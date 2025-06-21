package com.ahmedadeltito.financetracker.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.ahmedadeltito.financetracker.data.local.converter.RoomTypeConverters
import com.ahmedadeltito.financetracker.data.local.entity.TransactionCategoryEntity
import com.ahmedadeltito.financetracker.data.local.entity.TransactionEntity

@Database(
    entities = [
        TransactionEntity::class,
        TransactionCategoryEntity::class
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(RoomTypeConverters::class)
abstract class FinanceTrackerDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao
    abstract fun transactionCategoryDao(): TransactionCategoryDao

    companion object {
        const val DATABASE_NAME = "finance_tracker.db"
    }
} 