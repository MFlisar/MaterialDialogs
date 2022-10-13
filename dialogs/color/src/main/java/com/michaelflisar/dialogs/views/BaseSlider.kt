package com.michaelflisar.dialogs.views

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.google.android.material.slider.Slider
import kotlin.math.roundToInt

internal abstract class BaseSlider @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : LinearLayout(context, attrs) {

    lateinit var slider: Slider
        protected set
    protected lateinit var tvLabel: TextView
    protected lateinit var tvValue: TextView

    var onSelectionChanged: ((value: Int) -> Unit)? = null
    var value: Int = 0
        set(value) {
            if (field != value) {
                field = value
                update()
            }
        }

    fun init() {
        slider.setLabelFormatter { "${it.roundToInt()}" }
        slider.addOnChangeListener { slider, value, fromUser ->
            if (fromUser) {
                this.value = value.roundToInt()
                onSelectionChanged?.invoke(this.value)
            }
        }
        update()
    }

    fun setLabel(label: String) {
        tvLabel.text = label
        tvLabel.visibility = if (label.isNotEmpty()) View.VISIBLE else View.GONE
    }

    private fun update() {
        slider.value = value.toFloat()
        tvValue.text = value.toString()
    }
}