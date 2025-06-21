package com.ahmedadeltito.financetracker.feature.currencyconversion.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ahmedadeltito.financetracker.feature.currencyconversion.ui.CurrencyConverterEvent.OnBackClick
import com.ahmedadeltito.financetracker.ui.components.PrimaryButton
import com.ahmedadeltito.financetracker.ui.components.TransactionAlertDialog
import com.ahmedadeltito.financetracker.ui.components.TransactionTextFieldComponent
import kotlinx.coroutines.flow.collectLatest

@Composable
fun CurrencyConverterRoute(
    onNavigateBack: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }

    val viewModel: CurrencyConverterViewModel = hiltViewModel()
    val uiState by viewModel.state.collectAsStateWithLifecycle()

    var conversionResult by rememberSaveable { mutableStateOf<String?>(null) }
    conversionResult?.let { conversionMessage ->
        TransactionAlertDialog(
            title = "Currency Conversion Result",
            message = conversionMessage,
            confirmButtonText = "OK",
            onConfirm = { conversionResult = null },
            onDismiss = { conversionResult = null }
        )
    }

    LaunchedEffect(key1 = true) {
        viewModel.sideEffect.collectLatest { effect ->
            when (effect) {
                is CurrencyConverterSideEffect.NavigateBack -> onNavigateBack()
                is CurrencyConverterSideEffect.ShowSnackbar -> snackbarHostState.showSnackbar(effect.message)
                is CurrencyConverterSideEffect.ShowConversionResultDialog -> conversionResult = effect.conversionResult
            }
        }
    }

    CurrencyConverterScreen(
        snackbarHostState = snackbarHostState,
        uiState = uiState,
        onEvent = viewModel::onEvent
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrencyConverterScreen(
    snackbarHostState: SnackbarHostState,
    uiState: CurrencyConverterUiState,
    onEvent: (CurrencyConverterEvent) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Currency Converter") },
                navigationIcon = {
                    IconButton(onClick = { onEvent(OnBackClick) }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            when (uiState) {
                is CurrencyConverterUiState.Loading -> CircularProgressIndicator()
                is CurrencyConverterUiState.Form -> {
                    TransactionTextFieldComponent(
                        value = uiState.amount,
                        label = "Amount",
                        supportingText = uiState.validation.amountError,
                        onValueChange = { onEvent(CurrencyConverterEvent.OnAmountChange(it)) },
                    )
                    TransactionTextFieldComponent(
                        value = uiState.fromCode ?: "",
                        label = "From Code (e.g. USD)",
                        supportingText = uiState.validation.fromCodeError,
                        onValueChange = { onEvent(CurrencyConverterEvent.OnFromCurrencyChange(it.uppercase())) },
                    )
                    TransactionTextFieldComponent(
                        value = uiState.toCode ?: "",
                        label = "To Code (e.g. EUR)",
                        supportingText = uiState.validation.toCodeError,
                        onValueChange = { onEvent(CurrencyConverterEvent.OnToCurrencyChange(it.uppercase())) },
                    )

                    var expanded by remember { mutableStateOf(false) }
                    val selectedLabel = uiState.providerOptions.firstOrNull { it.first == uiState.selectedProviderId }?.second ?: ""
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded }
                    ) {
                        TextField(
                            value = selectedLabel,
                            onValueChange = {},
                            label = { Text("Rate Provider") },
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                            modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable).fillMaxWidth()
                        )
                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            uiState.providerOptions.forEach { (id, label) ->
                                DropdownMenuItem(
                                    text = { Text(label) },
                                    onClick = {
                                        expanded = false
                                        onEvent(CurrencyConverterEvent.OnProviderChange(id))
                                    }
                                )
                            }
                        }
                    }
                    uiState.validation.providerIdError?.let {
                        Text(
                            text = it,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    PrimaryButton(onClick = { onEvent(CurrencyConverterEvent.OnConvertClick) }) {
                        Text("Convert")
                    }
                }
            }
        }
    }
} 