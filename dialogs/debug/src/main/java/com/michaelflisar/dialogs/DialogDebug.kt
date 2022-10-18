package com.michaelflisar.dialogs

import android.os.Parcelable
import com.michaelflisar.dialogs.classes.*
import com.michaelflisar.dialogs.debug.R
import com.michaelflisar.dialogs.debug.databinding.MdfContentDebugBinding
import com.michaelflisar.dialogs.interfaces.IMaterialDialogEvent
import com.michaelflisar.dialogs.interfaces.IMaterialDialogPresenter
import com.michaelflisar.dialogs.interfaces.IMaterialEventManager
import com.michaelflisar.dialogs.interfaces.IMaterialViewManager
import com.michaelflisar.text.Text
import com.michaelflisar.text.asText
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Parcelize
class DialogDebug(
    // Key
    // Header
    override val title: Text = R.string.mdf_title_debug_dialog.asText(),
    override val icon: Icon = Icon.None,
    // specific fields
    val manager: DebugDataManager,
    val withNumbering: Boolean = false,
    val backButton: Text = R.string.mdf_button_back.asText(),
    // Buttons
    // ---
    // Style
    override val cancelable: Boolean = MaterialDialog.defaults.cancelable
) : MaterialDialogSetup<DialogDebug, MdfContentDebugBinding>() {

    @IgnoredOnParcel
    override val menu: Int? = null

    @IgnoredOnParcel
    override val id: Int? = null

    @IgnoredOnParcel
    override val extra: Parcelable? = null

    @IgnoredOnParcel
    override val buttonPositive: Text = Text.Empty

    @IgnoredOnParcel
    override val buttonNeutral: Text = Text.Empty

    override val buttonNegative: Text
        get() = backButton

    @IgnoredOnParcel
    override val viewManager: IMaterialViewManager<DialogDebug, MdfContentDebugBinding> =
        DebugViewManager(this)

    @IgnoredOnParcel
    override val eventManager: IMaterialEventManager<DialogDebug> =
        DebugEventManager(this)

    // -----------
    // Result Events
    // -----------

    sealed class Event : IMaterialDialogEvent {

        data class Result(
            override val id: Int?,
            override val extra: Parcelable?,
            val item: DebugItem<*>? = null,
            val index: Int? = null,
            val button: MaterialDialogButton? = null
        ) : Event()

        data class Action(
            override val id: Int?,
            override val extra: Parcelable?,
            override val data: MaterialDialogAction
        ) : Event(), IMaterialDialogEvent.Action

        data class NotifyDataSetChanged(
            override val id: Int?,
            override val extra: Parcelable?
        ) : Event()
    }
}