package com.michaelflisar.dialogs.classes

import androidx.appcompat.app.AlertDialog

sealed class MaterialDialogButton {

    abstract val alertButton: Int

    object Positive : MaterialDialogButton() {
        override val alertButton: Int = AlertDialog.BUTTON_POSITIVE
        override fun toString(): String = "MaterialDialogButton::Positive"
    }

    object Negative : MaterialDialogButton() {
        override val alertButton: Int = AlertDialog.BUTTON_NEGATIVE
        override fun toString(): String = "MaterialDialogButton::Negative"
    }

    object Neutral : MaterialDialogButton() {
        override val alertButton: Int = AlertDialog.BUTTON_NEUTRAL
        override fun toString(): String = "MaterialDialogButton::Neutral"
    }

}