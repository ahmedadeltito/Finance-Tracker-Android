package com.ahmedadeltito.financetracker.data.local

import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.ahmedadeltito.financetracker.data.local.entity.TransactionCategoryEntity.Companion.toEntity
import com.ahmedadeltito.financetracker.domain.entity.TransactionCategory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Provider

class DatabaseCallback(
    private val database: Provider<FinanceTrackerDatabase>
) : RoomDatabase.Callback() {

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)
        applicationScope.launch {
            populateDefaultCategories()
        }
    }

    private suspend fun populateDefaultCategories() {
        val categoryDao = database.get().transactionCategoryDao()
        TransactionCategory.DEFAULT_CATEGORIES.forEach { category ->
            categoryDao.insertCategory(category.toEntity())
        }
    }
}
