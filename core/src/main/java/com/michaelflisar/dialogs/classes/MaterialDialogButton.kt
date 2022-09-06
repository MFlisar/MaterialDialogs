package com.michaelflisar.dialogs.classes

import androidx.appcompat.app.AlertDialog

enum class MaterialDialogButton(val alertButton: Int) {
    Positive(AlertDialog.BUTTON_POSITIVE),
    Negative(AlertDialog.BUTTON_NEGATIVE),
    Neutral(AlertDialog.BUTTON_NEUTRAL)
}