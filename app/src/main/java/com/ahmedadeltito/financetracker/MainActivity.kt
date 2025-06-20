package com.ahmedadeltito.financetracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.ahmedadeltito.financetracker.feature.currencyconversion.navigation.currencyConverterScreen
import com.ahmedadeltito.financetracker.feature.currencyconversion.navigation.navigateToCurrencyConverter
import com.ahmedadeltito.financetracker.feature.transactions.navigation.addTransactionScreen
import com.ahmedadeltito.financetracker.feature.transactions.navigation.navigateToAddTransaction
import com.ahmedadeltito.financetracker.feature.transactions.navigation.navigateToEditTransaction
import com.ahmedadeltito.financetracker.feature.transactions.navigation.transactionsScreen
import com.ahmedadeltito.financetracker.feature.transactions.navigation.updateTransactionScreen
import com.ahmedadeltito.financetracker.ui.theme.FinanceTrackerTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FinanceTrackerApp()
        }
    }
}

@Composable
fun FinanceTrackerApp() {
    FinanceTrackerTheme {
        val navController = rememberNavController()
        NavHost(
            navController = navController,
            startDestination = "transactions_route"
        ) {
            transactionsScreen(
                onNavigateToAddTransaction = { navController.navigateToAddTransaction() },
                onNavigateToEditTransaction = { navController.navigateToEditTransaction(it) },
                onNavigateToCurrencyConverter = { navController.navigateToCurrencyConverter() }
            )
            addTransactionScreen(
                onNavigateBack = { navController.popBackStack() }
            )
            updateTransactionScreen(
                onNavigateBack = { navController.popBackStack() }
            )
            currencyConverterScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}