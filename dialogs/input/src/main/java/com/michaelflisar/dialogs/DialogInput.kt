package com.michaelflisar.dialogs

import android.content.Context
import android.os.Parcelable
import android.text.InputType
import com.michaelflisar.dialogs.classes.Icon
import com.michaelflisar.dialogs.classes.MaterialDialogAction
import com.michaelflisar.dialogs.classes.MaterialDialogButton
import com.michaelflisar.dialogs.input.R
import com.michaelflisar.dialogs.input.databinding.MdfContentInputBinding
import com.michaelflisar.dialogs.interfaces.IInputValidator
import com.michaelflisar.dialogs.interfaces.IMaterialDialogEvent
import com.michaelflisar.dialogs.interfaces.IMaterialEventManager
import com.michaelflisar.dialogs.interfaces.IMaterialViewManager
import com.michaelflisar.text.Text
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Parcelize
class DialogInput(
    // Key
    override val id: Int? = null,
    // Header
    override val title: Text = Text.Empty,
    override val icon: Icon = Icon.None,
    override val menu: Int? = null,
    // specific fields
    val description: Text = Text.Empty,
    val input: Input,
    val selectAllOnFocus: Boolean = false,
    // Buttons
    override val buttonPositive: Text = MaterialDialog.defaults.buttonPositive,
    override val buttonNegative: Text = MaterialDialog.defaults.buttonNegative,
    override val buttonNeutral: Text = MaterialDialog.defaults.buttonNeutral,
    // Style
    override val cancelable: Boolean = MaterialDialog.defaults.cancelable,
    // Attached Data
    override val extra: Parcelable? = null
) : MaterialDialogSetup<DialogInput, MdfContentInputBinding, DialogInput.Event>() {

    @IgnoredOnParcel
    override val viewManager: IMaterialViewManager<DialogInput, MdfContentInputBinding> =
        InputViewManager(this)

    @IgnoredOnParcel
    override val eventManager: IMaterialEventManager<DialogInput, MdfContentInputBinding> =
        InputEventManager(this)

    // -----------
    // Result Events
    // -----------

    sealed class Event : IMaterialDialogEvent {

        data class Result(
            override val id: Int?,
            override val extra: Parcelable?,
            val inputs: List<String>,
            val button: MaterialDialogButton
        ) : Event() {
            val input: String
                get() = inputs[0]
        }

        data class Action(
            override val id: Int?,
            override val extra: Parcelable?,
            override val data: MaterialDialogAction
        ) : Event(), IMaterialDialogEvent.Action

    }

    // -----------
    // Enums/Classes
    // -----------

    sealed class Input : Parcelable {
        @Parcelize
        class Single(
            val inputType: Int = InputType.TYPE_CLASS_TEXT,
            val value: Text = Text.Empty,
            val hint: Text = Text.Empty,
            val validator: IInputValidator = TextValidator(),
            val prefix: Text = Text.Empty,
            val suffix: Text = Text.Empty
        ) : Input()

        @Parcelize
        class Multi(
            val singles: List<Single>
        ) : Input()

        internal fun getSingles(): List<Single> {
            return when (this) {
                is Multi -> singles
                is Single -> listOf(this)
            }
        }
    }

    @Parcelize
    class NumberValidator<T>(
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
            return state == State.Ok
        }

        override fun getError(context: Context, input: String): String {
            val state = calcState(input, min, max)
            return when (state) {
                State.TooLow -> context.getString(
                    R.string.mdf_error_inputs_number_min_violated,
                    min.toString()
                )
                State.TooHigh -> context.getString(
                    R.string.mdf_error_inputs_number_max_violated,
                    max.toString()
                )
                State.InvalidNumber,
                State.Empty -> context.getString(R.string.mdf_error_inputs_number_invalid)
                State.Ok -> ""
            }
        }

        private fun calcState(input: String, min: T, max: T): State {

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

        private enum class State {
            TooLow,
            TooHigh,
            InvalidNumber,
            Empty,
            Ok
        }
    }

    @Parcelize
    class TextValidator(
        private val minLength: Int? = null,
        private val maxLength: Int? = null
    ) : IInputValidator {

        override fun isValid(input: String): Boolean {
            return (minLength == null || input.length >= minLength) &&
                    (maxLength == null || input.length <= maxLength)
        }

        override fun getError(context: Context, input: String): String {
            return if (minLength != null && maxLength != null)
                context.getString(
                    R.string.mdf_error_inputs_length_rule_violated,
                    minLength,
                    maxLength
                )
            else if (minLength != null) {
                context.getString(R.string.mdf_error_inputs_length_rule_min_violated, minLength)
            } else if (maxLength != null) {
                context.getString(R.string.mdf_error_inputs_length_rule_max_violated, maxLength!!)
            } else "" // can never happen, isValid would always return true in this case
        }
    }
}