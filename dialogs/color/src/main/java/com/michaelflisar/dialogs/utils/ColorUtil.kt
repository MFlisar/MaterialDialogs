package com.michaelflisar.dialogs.utils

import android.content.Context
import android.graphics.Color
import androidx.annotation.ColorInt
import androidx.core.graphics.ColorUtils
import com.michaelflisar.dialogs.classes.ColorDefinitions
import com.michaelflisar.dialogs.classes.GroupedColor
import kotlin.math.roundToInt

internal object ColorUtil {

    fun getNearestColorGroup(context: Context, color: Int): GroupedColor {
        val solidColor = ColorUtils.setAlphaComponent(color, 255)
        var bestMatch = ColorDefinitions.COLORS_BW
        var minDiff: Double? = null
        for (c in ColorDefinitions.COLORS) {
            val isMono = Color.red(color) == Color.green(color) &&
                    Color.red(color) == Color.blue(color)
            if (isMono) {
                return ColorDefinitions.COLORS_BW
            }
            val diff = calcColorDifference(solidColor, c.get(context))
            if (minDiff == null || minDiff > diff) {
                minDiff = diff
                bestMatch = c
            }
        }
        return bestMatch
    }

    fun getBestTextColor(background: Int): Int {
        if (Color.alpha(background) <= 255 * 0.4f) {
            return Color.BLACK
        }
        return if (getDarknessFactor(background) > 0.2f) {
            Color.WHITE
        } else {
            Color.BLACK
        }
    }

    fun getDarknessFactor(color: Int): Double {
        return 1 - (0.299 * Color.red(color) + 0.587 * Color.green(color) + 0.114 * Color.blue(color)) / 255
    }

    fun calcColorDifference(c1: Int, c2: Int): Double {
        val r1 = Color.red(c1)
        val g1 = Color.green(c1)
        val b1 = Color.blue(c1)
        val r2 = Color.red(c2)
        val g2 = Color.green(c2)
        val b2 = Color.blue(c2)
        val diffRed = Math.abs(r1 - r2)
        val diffGreen = Math.abs(g1 - g2)
        val diffBlue = Math.abs(b1 - b2)
        val pctDiffRed = diffRed.toDouble() / 255f
        val pctDiffGreen = diffGreen.toDouble() / 255f
        val pctDiffBlue = diffBlue.toDouble() / 255f
        return (pctDiffRed + pctDiffGreen + pctDiffBlue) / 3f
    }
}