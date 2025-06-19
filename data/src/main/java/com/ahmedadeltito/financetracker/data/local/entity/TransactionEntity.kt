package com.ahmedadeltito.financetracker.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ahmedadeltito.financetracker.domain.entity.Transaction
import com.ahmedadeltito.financetracker.domain.entity.TransactionCategory
import com.ahmedadeltito.financetracker.domain.entity.TransactionType
import java.math.BigDecimal
import java.util.Date

@Entity(
    tableName = "transactions",
    foreignKeys = [
        androidx.room.ForeignKey(
            entity = TransactionCategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"],
            onDelete = androidx.room.ForeignKey.RESTRICT
        )
    ]
)
data class TransactionEntity(
    @PrimaryKey
    val id: String,
    val type: TransactionType,
    val amount: BigDecimal,
    val currency: String,
    val categoryId: String,
    val date: Date,
    val notes: String?,
    val createdAt: Date,
    val updatedAt: Date
) {
    companion object {
        fun TransactionEntity.toDomain(category: TransactionCategory): Transaction = Transaction(
            id = id,
            type = type,
            amount = amount,
            currency = currency,
            category = category,
            date = date,
            notes = notes,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
        fun Transaction.toEntity(): TransactionEntity = TransactionEntity(
            id = id,
            type = type,
            amount = amount,
            currency = currency,
            categoryId = category.id,
            date = date,
            notes = notes,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }
} 