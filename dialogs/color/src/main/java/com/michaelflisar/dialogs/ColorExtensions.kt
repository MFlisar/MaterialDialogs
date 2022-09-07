package com.michaelflisar.dialogs

import android.content.Context
import android.content.res.ColorStateList
import android.content.res.Configuration
import android.content.res.Resources.Theme
import android.graphics.drawable.GradientDrawable
import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import androidx.core.widget.ImageViewCompat
import androidx.fragment.app.Fragment
import com.michaelflisar.dialogs.color.R

internal fun Context.isThemeDark() : Boolean {
    val typedValue = TypedValue()
    theme.resolveAttribute(android.R.attr.colorBackground, typedValue, true)
    @ColorInt val color = typedValue.data
    return ColorUtils.calculateLuminance(color) < 0.5
}
internal fun Context.isLandscape() = resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

internal fun View.setCircleBackground(color: Int, withBorder: Boolean) {
    var drawable: GradientDrawable? = null
    drawable = if (withBorder) {
        ContextCompat.getDrawable(context, if (context.isThemeDark()) R.drawable.circle_with_border_dark else R.drawable.circle_with_border_light) as GradientDrawable
    } else {
        ContextCompat.getDrawable(context, R.drawable.circle) as GradientDrawable
    }
    drawable.setColor(color)
    background = drawable
}

internal fun ImageView.tint(color: Int) {
    ImageViewCompat.setImageTintList(this, ColorStateList.valueOf(color))
}

internal fun Fragment.isLandscape() = resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE