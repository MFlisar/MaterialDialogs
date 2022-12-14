package com.michaelflisar.dialogs.interfaces

import android.os.Parcelable
import com.michaelflisar.dialogs.MaterialDialog
import com.michaelflisar.dialogs.MaterialDialogSetup
import com.michaelflisar.dialogs.classes.MaterialDialogAction

interface IMaterialDialogEvent {

    interface Action : IMaterialDialogEvent {
        val data: MaterialDialogAction
    }

    val id: Int?
    val extra: Parcelable?

    fun <S: MaterialDialogSetup<S>> send(presenter: IMaterialDialogPresenter<S>) {
        presenter.eventCallback?.invoke(this)
        MaterialDialog.sendEvent(presenter, this)
    }
}