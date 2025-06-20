package com.ahmedadeltito.financetracker.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ahmedadeltito.financetracker.ui.model.TransactionCategoryUiModel
import com.ahmedadeltito.financetracker.ui.model.TransactionTypeUiModel
import com.ahmedadeltito.financetracker.ui.model.TransactionUiModel
import com.ahmedadeltito.financetracker.ui.model.ValidationState
import java.util.Date

/**
 * A reusable set of form fields for adding/updating a transaction.
 */
@Composable
fun TransactionFormContent(
    modifier: Modifier = Modifier,
    transaction: TransactionUiModel,
    categories: List<TransactionCategoryUiModel>,
    validation: ValidationState,
    showDatePicker: Boolean,
    onAmountChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onDateChange: (Date) -> Unit,
    onTypeChange: (TransactionTypeUiModel) -> Unit,
    onCategorySelect: (String) -> Unit,
    onDismissDatePicker: () -> Unit,
    onDatePickerIconClick: () -> Unit,
    actionButtonText: String,
    onActionClick: () -> Unit
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        TransactionTypeSelectorComponent(
            type = transaction.type,
            onTypeChange = onTypeChange
        )
        // Amount Field
        TransactionTextFieldComponent(
            value = transaction.amount,
            label = "Amount",
            supportingText = validation.amountError,
            onValueChange = onAmountChange
        )
        // Description Field
        TransactionTextFieldComponent(
            value = transaction.note ?: "",
            label = "Description",
            supportingText = validation.descriptionError,
            onValueChange = onDescriptionChange
        )
        // Date Field
        TransactionTextFieldComponent(
            value = transaction.formattedDate,
            label = "Date",
            supportingText = validation.dateError,
            readOnly = true,
            trailingIcon = {
                IconButton(onClick = onDatePickerIconClick) {
                    Icon(imageVector = Icons.Default.DateRange, contentDescription = "Select Date")
                }
            }
        )
        // Category Selector
        TransactionCategorySelector(
            categories = categories,
            selectedCategoryId = transaction.category.id,
            onCategorySelected = onCategorySelect,
            error = validation.categoryError
        )
        // Primary Button
        PrimaryButton(
            onClick = onActionClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(actionButtonText)
        }
    }

    if (showDatePicker) {
        DatePickerComponent(
            onDateChange = onDateChange,
            onDismiss = onDismissDatePicker
        )
    }
} 