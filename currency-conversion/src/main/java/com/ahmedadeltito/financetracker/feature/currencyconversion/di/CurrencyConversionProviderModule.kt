package com.ahmedadeltito.financetracker.feature.currencyconversion.di

import com.ahmedadeltito.financetracker.feature.currencyconversion.data.remote.FrankfurterExchangeRateProvider
import com.ahmedadeltito.financetracker.feature.currencyconversion.domain.port.ExchangeRateProviderPort
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet

@Module
@InstallIn(SingletonComponent::class)
interface CurrencyConversionProviderModule {
    @Binds
    @IntoSet
    fun frankfurterProviderIntoSet(provider: FrankfurterExchangeRateProvider): ExchangeRateProviderPort
}