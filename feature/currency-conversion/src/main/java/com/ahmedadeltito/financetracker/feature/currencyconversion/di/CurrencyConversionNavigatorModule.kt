package com.ahmedadeltito.financetracker.feature.currencyconversion.di

import com.ahmedadeltito.financetracker.feature.currencyconversion.navigation.CurrencyConversionNavigator
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CurrencyConversionNavigatorModule {
    @Provides
    @Singleton
    fun provideNavigator(): CurrencyConversionNavigator = CurrencyConversionNavigator.Empty
} 