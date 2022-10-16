package com.michaelflisar.dialogs

import com.michaelflisar.dialogs.interfaces.IMaterialDialogAnimation
import com.michaelflisar.text.Text

data class MaterialDefaultSettings(

    // -----------------
    // default values
    // -----------------

    val buttonPositive: Text = Text.Resource(android.R.string.ok),
    val buttonNegative: Text = Text.Empty,
    val buttonNeutral: Text = Text.Empty,
    val cancelable: Boolean = true,
    val animation: IMaterialDialogAnimation? = null,

    // -----------------
    // general settings
    // -----------------

    /*
     * if true, Cancel Events will be send out if a dialog is closed via touch outside or backpress
     */
    val sendCancelEvents: Boolean = true,

    /*
     * if true, menu icons will be tinted and icons will be shown inside the overflow menu
     */
    val tintAndShowMenuIcons: Boolean = true,

    /*
     * if true, a bottomsheet dialog will be started in expanded state
     */
    val expandBottomSheetInitially: Boolean = true
)