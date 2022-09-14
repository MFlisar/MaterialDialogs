package com.michaelflisar.dialogs.classes

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

class Color(
    override val color: Int,
    override val label: String
) : IColor