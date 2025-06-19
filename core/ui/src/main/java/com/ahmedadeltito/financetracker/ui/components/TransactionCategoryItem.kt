package com.ahmedadeltito.financetracker.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.ahmedadeltito.financetracker.ui.model.TransactionCategoryUiModel
import com.ahmedadeltito.financetracker.ui.preview.PreviewData
import com.ahmedadeltito.financetracker.ui.theme.FinanceTrackerTheme

@Composable
internal fun TransactionCategoryItem(
    category: TransactionCategoryUiModel,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(category.color.copy(alpha = 0.2f))
                .then(
                    if (isSelected) {
                        Modifier.border(
                            width = 2.dp,
                            color = category.color,
                            shape = CircleShape
                        )
                    } else Modifier
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = category.icon,
                style = MaterialTheme.typography.titleMedium,
                color = category.color
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = category.name,
            style = MaterialTheme.typography.bodySmall,
            maxLines = 1
        )
    }
}

@LightAndDarkPreview
@Composable
private fun TransactionCategoryItemPreview() {
    FinanceTrackerTheme {
        Surface {
            Box(modifier = Modifier.padding(16.dp)) {
                TransactionCategoryItem(
                    category = PreviewData.sampleCategories[0],
                    isSelected = true,
                    onClick = {}
                )
            }
        }
    }
}