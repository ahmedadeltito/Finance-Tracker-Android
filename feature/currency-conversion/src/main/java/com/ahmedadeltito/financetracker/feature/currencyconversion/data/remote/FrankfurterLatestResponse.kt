package com.ahmedadeltito.financetracker.feature.currencyconversion.data.remote

import com.google.gson.annotations.SerializedName

data class FrankfurterLatestResponse(
    val amount: Double,
    val base: String,
    val date: String,
    @SerializedName("rates") val rates: Map<String, Double>
) 