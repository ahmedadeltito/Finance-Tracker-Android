package com.ahmedadeltito.financetracker.feature.transactions.currency

import com.ahmedadeltito.financetracker.feature.transactions.currency.remote.ExchangeRateHostService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ExchangeRateHostApi

@Module
@InstallIn(SingletonComponent::class)
object ExchangeRateHostNetworkModule {

    private const val BASE_URL = "https://api.exchangerate.host/"

    @Provides
    @Singleton
    @ExchangeRateHostApi
    fun provideOkHttpClient(): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(
            HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BASIC
            }
        )
        .build()

    @Provides
    @Singleton
    @ExchangeRateHostApi
    fun provideRetrofit(client: OkHttpClient): Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    @Provides
    @Singleton
    fun provideExchangeRateHostService(@ExchangeRateHostApi retrofit: Retrofit): ExchangeRateHostService =
        retrofit.create(ExchangeRateHostService::class.java)
}