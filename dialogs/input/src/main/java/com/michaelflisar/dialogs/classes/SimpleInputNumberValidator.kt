package com.michaelflisar.dialogs.classes

import android.content.Context
import com.michaelflisar.dialogs.input.R
import com.michaelflisar.dialogs.interfaces.IInputValidator
import kotlinx.parcelize.Parcelize

@Parcelize
class SimpleInputNumberValidator<T>(
    private val min: T,
    private val max: T
) : IInputValidator where T : Number {

    init {
        when (min) {
            is Int,
            is Long,
            is Float,
            is Double -> {
                // OK
            }
            else -> throw RuntimeException("Class ${min::class} not supported!")
        }
    }

    override fun isValid(input: String): Boolean {
        val state = calcState(input, min, max)
        return state == State.Empty || state == State.Ok
    }

    override fun getError(context: Context, input: String): String {
        val state = calcState(input, min, max)
        return when (state) {
            State.TooLow -> context.getString(R.string.mdf_error_inputs_number_min_violated, min.toString())
            State.TooHigh -> context.getString(R.string.mdf_error_inputs_number_max_violated, max.toString())
            State.InvalidNumber -> context.getString(R.string.mdf_error_inputs_number_invalid)
            State.Empty,
            State.Ok -> ""
        }
    }

    fun calcState(input: String, min: T, max: T): State {

        if (input.isEmpty())
            return State.Empty

        val parsed = when (min) {
            is Int -> input.toIntOrNull()
            is Long -> input.toLongOrNull()
            is Float -> input.toFloatOrNull()
            is Double -> input.toDoubleOrNull()
            else -> throw RuntimeException("Class ${min::class} not supported!")
        } ?: return State.InvalidNumber

        return when (min) {
            is Int -> {
                if ((parsed as Int) < (min as Int)) {
                    State.TooLow
                } else if ((parsed as Int) > (max as Int)) {
                    State.TooHigh
                } else State.Ok
            }
            is Long -> {
                if ((parsed as Long) < (min as Long)) {
                    State.TooLow
                } else if ((parsed as Long) > (max as Long)) {
                    State.TooHigh
                } else State.Ok
            }
            is Float -> {
                if ((parsed as Float) < (min as Float)) {
                    State.TooLow
                } else if ((parsed as Float) > (max as Float)) {
                    State.TooHigh
                } else State.Ok
            }
            is Double -> {
                if ((parsed as Double) < (min as Double)) {
                    State.TooLow
                } else if ((parsed as Double) > (max as Double)) {
                    State.TooHigh
                } else State.Ok
            }
            else -> throw RuntimeException("Class ${min::class} not supported!")
        }
    }

    enum class State {
        TooLow,
        TooHigh,
        InvalidNumber,
        Empty,
        Ok
    }
}