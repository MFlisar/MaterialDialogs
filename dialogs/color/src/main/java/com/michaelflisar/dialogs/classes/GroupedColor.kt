package com.michaelflisar.dialogs.classes

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Build
import android.view.View
import androidx.core.content.ContextCompat
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import com.michaelflisar.dialogs.color.R
import com.michaelflisar.dialogs.setCircleBackground
import com.michaelflisar.dialogs.utils.ColorUtil

class GroupedColor(
        mainColorIndex: Int,
        private val resTitle: Int,
        vararg val resColors: Int
) {

    private val mainColorRes: Int = resColors[mainColorIndex]

    fun getMainColor(context: Context): Int {
        return ContextCompat.getColor(context, mainColorRes)
    }

    fun getColor(context: Context, index: Int): Int {
        return ContextCompat.getColor(context, resColors[index])
    }

    fun getHeaderDescription(context: Context): String {
        return context.getString(resTitle).uppercase()
    }

    fun getColorDescription(context: Context, index: Int): String {
        return ColorUtil.getNameFromMaterialColor(context, resColors[index])
    }

    // ----------------
    // adapter functions
    // ----------------

    private fun hasExtraItems(isLandscape: Boolean) = !isLandscape && resColors.size == 14

    fun geAdapterItemCount(isLandscape: Boolean): Int {
        // we want a break before the 4 accent colors in portrait mode (fits perfectly without an extra row for the 4 columns)
        return if (hasExtraItems(isLandscape))
            resColors.size + 2
        else resColors.size
    }

    private fun getAdapterIndex(context: Context, isLandscape: Boolean, index: Int): Int {
        return if (hasExtraItems(isLandscape)) {
            val indexToUse = if (index <= 9) index else if (index > 11) (index - 2) else -1
            indexToUse
        } else index
    }

    fun getAdapterColor(context: Context, isLandscape: Boolean, index: Int): Int? {
        return getAdapterIndex(context, isLandscape, index).takeIf { it != -1 }?.let {
            ContextCompat.getColor(context, resColors[it])
        }
    }

    fun getAdapterColorName(context: Context, isLandscape: Boolean, index: Int): String? {
        return getAdapterIndex(context, isLandscape, index).takeIf { it != -1 }?.let {
            ColorUtil.getNameFromMaterialColor(context, resColors[it])
        }
    }

    // ----------------
    // helper functions
    // ----------------

    fun findMatchingColor(context: Context, color: Int): Int? {
        val solid = ColorUtil.adjustAlpha(color, 255)
        val matchingColor = resColors.find { ColorUtil.adjustAlpha(ContextCompat.getColor(context, it), 255) == solid }
        return matchingColor?.let { ContextCompat.getColor(context, it) }
    }

    fun findMatchingColorIndex(context: Context, color: Int?): Int {
        if (color == null) {
            return -1
        }
        val matchingColorIndex = resColors.indexOfFirst { ContextCompat.getColor(context, it) == color }
        return matchingColorIndex
    }

    fun displayAsCircleViewBackground(view: View) {
        if (this == ColorDefinitions.COLORS_BW) {
            val d: Drawable? = VectorDrawableCompat.create(view.context.resources, R.drawable.vector_bw, view.context.theme)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) view.background = d else view.setBackgroundDrawable(d)
        } else view.setCircleBackground(getMainColor(view.context), false)
    }
}