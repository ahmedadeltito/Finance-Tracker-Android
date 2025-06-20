package com.ahmedadeltito.financetracker.feature.currencyconversion.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ahmedadeltito.financetracker.feature.currencyconversion.domain.entity.Currency
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.IconButton
import androidx.compose.runtime.setValue

@Composable
fun CurrencyConverterRoute(viewModel: CurrencyConverterViewModel = hiltViewModel()) {
    val uiState by viewModel.state.collectAsStateWithLifecycle()
    CurrencyConverterScreen(uiState = uiState, onEvent = viewModel::onEvent)
}

@Composable
fun CurrencyConverterScreen(
    uiState: CurrencyConverterState,
    onEvent: (CurrencyConverterEvent) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        when (uiState) {
            is CurrencyConverterState.Input -> {
                OutlinedTextField(
                    value = uiState.amount,
                    onValueChange = { onEvent(CurrencyConverterEvent.OnAmountChange(it)) },
                    label = { Text("Amount") },
                    modifier = Modifier.fillMaxWidth()
                )
                // For brevity, using simple text fields for currency codes.
                OutlinedTextField(
                    value = uiState.from?.code ?: "",
                    onValueChange = {
                        onEvent(
                            CurrencyConverterEvent.OnFromCurrencyChange(
                                Currency(
                                    it
                                )
                            )
                        )
                    },
                    label = { Text("From Currency (e.g. USD)") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = uiState.to?.code ?: "",
                    onValueChange = { onEvent(CurrencyConverterEvent.OnToCurrencyChange(Currency(it))) },
                    label = { Text("To Currency (e.g. EUR)") },
                    modifier = Modifier.fillMaxWidth()
                )

                // Provider selector
                var expanded by remember { mutableStateOf(false) }
                OutlinedTextField(
                    value = uiState.providerOptions.firstOrNull { it.first == uiState.selectedProviderId }?.second ?: "",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Rate Provider") },
                    trailingIcon = {
                        IconButton(onClick = { expanded = true }) {
                            Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    uiState.providerOptions.forEach { (id, label) ->
                        DropdownMenuItem(text = { Text(label) }, onClick = {
                            expanded = false
                            onEvent(CurrencyConverterEvent.OnProviderChange(id))
                        })
                    }
                }

                Button(onClick = { onEvent(CurrencyConverterEvent.OnConvertClick) }) {
                    Text("Convert")
                }
            }

            CurrencyConverterState.Loading -> CircularProgressIndicator()
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