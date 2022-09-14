package com.michaelflisar.dialogs

import android.content.res.ColorStateList
import android.widget.ImageView
import androidx.core.widget.ImageViewCompat

internal fun ImageView.tint(color: Int) {
    ImageViewCompat.setImageTintList(this, ColorStateList.valueOf(color))
}