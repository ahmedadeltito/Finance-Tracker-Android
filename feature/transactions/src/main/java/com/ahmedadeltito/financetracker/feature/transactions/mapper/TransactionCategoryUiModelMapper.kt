package com.ahmedadeltito.financetracker.feature.transactions.mapper

import com.ahmedadeltito.financetracker.domain.entity.TransactionCategory
import com.ahmedadeltito.financetracker.ui.mapper.ColorMapper
import com.ahmedadeltito.financetracker.ui.model.TransactionCategoryUiModel

fun TransactionCategory.toUiModel(): TransactionCategoryUiModel {
    return TransactionCategoryUiModel(
        id = id,
        name = name,
        type = type.toUiModel(),
        color = ColorMapper.parseColor(color),
        icon = iconUrl ?: name.first().toString()
    )
}
