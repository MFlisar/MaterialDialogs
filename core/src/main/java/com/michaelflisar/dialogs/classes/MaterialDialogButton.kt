package com.michaelflisar.dialogs.classes

import androidx.appcompat.app.AlertDialog

sealed class MaterialDialogButton {

    abstract val alertButton: Int

    object Positive : MaterialDialogButton() {
        override val alertButton: Int = AlertDialog.BUTTON_POSITIVE
    }

    object Negative : MaterialDialogButton() {
        override val alertButton: Int = AlertDialog.BUTTON_NEGATIVE
    }

    object Neutral : MaterialDialogButton() {
        override val alertButton: Int = AlertDialog.BUTTON_NEUTRAL
    }

}