package com.ahmedadeltito.financetracker.ui.mapper

import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.core.graphics.toColorInt
import com.ahmedadeltito.financetracker.ui.theme.Other

object ColorMapper {

    fun parseColor(colorString: String?): Color = try {
        val color = when {
            colorString == null -> return Other
            colorString.startsWith("#") -> colorString
            else -> "#$colorString"
        }
        Color(color.toColorInt())
    } catch (e: IllegalArgumentException) {
        Log.e("ColorMapper", "Invalid color string: $colorString", e)
        Other
    }

}