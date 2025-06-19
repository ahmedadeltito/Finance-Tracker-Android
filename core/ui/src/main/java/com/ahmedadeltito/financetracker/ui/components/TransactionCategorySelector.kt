package com.ahmedadeltito.financetracker.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ahmedadeltito.financetracker.ui.model.TransactionCategoryUiModel
import com.ahmedadeltito.financetracker.ui.preview.PreviewData
import com.ahmedadeltito.financetracker.ui.theme.FinanceTrackerTheme

@Composable
fun TransactionCategorySelector(
    categories: List<TransactionCategoryUiModel>,
    selectedCategoryId: String,
    onCategorySelected: (String) -> Unit,
    error: String?
) {
    val selectedCategoryId = rememberSaveable { mutableStateOf(selectedCategoryId) }
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Category",
            style = MaterialTheme.typography.bodyMedium,
            color = if (error != null) MaterialTheme.colorScheme.error
            else MaterialTheme.colorScheme.onSurface
        )
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.height(200.dp)
        ) {
            items(categories) { category ->
                TransactionCategoryItem(
                    category = category,
                    isSelected = category.id == selectedCategoryId.value,
                    onClick = {
                        selectedCategoryId.value = category.id
                        onCategorySelected(category.id)
                    }
                )
            }
        }
        if (error != null) {
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@LightAndDarkPreview
@Composable
private fun TransactionCategorySelectorPreview() {
    FinanceTrackerTheme {
        Surface {
            var selectedCategory = remember { PreviewData.sampleCategories.first().id }
            TransactionCategorySelector(
                categories = PreviewData.sampleCategories,
                selectedCategoryId = selectedCategory,
                onCategorySelected = { selectedCategory = it },
                error = "Error Message"
            )
        }
    }
}