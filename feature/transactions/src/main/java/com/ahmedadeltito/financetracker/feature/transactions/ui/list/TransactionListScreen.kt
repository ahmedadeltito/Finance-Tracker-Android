package com.ahmedadeltito.financetracker.feature.transactions.ui.list

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ahmedadeltito.financetracker.feature.transactions.ui.list.TransactionListEvent.OnAddTransactionClick
import com.ahmedadeltito.financetracker.feature.transactions.ui.list.TransactionListEvent.OnTransactionClick
import com.ahmedadeltito.financetracker.feature.transactions.ui.list.TransactionListEvent.Refresh
import com.ahmedadeltito.financetracker.feature.transactions.ui.list.TransactionListEvent.SoftDeleteTransaction
import com.ahmedadeltito.financetracker.ui.components.DeleteConfirmationDialog
import com.ahmedadeltito.financetracker.ui.components.ErrorComponent
import com.ahmedadeltito.financetracker.ui.components.TransactionCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionListScreen(
    snackbarHostState: SnackbarHostState,
    uiState: TransactionListState,
    onEvent: (TransactionListEvent) -> Unit
) {
    var swipedTransactionId by rememberSaveable { mutableStateOf<String?>(null) }

    swipedTransactionId?.let { transactionId ->
        DeleteConfirmationDialog(
            onConfirm = {
                onEvent(SoftDeleteTransaction(transactionId))
                swipedTransactionId = null
            },
            onDismiss = {
                swipedTransactionId = null
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Transactions") }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onEvent(OnAddTransactionClick) }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Transaction")
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (uiState) {
                is TransactionListState.Loading -> CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
                is TransactionListState.Success -> {
                    val transactions = uiState.transactions
                    if (transactions.isEmpty()) {
                        Text(
                            text = "No transactions yet",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(
                                items = transactions,
                                key = { it.id }
                            ) { transaction ->
                                val dismissState = rememberSwipeToDismissBoxState(
                                    confirmValueChange = { dismissValue ->
                                        when (dismissValue) {
                                            SwipeToDismissBoxValue.EndToStart -> {
                                                swipedTransactionId = transaction.id
                                                false
                                            }
                                            else -> false
                                        }
                                    }
                                )
                                SwipeToDismissBox(
                                    state = dismissState,
                                    enableDismissFromStartToEnd = false,
                                    enableDismissFromEndToStart = true,
                                    backgroundContent = {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .padding(horizontal = 16.dp, vertical = 8.dp),
                                            contentAlignment = Alignment.CenterEnd
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Delete,
                                                contentDescription = "Delete Transaction",
                                                tint = MaterialTheme.colorScheme.error
                                            )
                                        }
                                    }
                                ) {
                                    TransactionCard(
                                        transaction = transaction,
                                        onClick = { onEvent(OnTransactionClick(transaction.id)) },
                                        modifier = Modifier.padding(
                                            horizontal = 16.dp,
                                            vertical = 8.dp
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
                is TransactionListState.Error -> ErrorComponent(
                    paddingValues = paddingValues,
                    errorMessage = uiState.message,
                    onClick = { onEvent(Refresh) }
                )
            }
        }
    }
} 