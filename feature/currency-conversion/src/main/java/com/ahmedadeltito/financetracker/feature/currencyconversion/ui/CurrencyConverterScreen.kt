package com.ahmedadeltito.financetracker.feature.currencyconversion.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ahmedadeltito.financetracker.feature.currencyconversion.ui.CurrencyConverterEvent.OnBackClick
import com.ahmedadeltito.financetracker.ui.components.PrimaryButton
import kotlinx.coroutines.flow.collectLatest

@Composable
fun CurrencyConverterRoute(
    onNavigateBack: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }

    val viewModel: CurrencyConverterViewModel = hiltViewModel()
    val uiState by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = true) {
        viewModel.sideEffect.collectLatest { effect ->
            when (effect) {
                is CurrencyConverterSideEffect.NavigateBack -> onNavigateBack()
                is CurrencyConverterSideEffect.ShowSnackbar -> snackbarHostState.showSnackbar(effect.message)
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
    uiState: CurrencyConverterState,
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
                is CurrencyConverterState.Loading -> CircularProgressIndicator()
                is CurrencyConverterState.Input -> {
                    OutlinedTextField(
                        value = uiState.amount,
                        onValueChange = { onEvent(CurrencyConverterEvent.OnAmountChange(it)) },
                        label = { Text("Amount") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = uiState.fromCode ?: "",
                        onValueChange = { onEvent(CurrencyConverterEvent.OnFromCurrencyChange(currencyCode = it)) },
                        label = { Text("From Currency (e.g. USD)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = uiState.toCode ?: "",
                        onValueChange = { onEvent(CurrencyConverterEvent.OnToCurrencyChange(currencyCode = it)) },
                        label = { Text("To Currency (e.g. EUR)") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    var dropdownMenuexpanded by remember { mutableStateOf(false) }
                    OutlinedTextField(
                        value = uiState.providerOptions.firstOrNull {
                            it.first == uiState.selectedProviderId
                        }?.second ?: "",
                        onValueChange = { },
                        readOnly = true,
                        label = { Text("Rate Provider") },
                        trailingIcon = {
                            IconButton(onClick = { dropdownMenuexpanded = true }) {
                                Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                    DropdownMenu(
                        expanded = dropdownMenuexpanded,
                        onDismissRequest = { dropdownMenuexpanded = false }
                    ) {
                        uiState.providerOptions.forEach { (id, label) ->
                            DropdownMenuItem(
                                text = { Text(label) },
                                onClick = {
                                    dropdownMenuexpanded = false
                                    onEvent(CurrencyConverterEvent.OnProviderChange(id))
                                }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    PrimaryButton(onClick = { onEvent(CurrencyConverterEvent.OnConvertClick) }) {
                        Text("Convert")
                    }
                }
                is CurrencyConverterState.Result -> Text(
                    text = "Result: ${uiState.result}",
                    style = MaterialTheme.typography.headlineMedium
                )
                is CurrencyConverterState.Error -> Text(
                    text = uiState.message,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
} 