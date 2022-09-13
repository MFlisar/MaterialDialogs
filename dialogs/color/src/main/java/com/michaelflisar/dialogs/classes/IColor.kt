package com.michaelflisar.dialogs.classes

import android.content.Context
import androidx.core.content.ContextCompat

sealed interface IColor {

    val color: Int
    val label: String

    fun get(context: Context): Int {
        return ContextCompat.getColor(context, color)
    }
}