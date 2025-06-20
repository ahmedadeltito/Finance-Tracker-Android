package com.ahmedadeltito.financetracker.feature.currencyconversion.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.ahmedadeltito.financetracker.feature.currencyconversion.ui.CurrencyConverterRoute

private const val currencyConverterRoute = "currency_converter_route"

fun NavController.navigateToCurrencyConverter() {
    this.navigate(currencyConverterRoute)
}

fun NavGraphBuilder.currencyConverterScreen(
    onNavigateBack: () -> Unit
) {
    composable(route = currencyConverterRoute) {
        CurrencyConverterRoute(onNavigateBack)
    }
}

/**
 * Abstraction that the main application can implement to handle *external* navigation requests
 * originating from inside the currency-conversion plugin.
 *
 * The default implementation (provided by the plugin) is a no-op so that integrators can pick
 * and choose which destinations to react to.
 */
interface CurrencyConversionNavigator {
    fun openSettings()
    fun openCurrencySelection(onSelected: (String) -> Unit)

    object Empty : CurrencyConversionNavigator {
        override fun openSettings() = Unit
        override fun openCurrencySelection(onSelected: (String) -> Unit) = Unit
    }
} 