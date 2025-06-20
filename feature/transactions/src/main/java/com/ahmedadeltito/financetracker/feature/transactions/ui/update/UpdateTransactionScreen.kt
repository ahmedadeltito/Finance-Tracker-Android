package com.ahmedadeltito.financetracker.feature.transactions.ui.update

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
import com.ahmedadeltito.financetracker.ui.components.TransactionFormContent
import com.ahmedadeltito.financetracker.ui.model.ValidationState
import com.ahmedadeltito.financetracker.feature.transactions.ui.update.UpdateTransactionEvent.OnAmountChange
import com.ahmedadeltito.financetracker.feature.transactions.ui.update.UpdateTransactionEvent.OnBackClick
import com.ahmedadeltito.financetracker.feature.transactions.ui.update.UpdateTransactionEvent.OnCategorySelect
import com.ahmedadeltito.financetracker.feature.transactions.ui.update.UpdateTransactionEvent.OnDateChange
import com.ahmedadeltito.financetracker.feature.transactions.ui.update.UpdateTransactionEvent.OnDescriptionChange
import com.ahmedadeltito.financetracker.feature.transactions.ui.update.UpdateTransactionEvent.OnTypeChange
import com.ahmedadeltito.financetracker.feature.transactions.ui.update.UpdateTransactionEvent.OnUpdateClick
import com.ahmedadeltito.financetracker.feature.transactions.ui.update.UpdateTransactionState.Error
import com.ahmedadeltito.financetracker.feature.transactions.ui.update.UpdateTransactionState.Loading
import com.ahmedadeltito.financetracker.feature.transactions.ui.update.UpdateTransactionState.Success
import com.ahmedadeltito.financetracker.ui.components.ErrorComponent
import com.ahmedadeltito.financetracker.ui.components.LightAndDarkPreview
import com.ahmedadeltito.financetracker.ui.components.LoadingComponent
import com.ahmedadeltito.financetracker.ui.preview.PreviewData
import com.ahmedadeltito.financetracker.ui.theme.FinanceTrackerTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdateTransactionScreen(
    snackbarHostState: SnackbarHostState,
    uiState: UpdateTransactionState,
    onEvent: (UpdateTransactionEvent) -> Unit,
) {
    var showDatePicker by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Update Transaction") },
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
                actionButtonText = "Update",
                onActionClick = { onEvent(OnUpdateClick) }
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
private fun UpdateTransactionScreenLoadingPreview() {
    FinanceTrackerTheme {
        UpdateTransactionScreen(
            snackbarHostState = SnackbarHostState(),
            uiState = Loading,
            onEvent = {}
        )
    }
}

@LightAndDarkPreview
@Composable
private fun UpdateTransactionScreenSuccessPreview() {
    FinanceTrackerTheme {
        UpdateTransactionScreen(
            snackbarHostState = SnackbarHostState(),
            uiState = Success(
                transaction = PreviewData.sampleTransactions[0],
                categories = PreviewData.sampleCategories,
                validation = ValidationState()
            ),
            onEvent = {}
        )
    }
}

@LightAndDarkPreview
@Composable
private fun UpdateTransactionScreenErrorPreview() {
    FinanceTrackerTheme {
        UpdateTransactionScreen(
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
private fun UpdateTransactionScreenValidationErrorPreview() {
    FinanceTrackerTheme {
        UpdateTransactionScreen(
            snackbarHostState = SnackbarHostState(),
            uiState = Success(
                transaction = PreviewData.sampleTransactions[0],
                categories = PreviewData.sampleCategories,
                validation = ValidationState(
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