package com.ahmedadeltito.financetracker.feature.transactions.currency

import com.ahmedadeltito.financetracker.feature.currencyconversion.domain.port.ExchangeRateProviderPort
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet

@Module
@InstallIn(SingletonComponent::class)
interface ExchangeRateHostProviderModule {
    @Binds
    @IntoSet
    fun exchangeRateHostIntoSet(provider: ExchangeRateHostProvider): ExchangeRateProviderPort
}