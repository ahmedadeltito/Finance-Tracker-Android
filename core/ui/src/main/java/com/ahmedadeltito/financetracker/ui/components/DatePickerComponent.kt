package com.ahmedadeltito.financetracker.ui.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ahmedadeltito.financetracker.ui.theme.FinanceTrackerTheme
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerComponent(
    onDateChange: (Date) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState()
    DatePickerDialog(
        onDismissRequest = { onDismiss() },
        confirmButton = {
            Button(
                onClick = {
                    datePickerState.selectedDateMillis?.let {
                        onDateChange(Date(it))
                    }
                    onDismiss()
                }
            ) {
                Text("OK")
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@LightAndDarkPreview
@Composable
private fun DatePickerComponentPreview() {
    FinanceTrackerTheme {
        Surface {
            Box(modifier = Modifier.padding(16.dp)) {
                var selectedDate by remember { mutableStateOf(Date()) }
                DatePickerComponent(
                    onDateChange = { selectedDate = it },
                    onDismiss = { }
                )
            }
        }
    }
}