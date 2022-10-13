package com.michaelflisar.dialogs.classes

import android.view.MenuItem
import androidx.appcompat.app.AlertDialog

sealed class MaterialDialogButton {

    sealed interface DialogButton {
        val alertButton: Int
    }

    object Positive : MaterialDialogButton(), DialogButton {
        override val alertButton: Int = AlertDialog.BUTTON_POSITIVE
    }
    object Negative : MaterialDialogButton(), DialogButton {
        override val alertButton: Int = AlertDialog.BUTTON_NEGATIVE
    }
    object Neutral : MaterialDialogButton(), DialogButton {
        override val alertButton: Int = AlertDialog.BUTTON_NEUTRAL
    }

    class Menu(val menu: Int) : MaterialDialogButton()
}