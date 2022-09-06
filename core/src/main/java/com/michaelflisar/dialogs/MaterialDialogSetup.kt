package com.michaelflisar.dialogs

import android.os.Parcelable
import androidx.viewbinding.ViewBinding
import com.michaelflisar.dialogs.classes.Icon
import com.michaelflisar.dialogs.classes.MaterialDialogButton
import com.michaelflisar.dialogs.interfaces.IMaterialDialogAnimation
import com.michaelflisar.dialogs.interfaces.IMaterialDialogEvent
import com.michaelflisar.dialogs.interfaces.IMaterialEventManager
import com.michaelflisar.dialogs.interfaces.IMaterialViewManager
import com.michaelflisar.text.Text
import kotlinx.parcelize.IgnoredOnParcel

abstract class MaterialDialogSetup<S : MaterialDialogSetup<S, B, E>, B : ViewBinding, E: IMaterialDialogEvent> :
    Parcelable {

    // Key
    abstract val id: Int?

    // Header
    abstract val title: Text
    abstract val icon: Icon

    // Buttons
    abstract val buttonPositive: Text
    abstract val buttonNegative: Text
    abstract val buttonNeutral: Text

    // Style
    abstract val cancelable: Boolean

    // Attached Data
    abstract val extra: Parcelable?

    val buttonsData: List<Pair<Text, MaterialDialogButton>> by lazy {
        listOf(
            Pair(buttonPositive, MaterialDialogButton.Positive),
            Pair(buttonNegative, MaterialDialogButton.Negative),
            Pair(buttonNeutral, MaterialDialogButton.Neutral)
        )
    }

    // --------------
    // functions
    // --------------

    abstract val viewManager: IMaterialViewManager<S, B>
    abstract val eventManager: IMaterialEventManager<S, B>

    // attention: this callback must be set AND unset by all presenters to avoid leaks!
    @IgnoredOnParcel
    var dismiss: (() -> Unit)? = null
    @IgnoredOnParcel
    var eventCallback: ((E) -> Unit)? = null

    // --------------
    // functions
    // --------------

    fun getButtonText(button: MaterialDialogButton): Text {
        return when (button) {
            MaterialDialogButton.Positive -> buttonPositive
            MaterialDialogButton.Negative -> buttonNegative
            MaterialDialogButton.Neutral -> buttonNeutral
        }
    }
}