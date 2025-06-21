package com.ahmedadeltito.financetracker.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.ahmedadeltito.financetracker.ui.theme.FinanceTrackerTheme

@Composable
fun TransactionAlertDialog(
    title: String,
    message: String,
    confirmButtonText: String,
    dismissButtonText: String? = null,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    val dismissButton: @Composable (() -> Unit)? = @Composable {
        dismissButtonText?.let { text -> TextButton(onClick = onDismiss) { Text(text) } }
    }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = { Text(message) },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm()
                    onDismiss()
                }
            ) {
                Text(confirmButtonText)
            }
        },
        dismissButton = dismissButton
    )
}

@LightAndDarkPreview
@Composable
fun TransactionAlertDialogPreview() {
    FinanceTrackerTheme {
        Surface {
            var open by remember { mutableStateOf(true) }
            if (open) {
                TransactionAlertDialog(
                    title = "Conversion Result",
                    message = "150 USD -> 137.25 EUR via Frankfurter.dev",
                    confirmButtonText = "OK",
                    onConfirm = { /* no-op */ },
                    onDismiss = { open = false }
                )
            }
        }
    }
}