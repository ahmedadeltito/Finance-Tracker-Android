package com.ahmedadeltito.financetracker.feature.transactions.mapper

import com.ahmedadeltito.financetracker.domain.entity.Transaction
import com.ahmedadeltito.financetracker.ui.mapper.DateMapper
import com.ahmedadeltito.financetracker.ui.model.TransactionUiModel

fun Transaction.toUiModel(): TransactionUiModel {
    val transactionType = type.toUiModel()
    return TransactionUiModel(
        id = id,
        amount = TransactionFormMapper.formatAmount(amount),
        formattedDate = DateMapper.formatDate(date),
        category = category.toUiModel(),
        note = notes.orEmpty(),
        type = transactionType,
        isSoftDeleted = false
    )
}
