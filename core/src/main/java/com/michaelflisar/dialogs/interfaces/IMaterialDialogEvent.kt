package com.michaelflisar.dialogs.interfaces

import android.os.Parcelable
import androidx.viewbinding.ViewBinding
import com.michaelflisar.dialogs.MaterialDialog
import com.michaelflisar.dialogs.MaterialDialogSetup
import com.michaelflisar.dialogs.classes.MaterialDialogAction

interface IMaterialDialogEvent {

    interface Action: IMaterialDialogEvent {
        val data: MaterialDialogAction
    }

    val id: Int?
    val extra: Parcelable?

    fun <S : MaterialDialogSetup<S, B, E>, B : ViewBinding, E : IMaterialDialogEvent> send(setup: MaterialDialogSetup<S, B, E>) {
        setup.eventCallback?.invoke(this as E)
        MaterialDialog.sendEvent(this)
    }
}