package com.ahmedadeltito.financetracker.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.ahmedadeltito.financetracker.feature.currencyconversion.navigation.CurrencyConversionNavigator
import com.ahmedadeltito.financetracker.feature.currencyconversion.navigation.currencyConverterScreen
import com.ahmedadeltito.financetracker.feature.transactions.navigation.addTransactionScreen
import com.ahmedadeltito.financetracker.feature.transactions.navigation.navigateToAddTransaction
import com.ahmedadeltito.financetracker.feature.transactions.navigation.navigateToEditTransaction
import com.ahmedadeltito.financetracker.feature.transactions.navigation.TransactionsNavigation.TRANSACTION_ROUTE
import com.ahmedadeltito.financetracker.feature.transactions.navigation.transactionsScreen
import com.ahmedadeltito.financetracker.feature.transactions.navigation.updateTransactionScreen

/**
 * Centralised navigation graph combining all feature graphs.
 * Host apps simply call [FinanceTrackerNavHost] instead of building the graph
 * manually, keeping navigation modular.
 */
@Composable
fun FinanceTrackerNavHost(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = TRANSACTION_ROUTE
    ) {
        financeTrackerNavGraph(
            navController = navController,
            currencyConversionNavigator = CurrencyConversionNavigator.Default
        )
    }
}

private fun NavGraphBuilder.financeTrackerNavGraph(
    navController: NavHostController,
    currencyConversionNavigator: CurrencyConversionNavigator
) {
    transactionsScreen(
        onNavigateToAddTransaction = { navController.navigateToAddTransaction() },
        onNavigateToEditTransaction = { navController.navigateToEditTransaction(it) },
        onNavigateToCurrencyConverter = {
            currencyConversionNavigator.navigateToCurrencyConverter(navController)
        }
    )
    addTransactionScreen(onNavigateBack = { navController.popBackStack() })
    updateTransactionScreen(onNavigateBack = { navController.popBackStack() })
    currencyConverterScreen(onNavigateBack = { navController.popBackStack() })
} 