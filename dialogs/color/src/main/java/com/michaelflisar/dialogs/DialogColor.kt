package com.michaelflisar.dialogs

import android.graphics.Color
import android.os.Parcelable
import com.michaelflisar.dialogs.classes.Icon
import com.michaelflisar.dialogs.classes.MaterialDialogButton
import com.michaelflisar.dialogs.color.databinding.MdfContentColorBinding
import com.michaelflisar.dialogs.interfaces.IMaterialDialogEvent
import com.michaelflisar.dialogs.interfaces.IMaterialEventManager
import com.michaelflisar.dialogs.interfaces.IMaterialViewManager
import com.michaelflisar.text.Text
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Parcelize
class DialogColor(
    // Key
    override val id: Int?,
    // Header
    override val title: Text,
    override val icon: Icon = Icon.None,
    // specific fields
    val color: Int = Color.BLACK,
    val alphaAllowed: Boolean = false,
    val moveToCustomPageOnPickerSelection: Boolean = false,
    val updateCustomColorOnPickerSelection: Boolean = true,
    // Buttons
    override val buttonPositive: Text = MaterialDialog.defaults.buttonPositive,
    override val buttonNegative: Text = MaterialDialog.defaults.buttonNegative,
    override val buttonNeutral: Text = MaterialDialog.defaults.buttonNeutral,
    // Style
    override val cancelable: Boolean = MaterialDialog.defaults.cancelable,
    // Attached Data
    override val extra: Parcelable? = null
) : MaterialDialogSetup<DialogColor, MdfContentColorBinding, DialogColor.Event>() {

    @IgnoredOnParcel
    override val viewManager: IMaterialViewManager<DialogColor, MdfContentColorBinding> =
        ColorViewManager(this)

    @IgnoredOnParcel
    override val eventManager: IMaterialEventManager<DialogColor, MdfContentColorBinding> =
        ColorEventManager(this)

    // -----------
    // Result Events
    // -----------

    sealed class Event : IMaterialDialogEvent {
        data class Result(
            override val id: Int?,
            override val extra: Parcelable?,
            val color: Int,
            val button: MaterialDialogButton
        ) : Event()

        data class Cancelled(override val id: Int?, override val extra: Parcelable?) : Event()
    }
}