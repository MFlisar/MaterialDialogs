package com.michaelflisar.dialogs

import android.os.Parcelable
import com.michaelflisar.dialogs.classes.*
import com.michaelflisar.dialogs.gdpr.R
import com.michaelflisar.dialogs.gdpr.databinding.MdfContentGdprBinding
import com.michaelflisar.dialogs.interfaces.IGDPRPrefs
import com.michaelflisar.dialogs.interfaces.IMaterialDialogEvent
import com.michaelflisar.dialogs.interfaces.IMaterialEventManager
import com.michaelflisar.dialogs.interfaces.IMaterialViewManager
import com.michaelflisar.text.Text
import com.michaelflisar.text.asText
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Parcelize
class DialogGDPR(
    // Key
    override val id: Int? = null,
    // Header
    override val title: Text = R.string.gdpr_dialog_title.asText(),
    override val icon: Icon = Icon.None,
    // specific fields
    val setup: GDPRSetup,
    // Buttons
    // Style
    override val cancelable: Boolean = MaterialDialog.defaults.cancelable,
    // Attached Data
    override val extra: Parcelable? = null
) : MaterialDialogSetup<DialogGDPR>() {

    @IgnoredOnParcel
    override val buttonPositive: Text = Text.Empty

    @IgnoredOnParcel
    override val buttonNegative: Text = Text.Empty

    @IgnoredOnParcel
    override val buttonNeutral: Text = Text.Empty

    @IgnoredOnParcel
    override val menu: Int? = null

    @IgnoredOnParcel
    override val viewManager: IMaterialViewManager<DialogGDPR, MdfContentGdprBinding> =
        GDPRViewManager(this)

    @IgnoredOnParcel
    override val eventManager: IMaterialEventManager<DialogGDPR> =
        GDPREventManager(this)

    // -----------
    // Result Events
    // -----------

    sealed class Event : IMaterialDialogEvent {

        data class Result(
            override val id: Int?,
            override val extra: Parcelable?,
            val consent: GDPRConsentState
        ) : Event()

        data class Action(
            override val id: Int?,
            override val extra: Parcelable?,
            override val data: MaterialDialogAction
        ) : Event(), IMaterialDialogEvent.Action
    }
}