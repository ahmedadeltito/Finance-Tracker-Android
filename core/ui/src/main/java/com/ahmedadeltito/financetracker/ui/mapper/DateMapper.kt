package com.ahmedadeltito.financetracker.ui.mapper

import androidx.core.net.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DateMapper {

    fun formatDate(date: Date): String {
        val dateFormatter = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        return dateFormatter.format(date)
    }

    fun parseDateString(dateString: String): Date {
        val dateFormatter = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        return try {
            dateFormatter.parse(dateString)
        } catch (e: ParseException) {
            println("Error parsing date string: '$dateString'. ${e.message}")
            Date()
        }
    }

}