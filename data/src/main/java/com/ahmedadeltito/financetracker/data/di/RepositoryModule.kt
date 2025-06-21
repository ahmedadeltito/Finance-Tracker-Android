package com.ahmedadeltito.financetracker.data.di

import com.ahmedadeltito.financetracker.data.repository.TransactionRepositoryImpl
import com.ahmedadeltito.financetracker.domain.repository.TransactionRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface RepositoryModule {

    @Binds
    @Singleton
    fun bindTransactionRepository(
        repository: TransactionRepositoryImpl
    ): TransactionRepository
}
