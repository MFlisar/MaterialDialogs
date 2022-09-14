package com.michaelflisar.dialogs.views

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.text.InputFilter
import android.text.InputFilter.LengthFilter
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.core.graphics.ColorUtils
import androidx.core.widget.doOnTextChanged
import com.google.android.material.slider.LabelFormatter
import com.michaelflisar.dialogs.color.databinding.MdfViewCustomColorBinding
import com.michaelflisar.dialogs.drawables.DrawableCheckerBoard
import com.michaelflisar.dialogs.utils.ColorUtil
import java.lang.String


class CustomColorView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : LinearLayout(context, attrs) {

    val binding: MdfViewCustomColorBinding
    val slidersARGB: List<Slider>

    var value: Int
        get() {
            val values = slidersARGB.map { it.value }
            return Color.argb(values[0], values[1], values[2], values[3])
        }
        set(value) {
            updateAfterValueChanged(value, true)
        }

    var onValueChanged: ((value: Int) -> Unit)? = null

    init {
        orientation = VERTICAL
        binding = MdfViewCustomColorBinding.inflate(LayoutInflater.from(context), this)

        slidersARGB = listOf(
            binding.sliderAlpha,
            binding.sliderRed,
            binding.sliderGreen,
            binding.sliderBlue
        )
        val labels = "ARGB".toList().map { it.toString() }
        val colors = listOf(
            Color.GRAY,
            Color.RED,
            Color.GREEN,
            Color.BLUE
        )
        slidersARGB.forEachIndexed { index, slider ->
            slider.setLabel(labels[index])
            slider.slider.trackActiveTintList = ColorStateList.valueOf(colors[index])
            slider.slider.trackInactiveTintList =
                ColorStateList.valueOf(ColorUtils.setAlphaComponent(colors[index], 80))
            slider.slider.thumbTintList = ColorStateList.valueOf(colors[index])
            slider.slider.thumbStrokeColor = ColorStateList.valueOf(colors[index])
            slider.slider.backgroundTintList = ColorStateList.valueOf(colors[index])
            slider.slider.haloTintList = ColorStateList.valueOf(ColorUtils.setAlphaComponent(colors[index], 80))
            slider.slider.labelBehavior = LabelFormatter.LABEL_GONE // no easy way to color it so we hide it, we display the value next to the slider anyway
            slider.onSelectionChanged = {
                reportValue()
                updatePreview(true)
            }
        }

        binding.vCheckerBackground.background = DrawableCheckerBoard()
        updatePreview(true)
        supportsAlpha(true)
    }

    fun supportsAlpha(supported: Boolean) {
        if (supported) {
            binding.sliderAlpha.visibility = VISIBLE
        } else {
            binding.sliderAlpha.visibility = GONE
            binding.sliderAlpha.value = 255

        }
        val ems = if (supported) 8 else 6
        binding.etCustomValue.maxWidth = ems
        binding.etCustomValue.minEms = ems
        binding.etCustomValue.maxEms = ems
        binding.etCustomValue.setEms(ems)
        binding.etCustomValue.filters = arrayOf<InputFilter>(LengthFilter(ems))
        binding.etCustomValue.doOnTextChanged { text, start, before, count ->
            if ((text?.length ?: 0) > 0) {
                try {
                    Log.d("COLOR", "Text = $text")
                    val color = Color.parseColor("#" + text.toString())
                    if (color != value)
                        updateAfterValueChanged(color, false)
                } catch (e: IllegalArgumentException) {
                }
            }
        }
        updatePreview(true)
    }

    private fun reportValue() {
        onValueChanged?.invoke(this.value)
    }

    private fun updatePreview(updateText: Boolean) {
        binding.colorPreview.setBackgroundColor(value)
        binding.etCustomValue.setTextColor(ColorUtil.getBestTextColor(value))
        if (updateText) {
            val showAlpha = binding.sliderAlpha.visibility == VISIBLE
            val text = if (showAlpha) {
                String.format("%08X", value)
            } else {
                String.format("%06X", 0xFFFFFF and value)
            }
            binding.etCustomValue.setText(text)
        }
    }

    private fun updateAfterValueChanged(value: Int, updateText: Boolean) {
        val values = listOf(
            Color.alpha(value),
            Color.red(value),
            Color.green(value),
            Color.blue(value)
        )
        slidersARGB.forEachIndexed { index, slider -> slider.value = values[index] }
        updatePreview(updateText)
    }
}