package com.ahmedadeltito.financetracker.feature.currencyconversion.di

import com.ahmedadeltito.financetracker.feature.currencyconversion.data.repository.ExchangeRateRepositoryImpl
import com.ahmedadeltito.financetracker.feature.currencyconversion.domain.repository.ExchangeRateRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface CurrencyConversionRepositoryModule {

    @Binds
    @Singleton
    fun bindExchangeRateRepository(
        repository: ExchangeRateRepositoryImpl
    ): ExchangeRateRepository
}