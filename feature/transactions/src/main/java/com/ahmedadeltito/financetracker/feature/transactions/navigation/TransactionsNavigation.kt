package com.ahmedadeltito.financetracker.feature.transactions.navigation

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.ahmedadeltito.financetracker.feature.transactions.ui.add.AddTransactionScreen
import com.ahmedadeltito.financetracker.feature.transactions.ui.add.AddTransactionSideEffect
import com.ahmedadeltito.financetracker.feature.transactions.ui.add.AddTransactionViewModel
import com.ahmedadeltito.financetracker.feature.transactions.ui.list.TransactionListScreen
import com.ahmedadeltito.financetracker.feature.transactions.ui.update.UpdateTransactionScreen
import com.ahmedadeltito.financetracker.feature.transactions.ui.update.UpdateTransactionSideEffect
import com.ahmedadeltito.financetracker.feature.transactions.ui.update.UpdateTransactionViewModel
import kotlinx.coroutines.flow.collectLatest

private const val transactionsRoute = "transactions_route"
private const val addTransactionRoute = "add_transaction_route"
private const val updateTransactionRoute = "update_transaction_route"
const val transactionIdArg = "transactionId"

fun NavController.navigateToAddTransaction() {
    this.navigate(addTransactionRoute)
}

fun NavController.navigateToEditTransaction(transactionId: String) {
    this.navigate("$updateTransactionRoute?$transactionIdArg=$transactionId")
}

fun NavGraphBuilder.transactionsScreen(
    onNavigateToAddTransaction: () -> Unit,
    onNavigateToEditTransaction: (String) -> Unit
) {
    composable(route = transactionsRoute) {
        TransactionListScreen(
            onNavigateToAddTransaction = onNavigateToAddTransaction,
            onNavigateToTransactionDetails = onNavigateToEditTransaction
        )
    }
}

fun NavGraphBuilder.addTransactionScreen(
    onNavigateBack: () -> Unit
) {
    composable(
        route = addTransactionRoute,
        arguments = listOf(
            navArgument(transactionIdArg) {
                type = NavType.StringType
                nullable = true
                defaultValue = null
            }
        )
    ) {
        val snackbarHostState = remember { SnackbarHostState() }

        val viewModel: AddTransactionViewModel = hiltViewModel()
        val uiState = viewModel.state.collectAsStateWithLifecycle()

        LaunchedEffect(key1 = true) {
            viewModel.sideEffect.collectLatest { effect ->
                when (effect) {
                    is AddTransactionSideEffect.NavigateBack -> onNavigateBack()
                    is AddTransactionSideEffect.ShowSuccess -> snackbarHostState.showSnackbar(effect.message)
                }
            }
        }

        AddTransactionScreen(
            snackbarHostState = snackbarHostState,
            uiState = uiState.value,
            onEvent = viewModel::onEvent
        )
    }
}

fun NavGraphBuilder.updateTransactionScreen(
    onNavigateBack: () -> Unit
) {
    composable(
        route = "$updateTransactionRoute?$transactionIdArg={$transactionIdArg}",
        arguments = listOf(
            navArgument(transactionIdArg) {
                type = NavType.StringType
                nullable = true
                defaultValue = null
            }
        )
    ) {
        val snackbarHostState = remember { SnackbarHostState() }

        val viewModel: UpdateTransactionViewModel = hiltViewModel()
        val uiState = viewModel.state.collectAsStateWithLifecycle()

        LaunchedEffect(key1 = true) {
            viewModel.sideEffect.collectLatest { effect ->
                when (effect) {
                    is UpdateTransactionSideEffect.NavigateBack -> onNavigateBack()
                    is UpdateTransactionSideEffect.ShowSuccess -> snackbarHostState.showSnackbar(effect.message)
                }
            }
        }

        UpdateTransactionScreen(
            snackbarHostState = snackbarHostState,
            uiState = uiState.value,
            onEvent = viewModel::onEvent
        )
    }
}