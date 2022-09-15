package com.michaelflisar.dialogs

import android.os.Bundle
import android.os.Parcelable
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.LifecycleOwner
import com.michaelflisar.dialogs.classes.RepeatListener
import com.michaelflisar.dialogs.interfaces.IMaterialViewManager
import com.michaelflisar.dialogs.number.R
import com.michaelflisar.dialogs.number.databinding.MdfContentNumberBinding
import kotlinx.parcelize.Parcelize

internal class NumberViewManager<T : Number>(
    private val setup: DialogNumber<T>
) : IMaterialViewManager<DialogNumber<T>, MdfContentNumberBinding> {

    override val wrapInScrollContainer = true

    internal lateinit var currentValue: T

    override fun createContentViewBinding(
        layoutInflater: LayoutInflater,
        parent: ViewGroup?,
        attachToParent: Boolean
    ) = MdfContentNumberBinding.inflate(layoutInflater, parent, attachToParent)

    override fun initBinding(
        lifecycleOwner: LifecycleOwner,
        binding: MdfContentNumberBinding,
        savedInstanceState: Bundle?
    ) {
        val state =
            MaterialDialogUtil.getViewState<ViewState<T>>(savedInstanceState)
        currentValue = state?.value ?: setup.value
        setup.description.display(binding.mdfDescription)
        if (binding.mdfDescription.text.isEmpty()) {
            binding.mdfDescription.visibility = View.GONE
        }
        val repeatListener = RepeatListener(400L, 100L) {
            currentValue = adjust(currentValue, it.id == R.id.mdf_increase)
            updateDisplayValue(binding)
        }
        binding.mdfIncrease.setOnTouchListener(repeatListener)
        binding.mdfDecrease.setOnTouchListener(repeatListener)

        binding.mdfTextInputEditText.inputType = when (setup.value) {
            is Int,
            is Long -> InputType.TYPE_CLASS_NUMBER
            is Float,
            is Double -> InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
            else -> throw RuntimeException("Class ${setup.value::class} not supported!")
        }
        binding.mdfTextInputEditText.doAfterTextChanged {
            setError(binding, "")
        }
        updateDisplayValue(binding)
    }

    override fun saveViewState(binding: MdfContentNumberBinding, outState: Bundle) {
        MaterialDialogUtil.saveViewState(outState, ViewState(currentValue))
    }

    // -----------
    // Functions
    // -----------

    private fun adjust(value: T, increase: Boolean): T {
        val newValue = when (value) {
            is Int -> value as Int + (setup.setup.step as Int * (if (increase) 1 else -1))
            is Long -> value as Long + (setup.setup.step as Long * (if (increase) 1L else -1L))
            is Float -> value as Float + (setup.setup.step as Float * (if (increase) 1f else -1f))
            is Double -> value as Double + (setup.setup.step as Double * (if (increase) 1.0 else -1.0))
            else -> throw RuntimeException()
        } as T

        val tooLow = when (newValue) {
            is Int -> (newValue as Int) < (setup.setup.min as Int)
            is Long -> (newValue as Long) < (setup.setup.min as Long)
            is Float -> (newValue as Float) < (setup.setup.min as Float)
            is Double -> (newValue as Double) < (setup.setup.min as Double)
            else -> throw RuntimeException()
        }

        if (tooLow)
            return setup.setup.min

        val tooHigh = when (newValue) {
            is Int -> (newValue as Int) > (setup.setup.max as Int)
            is Long -> (newValue as Long) > (setup.setup.max as Long)
            is Float -> (newValue as Float) > (setup.setup.max as Float)
            is Double -> (newValue as Double) > (setup.setup.max as Double)
            else -> throw RuntimeException()
        }

        if (tooHigh)
            return setup.setup.max

        return newValue
    }

    @Suppress("UNCHECKED_CAST")
    private fun parse(text: String): T {
        return when (setup.value) {
            is Int -> text.toIntOrNull() ?: 0
            is Long -> text.toLongOrNull() ?: 0L
            is Float -> text.toFloatOrNull() ?: 0f
            is Double -> text.toDoubleOrNull() ?: 0.0
            else -> throw RuntimeException()
        } as T
    }

    private fun updateDisplayValue(binding: MdfContentNumberBinding) {
        binding.mdfTextInputEditText.setText(
            setup.setup.formatter?.format(binding.root.context, currentValue)
                ?: currentValue.toString())
        binding.mdfTextInputEditText.clearFocus()
        MaterialDialogUtil.ensureKeyboardCloses(binding.mdfTextInputEditText)
    }

    internal fun setError(binding: MdfContentNumberBinding, error: String) {
        binding.mdfTextInputLayout.error = error.takeIf { it.isNotEmpty() }
    }

    internal fun getCurrentValue(binding: MdfContentNumberBinding): T? {
        val formatterText = setup.setup.formatter?.format(binding.root.context, currentValue)
            ?: currentValue.toString()
        val input = binding.mdfTextInputEditText.text.toString()
        if (input == formatterText)
            return currentValue

        val userValue = parse(input)
        if (setup.setup.isValid(userValue)) {
            currentValue = userValue
            return currentValue
        }
        return null
    }

    // -----------
    // State
    // -----------

    @Parcelize
    private class ViewState<T : Number>(
        val value: T
    ) : Parcelable
}