package com.michaelflisar.dialogs.classes

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class GroupedColor(
    private val indexOfMainColor: Int,
    val colors: List<Color>
) : IColor, Parcelable {

    override val color: Int
        get() = colors[indexOfMainColor].color

    override val label = ""
}