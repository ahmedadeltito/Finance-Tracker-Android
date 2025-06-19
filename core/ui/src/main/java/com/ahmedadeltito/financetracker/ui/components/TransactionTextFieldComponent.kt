package com.ahmedadeltito.financetracker.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.ahmedadeltito.financetracker.ui.theme.FinanceTrackerTheme

@Composable
fun TransactionTextFieldComponent(
    value: String,
    label: String,
    supportingText: String? = null,
    readOnly: Boolean = false,
    trailingIcon: @Composable (() -> Unit)? = null,
    onValueChange: (String) -> Unit = { }
) {
    OutlinedTextField(
        modifier = Modifier.fillMaxWidth(),
        value = value,
        onValueChange = { onValueChange(it) },
        label = { Text(label) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        isError = supportingText != null,
        readOnly = readOnly,
        trailingIcon = trailingIcon,
        supportingText = supportingText?.let { { Text(it) } }
    )
}

@LightAndDarkPreview
@Composable
private fun TransactionTextFieldPreview() {
    FinanceTrackerTheme {
        Surface {
            Column(modifier = Modifier.padding(16.dp)) {
                var text by remember { mutableStateOf("") }
                TransactionTextFieldComponent(
                    value = text,
                    onValueChange = { text = it },
                    label = "Amount",
                )
            }
        }
    }
}