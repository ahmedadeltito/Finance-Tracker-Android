package com.ahmedadeltito.financetracker.data.local.converter

import androidx.room.TypeConverter
import com.ahmedadeltito.financetracker.domain.entity.TransactionType
import java.math.BigDecimal
import java.util.Date

class RoomTypeConverters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? = value?.let { Date(it) }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? = date?.time

    @TypeConverter
    fun fromString(value: String?): BigDecimal? = value?.let { BigDecimal(it) }

    @TypeConverter
    fun bigDecimalToString(bigDecimal: BigDecimal?): String? = bigDecimal?.toString()

    @TypeConverter
    fun fromTransactionType(value: TransactionType?): String? = value?.name

    @TypeConverter
    fun toTransactionType(value: String?): TransactionType? = value?.let { TransactionType.valueOf(it) }
} 