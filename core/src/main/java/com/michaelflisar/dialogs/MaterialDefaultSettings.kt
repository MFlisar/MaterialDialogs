package com.michaelflisar.dialogs

import com.michaelflisar.dialogs.interfaces.IMaterialDialogAnimation
import com.michaelflisar.text.Text

data class MaterialDefaultSettings(
    val buttonPositive: Text = Text.Resource(android.R.string.ok),
    val buttonNegative: Text = Text.Empty,
    val buttonNeutral: Text = Text.Empty,
    val cancelable: Boolean = true,
    val animation: IMaterialDialogAnimation? = null
)