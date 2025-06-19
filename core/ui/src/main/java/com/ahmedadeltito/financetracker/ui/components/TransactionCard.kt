package com.ahmedadeltito.financetracker.ui.components

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
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ahmedadeltito.financetracker.ui.model.TransactionCategoryUiModel
import com.ahmedadeltito.financetracker.ui.model.TransactionTypeUiModel
import com.ahmedadeltito.financetracker.ui.model.TransactionUiModel
import com.ahmedadeltito.financetracker.ui.preview.PreviewData
import com.ahmedadeltito.financetracker.ui.theme.Expense
import com.ahmedadeltito.financetracker.ui.theme.FinanceTrackerTheme
import com.ahmedadeltito.financetracker.ui.theme.Income

@Composable
fun TransactionCard(
    transaction: TransactionUiModel,
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
                    categoryColor = transaction.category.color,
                    categoryIcon = transaction.category.icon
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
                        text = transaction.formattedDate,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }

            // Amount and arrow
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = transaction.type.icon,
                        contentDescription = transaction.type.displayText,
                        tint = transaction.type.color,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = transaction.amount,
                        style = MaterialTheme.typography.titleMedium,
                        color = transaction.type.color,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
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
    categoryIcon: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = categoryIcon,
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

@Preview(showBackground = true)
@Composable
private fun TransactionCardIncomePreview() {
    FinanceTrackerTheme {
        TransactionCard(
            transaction = TransactionUiModel(
                id = "1",
                amount = "+$1,500.00",
                formattedDate = "Jan 15, 2024",
                category = TransactionCategoryUiModel(
                    id = "1",
                    name = "Salary",
                    type = TransactionTypeUiModel.Income,
                    color = Income,
                    icon = "S"
                ),
                note = "Monthly salary",
                type = TransactionTypeUiModel.Income
            ),
            onClick = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun TransactionCardExpensePreview() {
    FinanceTrackerTheme {
        TransactionCard(
            transaction = TransactionUiModel(
                id = "2",
                amount = "-$50.00",
                formattedDate = "Jan 15, 2024",
                category = TransactionCategoryUiModel(
                    id = "2",
                    name = "Food",
                    type = TransactionTypeUiModel.Expense,
                    color = Expense,
                    icon = "F"
                ),
                note = "Lunch",
                type = TransactionTypeUiModel.Expense
            ),
            onClick = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun TransactionCardListPreview() {
    FinanceTrackerTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            TransactionCard(
                transaction = TransactionUiModel(
                    id = "1",
                    amount = "+$1,500.00",
                    formattedDate = "Jan 15, 2024",
                    category = TransactionCategoryUiModel(
                        id = "1",
                        name = "Salary",
                        type = TransactionTypeUiModel.Income,
                        color = Income,
                        icon = "S"
                    ),
                    note = "Monthly salary",
                    type = TransactionTypeUiModel.Income
                ),
                onClick = {}
            )
            Spacer(modifier = Modifier.height(8.dp))
            TransactionCard(
                transaction = TransactionUiModel(
                    id = "2",
                    amount = "-$50.00",
                    formattedDate = "Jan 15, 2024",
                    category = TransactionCategoryUiModel(
                        id = "2",
                        name = "Food",
                        type = TransactionTypeUiModel.Expense,
                        color = Expense,
                        icon = "F"
                    ),
                    note = "Lunch",
                    type = TransactionTypeUiModel.Expense
                ),
                onClick = {}
            )
        }
    }
}

@LightAndDarkPreview
@Composable
private fun TransactionCardPreview() {
    FinanceTrackerTheme {
        Surface {
            TransactionCard(
                transaction = PreviewData.sampleTransactions[0],
                onClick = {}
            )
        }
    }
}