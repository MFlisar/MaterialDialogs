package com.michaelflisar.dialogs

import android.content.Context
import android.content.res.ColorStateList
import android.content.res.Configuration
import android.widget.ImageView
import androidx.core.widget.ImageViewCompat

internal fun ImageView.tint(color: Int) {
    ImageViewCompat.setImageTintList(this, ColorStateList.valueOf(color))
}

internal fun Context.isLandscape() = resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE