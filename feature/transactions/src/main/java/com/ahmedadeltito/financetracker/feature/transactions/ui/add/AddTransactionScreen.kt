package com.ahmedadeltito.financetracker.feature.transactions.ui.add

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.ahmedadeltito.financetracker.feature.transactions.ui.add.AddTransactionEvent.OnAmountChange
import com.ahmedadeltito.financetracker.feature.transactions.ui.add.AddTransactionEvent.OnBackClick
import com.ahmedadeltito.financetracker.feature.transactions.ui.add.AddTransactionEvent.OnCategorySelect
import com.ahmedadeltito.financetracker.feature.transactions.ui.add.AddTransactionEvent.OnDateChange
import com.ahmedadeltito.financetracker.feature.transactions.ui.add.AddTransactionEvent.OnDescriptionChange
import com.ahmedadeltito.financetracker.feature.transactions.ui.add.AddTransactionEvent.OnSaveClick
import com.ahmedadeltito.financetracker.feature.transactions.ui.add.AddTransactionEvent.OnTypeChange
import com.ahmedadeltito.financetracker.feature.transactions.ui.add.AddTransactionUiState.Error
import com.ahmedadeltito.financetracker.feature.transactions.ui.add.AddTransactionUiState.Loading
import com.ahmedadeltito.financetracker.feature.transactions.ui.add.AddTransactionUiState.Success
import com.ahmedadeltito.financetracker.ui.components.TransactionFormContent
import com.ahmedadeltito.financetracker.ui.model.TransactionFormValidationState
import com.ahmedadeltito.financetracker.ui.components.ErrorComponent
import com.ahmedadeltito.financetracker.ui.components.LightAndDarkPreview
import com.ahmedadeltito.financetracker.ui.components.LoadingComponent
import com.ahmedadeltito.financetracker.ui.preview.PreviewData
import com.ahmedadeltito.financetracker.ui.theme.FinanceTrackerTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionScreen(
    snackbarHostState: SnackbarHostState,
    uiState: AddTransactionUiState,
    onEvent: (AddTransactionEvent) -> Unit
) {
    var showDatePicker by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Transaction") },
                navigationIcon = {
                    IconButton(onClick = { onEvent(OnBackClick) }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        when (uiState) {
            is Loading -> LoadingComponent(paddingValues = paddingValues)
            is Success -> TransactionFormContent(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                transaction = uiState.transaction,
                categories = uiState.categories,
                validation = uiState.validation,
                showDatePicker = showDatePicker,
                onAmountChange = { onEvent(OnAmountChange(it)) },
                onDescriptionChange = { onEvent(OnDescriptionChange(it)) },
                onDateChange = { onEvent(OnDateChange(it)) },
                onTypeChange = { onEvent(OnTypeChange(it)) },
                onCategorySelect = { onEvent(OnCategorySelect(it)) },
                onDismissDatePicker = { showDatePicker = false },
                onDatePickerIconClick = { showDatePicker = true },
                actionButtonText = "Save",
                onActionClick = { onEvent(OnSaveClick) }
            )
            is Error -> ErrorComponent(
                errorMessage = uiState.message,
                paddingValues = paddingValues,
                onClick = { onEvent(OnBackClick) }
            )
        }
    }
}

@LightAndDarkPreview
@Composable
private fun AddTransactionScreenLoadingPreview() {
    FinanceTrackerTheme {
        AddTransactionScreen(
            snackbarHostState = SnackbarHostState(),
            uiState = Loading,
            onEvent = {}
        )
    }
}

@LightAndDarkPreview
@Composable
private fun AddTransactionScreenSuccessPreview() {
    FinanceTrackerTheme {
        AddTransactionScreen(
            snackbarHostState = SnackbarHostState(),
            uiState = Success(
                transaction = PreviewData.sampleTransactions[0],
                categories = PreviewData.sampleCategories,
                validation = TransactionFormValidationState()
            ),
            onEvent = {}
        )
    }
}

@LightAndDarkPreview
@Composable
private fun AddTransactionScreenErrorPreview() {
    FinanceTrackerTheme {
        AddTransactionScreen(
            snackbarHostState = SnackbarHostState(),
            uiState = Error(
                message = "Failed to load transaction data"
            ),
            onEvent = {}
        )
    }
}

@LightAndDarkPreview
@Composable
private fun AddTransactionScreenValidationErrorPreview() {
    FinanceTrackerTheme {
        AddTransactionScreen(
            snackbarHostState = SnackbarHostState(),
            uiState = Success(
                transaction = PreviewData.sampleTransactions[0],
                categories = PreviewData.sampleCategories,
                validation = TransactionFormValidationState(
                    amountError = "Amount is required",
                    descriptionError = "Description is too long",
                    categoryError = "Please select a category",
                    dateError = "Please select a date"
                )
            ),
            onEvent = {}
        )
    }
}