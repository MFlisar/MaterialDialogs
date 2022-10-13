package com.michaelflisar.dialogs.classes

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat

internal sealed interface IColor {

    val color: Int
    val label: String

    fun getCustomDrawable(): Drawable? = null

    fun get(context: Context): Int {
        return ContextCompat.getColor(context, color)
    }
}