package com.michaelflisar.dialogs.utils

import android.content.Context
import android.graphics.Color
import androidx.annotation.ColorInt
import com.michaelflisar.dialogs.classes.ColorDefinitions
import com.michaelflisar.dialogs.classes.GroupedColor
import kotlin.math.roundToInt

object ColorUtil {

    val COLOR_FILTER_NEGATIVE = floatArrayOf(
        -1.0f,
        0f,
        0f,
        0f,
        255f,
        0f,
        -1.0f,
        0f,
        0f,
        255f,
        0f,
        0f,
        -1.0f,
        0f,
        255f,
        0f,
        0f,
        0f,
        1.0f,
        0f
    )
    const val DARKNESS_FACTOR_BORDER = 0.2


    fun getNameFromMaterialColor(context: Context, color: Int): String {
        val name = context.resources.getResourceEntryName(color)
        val firstUnderlineIndex = name.indexOf("_")
        val lastUnderlineIndex = name.lastIndexOf("_")
        //        String group = name.substring(firstUnderlineIndex + 1, lastUnderlineIndex).replace("_", " ");
        return name.substring(lastUnderlineIndex + 1)
    }

    fun getNearestColorGroup(context: Context, color: Int): GroupedColor {
        val solidColor = adjustAlpha(color, 255)
        var bestMatch = ColorDefinitions.COLORS_BW
        var minDiff: Double? = null
        for (c in ColorDefinitions.COLORS) {
            // s/w => nur auswählen, falls Farben gleich sind
            if (c.resColors.size == 2) {
                if (solidColor == c.getColor(context, 0) || solidColor == c.getColor(context, 1)) {
                    return c
                }
            } else {
                val diff = calcColorDifference(solidColor, c.getColor(context, 6))
                if (minDiff == null || minDiff > diff) {
                    minDiff = diff
                    bestMatch = c
                }
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

    fun isColorDark(color: Int): Boolean {
        val darkness = getDarknessFactor(color)
        return darkness >= 0.5
    }

    @JvmStatic
    fun getDarknessFactor(color: Int): Double {
        return 1 - (0.299 * Color.red(color) + 0.587 * Color.green(color) + 0.114 * Color.blue(color)) / 255
    }

    @JvmStatic
    fun getColorAsRGB(color: Int): String {
        return String.format("#%06X", 0xFFFFFF and color)
    }

    fun getColorAsARGB(color: Int): String {
        return String.format("#%08X", -0x1 and color)
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

    @ColorInt
    fun adjustAlpha(@ColorInt color: Int, percentages: Float): Int {
        val alpha = (255 * percentages).roundToInt()
        val red = Color.red(color)
        val green = Color.green(color)
        val blue = Color.blue(color)
        return Color.argb(alpha, red, green, blue)
    }

    @ColorInt
    fun adjustAlpha(@ColorInt color: Int, value: Int): Int {
        val alpha = value
        val red = Color.red(color)
        val green = Color.green(color)
        val blue = Color.blue(color)
        return Color.argb(alpha, red, green, blue)
    }
}