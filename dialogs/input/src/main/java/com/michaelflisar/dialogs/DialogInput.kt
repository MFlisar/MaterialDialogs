package com.michaelflisar.dialogs

import android.content.Context
import android.os.Parcelable
import android.text.InputType
import com.google.android.material.textfield.TextInputEditText
import com.michaelflisar.dialogs.classes.Icon
import com.michaelflisar.dialogs.classes.MaterialDialogButton
import com.michaelflisar.dialogs.input.R
import com.michaelflisar.dialogs.input.databinding.MdfContentInputBinding
import com.michaelflisar.dialogs.interfaces.IMaterialDialogAnimation
import com.michaelflisar.dialogs.interfaces.IMaterialDialogEvent
import com.michaelflisar.dialogs.interfaces.IMaterialEventManager
import com.michaelflisar.dialogs.interfaces.IMaterialViewManager
import com.michaelflisar.text.Text
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Parcelize
class DialogInput(
    // Key
    override val id: Int?,
    // Header
    override val title: Text,
    override val icon: Icon = Icon.None,
    // specific fields
    val inputType: Int = InputType.TYPE_CLASS_TEXT,
    val initialValue: Text = Text.Empty,
    val hint: Text = Text.Empty,
    val description: Text = Text.Empty,
    val validator: InputValidator = VALIDATOR_ALLOW_ALL,
    // Buttons
    override val buttonPositive: Text = MaterialDialog.defaults.buttonPositive,
    override val buttonNegative: Text = MaterialDialog.defaults.buttonNegative,
    override val buttonNeutral: Text = MaterialDialog.defaults.buttonNeutral,
    // Style
    override val cancelable: Boolean = MaterialDialog.defaults.cancelable,
    // Attached Data
    override val extra: Parcelable? = null
) : MaterialDialogSetup<DialogInput, MdfContentInputBinding, DialogInput.Event>() {

    companion object {
        val VALIDATOR_ALLOW_ALL = SimpleInputValidator(SimpleInputValidator.Mode.AllowAll)
        val VALIDATOR_NON_EMPTY = SimpleInputValidator(SimpleInputValidator.Mode.AllowNonEmptyOnly)
    }

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
            val input: String,
            val button: MaterialDialogButton
        ) : Event()

        data class Cancelled(override val id: Int?, override val extra: Parcelable?) : Event()
    }

    // -----------
    // Interfaces/Classes
    // -----------

    interface InputValidator : Parcelable {
        fun isValid(input: String): Boolean
        fun getError(context: Context, input: String): String
    }

    @Parcelize
    class SimpleInputValidator(
        private val mode: Mode
    ) : InputValidator {

        enum class Mode {
            AllowAll,
            AllowNonEmptyOnly
        }

        override fun isValid(input: String) = when (mode) {
            Mode.AllowAll -> true
            Mode.AllowNonEmptyOnly -> input.isNotEmpty()
        }

        override fun getError(context: Context, input: String) = when (mode) {
            Mode.AllowAll -> ""
            Mode.AllowNonEmptyOnly -> context.getString(R.string.mdf_error_only_non_empty_inputs_allowed)
        }
    }

    @Parcelize
    class ViewState(
        val input: String,
        val selectionStart: Int,
        val selectionEnd: Int
    ) : Parcelable {
        constructor(textInputEditText: TextInputEditText) : this(
            textInputEditText.text?.toString() ?: "",
            textInputEditText.selectionStart,
            textInputEditText.selectionEnd
        )
    }
}