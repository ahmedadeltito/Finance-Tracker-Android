package com.ahmedadeltito.financetracker.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ahmedadeltito.financetracker.ui.model.TransactionTypeUiModel
import com.ahmedadeltito.financetracker.ui.theme.FinanceTrackerTheme

@Composable
fun TransactionTypeSelectorComponent(
    type: TransactionTypeUiModel,
    onTypeChange: (TransactionTypeUiModel) -> Unit
) {
    SingleChoiceSegmentedButtonRow(
        modifier = Modifier.fillMaxWidth()
    ) {
        SegmentedButton(
            selected = type == TransactionTypeUiModel.Income,
            onClick = { onTypeChange(TransactionTypeUiModel.Income) },
            shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2)
        ) {
            Text("Income")
        }
        SegmentedButton(
            selected = type == TransactionTypeUiModel.Expense,
            onClick = { onTypeChange(TransactionTypeUiModel.Expense) },
            shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2)
        ) {
            Text("Expense")
        }
    }
}

@LightAndDarkPreview
@Composable
private fun TransactionTypeSelectorPreview() {
    FinanceTrackerTheme {
        Surface {
            Box(modifier = Modifier.padding(16.dp)) {
                var selectedType by remember {
                    mutableStateOf<TransactionTypeUiModel>(TransactionTypeUiModel.Income)
                }
                TransactionTypeSelectorComponent(
                    type = selectedType,
                    onTypeChange = { selectedType = it }
                )
            }
        }
    }
}