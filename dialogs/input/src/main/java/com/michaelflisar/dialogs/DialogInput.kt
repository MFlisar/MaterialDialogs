package com.michaelflisar.dialogs

import android.os.Parcelable
import android.text.InputType
import com.michaelflisar.dialogs.classes.Icon
import com.michaelflisar.dialogs.classes.MaterialDialogButton
import com.michaelflisar.dialogs.classes.SimpleInputValidator
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
    override val id: Int?,
    // Header
    override val title: Text,
    override val icon: Icon = Icon.None,
    // specific fields
    val inputType: Int = InputType.TYPE_CLASS_TEXT,
    val initialValue: Text = Text.Empty,
    val hint: Text = Text.Empty,
    val description: Text = Text.Empty,
    val validator: IInputValidator = createSimpleValidator(),
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
        fun createSimpleValidator(minLength: Int? = null, maxLength: Int? = null): IInputValidator =
            SimpleInputValidator(minLength, maxLength)
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
}