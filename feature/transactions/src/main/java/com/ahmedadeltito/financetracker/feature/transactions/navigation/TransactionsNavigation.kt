package com.ahmedadeltito.financetracker.feature.transactions.navigation

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.ahmedadeltito.financetracker.feature.transactions.navigation.TransactionsNavigation.ADD_TRANSACTION_ROUTE
import com.ahmedadeltito.financetracker.feature.transactions.navigation.TransactionsNavigation.TRANSACTION_ID_ARG
import com.ahmedadeltito.financetracker.feature.transactions.navigation.TransactionsNavigation.TRANSACTION_ROUTE
import com.ahmedadeltito.financetracker.feature.transactions.navigation.TransactionsNavigation.UPDATE_TRANSACTION_ROUTE
import com.ahmedadeltito.financetracker.feature.transactions.ui.add.AddTransactionScreen
import com.ahmedadeltito.financetracker.feature.transactions.ui.add.AddTransactionSideEffect
import com.ahmedadeltito.financetracker.feature.transactions.ui.add.AddTransactionViewModel
import com.ahmedadeltito.financetracker.feature.transactions.ui.list.TransactionListEvent
import com.ahmedadeltito.financetracker.feature.transactions.ui.list.TransactionListScreen
import com.ahmedadeltito.financetracker.feature.transactions.ui.list.TransactionListSideEffect
import com.ahmedadeltito.financetracker.feature.transactions.ui.list.TransactionListViewModel
import com.ahmedadeltito.financetracker.feature.transactions.ui.update.UpdateTransactionScreen
import com.ahmedadeltito.financetracker.feature.transactions.ui.update.UpdateTransactionSideEffect
import com.ahmedadeltito.financetracker.feature.transactions.ui.update.UpdateTransactionViewModel
import kotlinx.coroutines.flow.collectLatest

object TransactionsNavigation {
    const val TRANSACTION_ROUTE = "transactions_route"
    internal const val ADD_TRANSACTION_ROUTE = "add_transaction_route"
    internal const val UPDATE_TRANSACTION_ROUTE = "update_transaction_route"
    const val TRANSACTION_ID_ARG = "transactionId"
}

fun NavController.navigateToAddTransaction() {
    this.navigate(ADD_TRANSACTION_ROUTE)
}

fun NavController.navigateToEditTransaction(transactionId: String) {
    this.navigate("$UPDATE_TRANSACTION_ROUTE?$TRANSACTION_ID_ARG=$transactionId")
}

fun NavGraphBuilder.transactionsScreen(
    onNavigateToAddTransaction: () -> Unit,
    onNavigateToEditTransaction: (String) -> Unit,
    onNavigateToCurrencyConverter: () -> Unit
) {
    composable(route = TRANSACTION_ROUTE) {

        val snackbarHostState = remember { SnackbarHostState() }

        val viewModel: TransactionListViewModel = hiltViewModel()
        val uiState by viewModel.state.collectAsStateWithLifecycle()

        LaunchedEffect(key1 = true) {
            viewModel.sideEffect.collect { effect ->
                when (effect) {
                    is TransactionListSideEffect.NavigateToTransactionDetails ->
                        onNavigateToEditTransaction(effect.transactionId)
                    is TransactionListSideEffect.NavigateToAddTransaction ->
                        onNavigateToAddTransaction()
                    is TransactionListSideEffect.ShowUndoSnackbar -> {
                        val actionLabel = if (effect.transactionId != null) "Undo" else null
                        val duration = if (effect.transactionId != null) SnackbarDuration.Long else SnackbarDuration.Short
                        val result = snackbarHostState.showSnackbar(
                            message = effect.message,
                            actionLabel = actionLabel,
                            duration = duration
                        )
                        when (result) {
                            SnackbarResult.Dismissed -> effect.transactionId?.let {
                                viewModel.onEvent(TransactionListEvent.HardDeleteTransaction(it))
                            }
                            SnackbarResult.ActionPerformed -> viewModel.onEvent(TransactionListEvent.UndoDelete)
                        }
                    }
                }
            }
        }

        TransactionListScreen(
            snackbarHostState = snackbarHostState,
            uiState = uiState,
            onEvent = viewModel::onEvent,
            onNavigateToCurrencyConverter = onNavigateToCurrencyConverter
        )
    }
}

fun NavGraphBuilder.addTransactionScreen(
    onNavigateBack: () -> Unit
) {
    composable(route = ADD_TRANSACTION_ROUTE) {
        val snackbarHostState = remember { SnackbarHostState() }

        val viewModel: AddTransactionViewModel = hiltViewModel()
        val uiState = viewModel.state.collectAsStateWithLifecycle()

        LaunchedEffect(key1 = true) {
            viewModel.sideEffect.collectLatest { effect ->
                when (effect) {
                    is AddTransactionSideEffect.NavigateBack -> onNavigateBack()
                    is AddTransactionSideEffect.ShowSnackbar -> snackbarHostState.showSnackbar(effect.message)
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
        route = "$UPDATE_TRANSACTION_ROUTE?$TRANSACTION_ID_ARG={$TRANSACTION_ID_ARG}",
        arguments = listOf(
            navArgument(TRANSACTION_ID_ARG) {
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
                    is UpdateTransactionSideEffect.ShowSnackbar -> snackbarHostState.showSnackbar(effect.message)
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