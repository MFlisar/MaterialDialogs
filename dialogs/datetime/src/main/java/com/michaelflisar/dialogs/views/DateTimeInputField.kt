package com.michaelflisar.dialogs.views

import android.content.Context
import android.text.InputFilter
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import androidx.core.widget.doAfterTextChanged
import com.michaelflisar.dialogs.datetime.R
import com.michaelflisar.dialogs.datetime.databinding.MdfViewDtInputFieldBinding

internal class DateTimeInputField @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : LinearLayout(context, attrs, defStyleAttr, defStyleRes) {

    private val binding: MdfViewDtInputFieldBinding

    var value: Int = 0
        private set

    var displayValue: String = "00"
        private set

    private var min: Int = 0
    private var max: Int = 99
    private var length: Int = 2
    private var disableJumpToNextFocusOnce: Boolean = false

    init {
        orientation = VERTICAL
        binding = MdfViewDtInputFieldBinding.inflate(LayoutInflater.from(context), this)

        val a = context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.DateTimeInputField,
            defStyleAttr,
            defStyleRes
        )
        val editTextId = a.getResourceId(R.styleable.DateTimeInputField_mdf_editTextId, -1)
        val editTextNextId = a.getResourceId(R.styleable.DateTimeInputField_mdf_editTextNextId, -1)
        a.recycle()

        binding.mdfTiet.id = editTextId
        binding.mdfTiet.nextFocusDownId = editTextNextId
    }

    fun init(
        value: Int,
        min: Int,
        max: Int,
        length: Int,
        label: Int,
        onValueChanged: (value: Int) -> Unit
    ) {
        this.value = value
        this.min = min
        this.max = max
        this.length = length

        binding.mdfTiet.minEms = length
        binding.mdfTiet.filters = arrayOf(InputFilter.LengthFilter(length))

        binding.mdfTiet.doAfterTextChanged {
            val text = it?.toString() ?: ""
            val value = text.toIntOrNull() ?: 0
            val valid = !(value < this.min || value > this.max)
            if (valid) {
                this.value = value
                this.displayValue = text
                onValueChanged(value)
                if (!disableJumpToNextFocusOnce && text.length == this.max.toString().length && binding.mdfTiet.nextFocusDownId > 0) {
                    binding.root.rootView.findViewById<View>(binding.mdfTiet.nextFocusDownId)
                        .requestFocus()
                }
                disableJumpToNextFocusOnce = false
            } else {
                disableJumpToNextFocusOnce = true
                resetText(true)
            }
        }
        binding.mdfTiet.setOnFocusChangeListener { view, focused ->
            if (!focused) {
                val text = binding.mdfTiet.text?.toString() ?: ""
                if (text.isEmpty() || text.length < length) {
                    updateText(false)
                }
            }
        }
        binding.mdfLabel.setText(label)
    }

    fun update(value: Int, newMax: Int? = null) {
        if (newMax != null) {
            max = newMax
        }
        this.value = value
        updateText(false)
    }

    private fun updateText(setCursorToEnd: Boolean) {
        displayValue = this.value.toString().padStart(length, '0')
        val currentDisplayValue = binding.mdfTiet.text.toString()
        if (displayValue != currentDisplayValue) {
            binding.mdfTiet.setText(displayValue)
            if (setCursorToEnd) {
                binding.mdfTiet.setSelection(displayValue.length)
            }
        }
    }

    private fun resetText(setCursorToEnd: Boolean) {
        binding.mdfTiet.setText(displayValue)
        if (setCursorToEnd) {
            binding.mdfTiet.setSelection(displayValue.length)
        }
    }
}