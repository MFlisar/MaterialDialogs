package com.michaelflisar.dialogs

import android.os.Parcelable
import android.view.Gravity
import com.michaelflisar.dialogs.classes.Icon
import com.michaelflisar.dialogs.classes.InstanceManager
import com.michaelflisar.dialogs.classes.MaterialDialogAction
import com.michaelflisar.dialogs.classes.MaterialDialogButton
import com.michaelflisar.dialogs.interfaces.IMaterialDialogEvent
import com.michaelflisar.dialogs.interfaces.IMaterialDialogPresenter
import com.michaelflisar.dialogs.interfaces.IMaterialEventManager
import com.michaelflisar.dialogs.interfaces.IMaterialViewManager
import com.michaelflisar.dialogs.progress.databinding.MdfContentProgressBinding
import com.michaelflisar.text.Text
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Parcelize
class DialogProgress(
    // Key
    override val id: Int,
    // Header
    override val title: Text = Text.Empty,
    override val icon: Icon = Icon.None,
    // specific fields
    val text: Text,
    val textGravity: Int = Gravity.CENTER_HORIZONTAL,
    val indeterminate: Boolean = true,
    val horizontal: Boolean = true,
    val dismissOnNegative: Boolean = false,
    val dismissOnPositive: Boolean = false,
    val dismissOnNeutral: Boolean = false,
    // Buttons
    override val buttonPositive: Text = Text.Empty,
    override val buttonNegative: Text = Text.Empty,
    override val buttonNeutral: Text = Text.Empty,
    // Style
    override val cancelable: Boolean = false
) : MaterialDialogSetup<DialogProgress>() {

    companion object {

        fun update(id: Int, text: Text) {
            InstanceManager.get(id)?.let {
                (it.setup.viewManager as ProgressViewManager).updateText(text)
            }
        }

        fun updateProgress(id: Int, progress: Int) {
            InstanceManager.get(id)?.let {
                (it.setup.viewManager as ProgressViewManager).updateProgress(progress)
            }
        }

        fun dismiss(id: Int) {
            InstanceManager.dismiss(id)
        }
    }

    @IgnoredOnParcel
    override val menu: Int? = null

    @IgnoredOnParcel
    override val extra: Parcelable? = null

    @IgnoredOnParcel
    override val viewManager: IMaterialViewManager<DialogProgress, MdfContentProgressBinding> =
        ProgressViewManager(this)

    @IgnoredOnParcel
    override val eventManager: IMaterialEventManager<DialogProgress> =
        ProgressEventManager(this)

    // -----------
    // Result Events
    // -----------

    sealed class Event : IMaterialDialogEvent {

        data class Result(
            override val id: Int?,
            override val extra: Parcelable?,
            val button: MaterialDialogButton? = null
        ) : Event()

        data class Action(
            override val id: Int?,
            override val extra: Parcelable?,
            override val data: MaterialDialogAction
        ) : Event(), IMaterialDialogEvent.Action
    }
}