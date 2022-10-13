package com.michaelflisar.dialogs.classes

import android.graphics.drawable.Drawable
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

internal class GroupedColor(
    private val indexOfMainColor: Int,
    val colors: List<Color>,
    val drawable: Drawable? = null
) : IColor {

    override val color: Int
        get() = colors[indexOfMainColor].color

    override val label = ""

    override fun getCustomDrawable() = drawable
}