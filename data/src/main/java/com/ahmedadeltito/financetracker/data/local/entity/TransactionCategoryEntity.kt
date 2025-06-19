package com.ahmedadeltito.financetracker.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ahmedadeltito.financetracker.domain.entity.TransactionCategory
import com.ahmedadeltito.financetracker.domain.entity.TransactionType

@Entity(tableName = "transaction_categories")
data class TransactionCategoryEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val type: TransactionType,
    val iconUrl: String?,
    val color: String?
) {
    companion object {
        fun TransactionCategoryEntity.toDomain(): TransactionCategory = TransactionCategory(
            id = id,
            name = name,
            type = type,
            iconUrl = iconUrl,
            color = color
        )
        fun TransactionCategory.toEntity(): TransactionCategoryEntity = TransactionCategoryEntity(
            id = id,
            name = name,
            type = type,
            iconUrl = iconUrl,
            color = color
        )
    }
} 