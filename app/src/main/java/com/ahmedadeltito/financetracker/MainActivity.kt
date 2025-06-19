package com.ahmedadeltito.financetracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import com.ahmedadeltito.financetracker.feature.transactions.ui.TransactionListScreen
import com.ahmedadeltito.financetracker.ui.theme.FinanceTrackerTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FinanceTrackerApp()
        }
    }
}

@Composable
fun FinanceTrackerApp() {
    FinanceTrackerTheme {
        TransactionListScreen(
            onNavigateToTransactionDetails = { /* TODO: Implement navigation */ },
            onNavigateToAddTransaction = { /* TODO: Implement navigation */ }
        )
    }
}