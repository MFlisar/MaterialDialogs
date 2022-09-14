package com.michaelflisar.dialogs.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import com.michaelflisar.dialogs.color.databinding.MdfViewColorSliderHorizontalBinding

class ColorSliderHorizontal @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : BaseSlider(context, attrs) {

    init {
        orientation = HORIZONTAL
        val binding =
            MdfViewColorSliderHorizontalBinding.inflate(LayoutInflater.from(context), this)
        tvLabel = binding.tvLabel
        tvValue = binding.tvValue
        slider = binding.slider
        init()
    }
}