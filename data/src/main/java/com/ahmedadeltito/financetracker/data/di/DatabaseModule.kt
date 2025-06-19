package com.ahmedadeltito.financetracker.data.di

import android.content.Context
import androidx.room.Room
import com.ahmedadeltito.financetracker.data.local.DatabaseCallback
import com.ahmedadeltito.financetracker.data.local.FinanceTrackerDatabase
import com.ahmedadeltito.financetracker.data.local.TransactionCategoryDao
import com.ahmedadeltito.financetracker.data.local.TransactionDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Provider
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context,
        provider: Provider<FinanceTrackerDatabase>
    ): FinanceTrackerDatabase = Room.databaseBuilder(
        context,
        FinanceTrackerDatabase::class.java,
        FinanceTrackerDatabase.DATABASE_NAME
    ).addCallback(DatabaseCallback(provider))
        .build()

    @Provides
    fun provideTransactionDao(
        database: FinanceTrackerDatabase
    ): TransactionDao = database.transactionDao()

    @Provides
    fun provideTransactionCategoryDao(
        database: FinanceTrackerDatabase
    ): TransactionCategoryDao = database.transactionCategoryDao()
}
