package com.ahmedadeltito.financetracker.feature.currencyconversion.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.ahmedadeltito.financetracker.feature.currencyconversion.ui.CurrencyConverterRoute

private const val currencyConverterRoute = "currency_converter_route"

fun NavGraphBuilder.currencyConverterScreen(
    onNavigateBack: () -> Unit = {}
) {
    composable(route = currencyConverterRoute) {
        CurrencyConverterRoute(onNavigateBack)
    }
}

interface CurrencyConversionNavigator {
    fun navigateToCurrencyConverter(navController: NavController)

    object Default : CurrencyConversionNavigator {
        override fun navigateToCurrencyConverter(navController: NavController) {
            navController.navigate(currencyConverterRoute)
        }
    }
} 