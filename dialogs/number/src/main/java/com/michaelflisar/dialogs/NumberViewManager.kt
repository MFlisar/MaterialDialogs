package com.michaelflisar.dialogs

import android.os.Bundle
import android.os.Parcelable
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import com.michaelflisar.dialogs.classes.BaseMaterialViewManager
import com.michaelflisar.dialogs.classes.RepeatListener
import com.michaelflisar.dialogs.interfaces.IMaterialDialogPresenter
import com.michaelflisar.dialogs.number.R
import com.michaelflisar.dialogs.number.databinding.MdfContentNumberBinding
import com.michaelflisar.dialogs.number.databinding.MdfContentNumberRowBinding
import kotlinx.parcelize.Parcelize

internal class NumberViewManager<T : Number>(
    override val setup: DialogNumber<T>
) : BaseMaterialViewManager<DialogNumber<T>, MdfContentNumberBinding>() {

    override val wrapInScrollContainer = true

    private val rowBindings = ArrayList<MdfContentNumberRowBinding>()
    private var currentValues = ArrayList<T>()

    override fun onCreateContentViewBinding(
        layoutInflater: LayoutInflater,
        parent: ViewGroup?,
        attachToParent: Boolean
    ) = MdfContentNumberBinding.inflate(layoutInflater, parent, attachToParent)

    override fun initBinding(
        presenter: IMaterialDialogPresenter<*>,
        savedInstanceState: Bundle?
    ) {
        val state =
            MaterialDialogUtil.getViewState<ViewState<T>>(savedInstanceState)

        val inputs = setup.input.getSingles<T>()
        currentValues = ArrayList(state?.values ?: inputs.map { it.value })

        setup.description.display(binding.mdfDescription)
        if (binding.mdfDescription.text.isEmpty()) {
            binding.mdfDescription.visibility = View.GONE
        }

        val layoutInflater = LayoutInflater.from(binding.mdfContainer.context)
        rowBindings.clear()
        inputs.forEachIndexed { index, single ->
            val rowBinding =
                MdfContentNumberRowBinding.inflate(layoutInflater, binding.mdfContainer, true)
            rowBindings.add(rowBinding)

            val repeatListener = RepeatListener(400L, 100L) {
                currentValues[index] = adjust(
                    single.min,
                    single.max,
                    single.step,
                    currentValues[index],
                    it.id == R.id.mdf_increase
                )
                updateDisplayValue(index)
            }
            rowBinding.mdfIncrease.setOnTouchListener(repeatListener)
            rowBinding.mdfDecrease.setOnTouchListener(repeatListener)

            single.hint.display(rowBinding.mdfTextInputLayout) { view, text ->
                view.hint = text
            }
            rowBinding.mdfTextInputEditText.gravity = single.gravity

            rowBinding.mdfTextInputEditText.inputType = when (setup.firstValue()) {
                is Int,
                is Long -> InputType.TYPE_CLASS_NUMBER
                is Float,
                is Double -> InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
                else -> throw RuntimeException("Class ${setup.firstValue()::class} not supported!")
            }
            rowBinding.mdfTextInputEditText.doAfterTextChanged {
                setError(index, "")
            }
            updateDisplayValue(index)
        }
    }

    override fun saveViewState(outState: Bundle) {
        MaterialDialogUtil.saveViewState(outState, ViewState(currentValues))
    }

    // -----------
    // Functions
    // -----------

    @Suppress("UNCHECKED_CAST")
    private fun adjust(
        min: T,
        max: T,
        step: T,
        value: T,
        increase: Boolean
    ): T {
        val newValue = when (value) {
            is Int -> value as Int + (step as Int * (if (increase) 1 else -1))
            is Long -> value as Long + (step as Long * (if (increase) 1L else -1L))
            is Float -> value as Float + (step as Float * (if (increase) 1f else -1f))
            is Double -> value as Double + (step as Double * (if (increase) 1.0 else -1.0))
            else -> throw RuntimeException()
        } as T

        val tooLow = when (newValue) {
            is Int -> (newValue as Int) < (min as Int)
            is Long -> (newValue as Long) < (min as Long)
            is Float -> (newValue as Float) < (min as Float)
            is Double -> (newValue as Double) < (min as Double)
            else -> throw RuntimeException()
        }

        if (tooLow)
            return min

        val tooHigh = when (newValue) {
            is Int -> (newValue as Int) > (max as Int)
            is Long -> (newValue as Long) > (max as Long)
            is Float -> (newValue as Float) > (max as Float)
            is Double -> (newValue as Double) > (max as Double)
            else -> throw RuntimeException()
        }

        if (tooHigh)
            return max

        return newValue
    }

    @Suppress("UNCHECKED_CAST")
    private fun parse(text: String): T {
        return when (setup.firstValue()) {
            is Int -> text.toIntOrNull() ?: 0
            is Long -> text.toLongOrNull() ?: 0L
            is Float -> text.toFloatOrNull() ?: 0f
            is Double -> text.toDoubleOrNull() ?: 0.0
            else -> throw RuntimeException()
        } as T
    }

    private fun updateDisplayValue(index: Int) {
        val input = setup.input.getSingles<T>()[index]
        val currentValue = currentValues[index]
        rowBindings[index].mdfTextInputEditText.setText(
            input.formatter?.format(
                context,
                currentValue
            ) ?: currentValue.toString()
        )
        rowBindings[index].mdfTextInputEditText.clearFocus()
        MaterialDialogUtil.ensureKeyboardCloses(rowBindings[index].mdfTextInputEditText)
    }

    internal fun setError(index: Int, error: String) {
        rowBindings[index].mdfTextInputLayout.error = error.takeIf { it.isNotEmpty() }
    }

    internal fun getCurrentValues(): List<T> {
        return setup.input.getSingles<T>().mapIndexed { index, single ->
            val currentValue = currentValues[index]
            val formatterText = single.formatter?.format(context, currentValue)
                ?: currentValue.toString()
            val input = rowBindings[index].mdfTextInputEditText.text.toString()
            if (input == formatterText) {
                currentValue
            } else {
                val userValue = parse(input)
                userValue
            }
        }
    }

    // -----------
    // State
    // -----------

    @Parcelize
    private class ViewState<T : Number>(
        val values: List<T>
    ) : Parcelable
}