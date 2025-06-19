package com.ahmedadeltito.financetracker.ui.components

import android.view.SurfaceControl.Transaction
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ahmedadeltito.financetracker.domain.entity.Transaction
import com.ahmedadeltito.financetracker.domain.entity.TransactionType
import com.ahmedadeltito.financetracker.ui.theme.Expense
import com.ahmedadeltito.financetracker.ui.theme.Income
import java.text.SimpleDateFormat
import java.util.Locale
import androidx.core.graphics.toColorInt

@Composable
fun TransactionCard(
    transaction: Transaction,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Category icon and name
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                CategoryIcon(
                    categoryColor = Color((transaction.category.color ?: "#757575").toColorInt()),
                    categoryName = transaction.category.name
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = transaction.category.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                            .format(transaction.date),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }

            // Amount and arrow
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = formatAmount(transaction.amount.toString(), transaction.type),
                    style = MaterialTheme.typography.titleMedium,
                    color = if (transaction.type == TransactionType.INCOME) Income else Expense,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.Default.KeyboardArrowRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }
    }
}

@Composable
private fun CategoryIcon(
    categoryColor: Color,
    categoryName: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = categoryName.first().toString(),
        modifier = modifier
            .size(40.dp)
            .background(
                color = categoryColor.copy(alpha = 0.2f),
                shape = MaterialTheme.shapes.small
            )
            .padding(8.dp),
        style = MaterialTheme.typography.titleMedium,
        color = categoryColor
    )
}

private fun formatAmount(amount: String, type: TransactionType): String {
    return when (type) {
        TransactionType.INCOME -> "+$amount"
        TransactionType.EXPENSE -> "-$amount"
    }
} 