package com.michaelflisar.dialogs.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import com.michaelflisar.dialogs.color.databinding.MdfViewCustomColorHorizontalBinding

class CustomColorViewHorizontal @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : BaseCustomColorView(context, attrs) {

    init {
        orientation = HORIZONTAL
        val binding =
            MdfViewCustomColorHorizontalBinding.inflate(LayoutInflater.from(context), this)
        slidersARGB = listOf(
            binding.sliderAlpha,
            binding.sliderRed,
            binding.sliderGreen,
            binding.sliderBlue
        )
        vCheckerBackground = binding.vCheckerBackground
        colorPreview = binding.colorPreview
        etCustomValue = binding.etCustomValue
        init()
    }
}