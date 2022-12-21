package com.michaelflisar.dialogs.interfaces

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleOwner
import com.michaelflisar.dialogs.MaterialDialogSetup
import com.michaelflisar.dialogs.MaterialDialogUtil
import com.michaelflisar.dialogs.classes.MaterialDialogButton
import com.michaelflisar.dialogs.classes.MaterialDialogParent

interface IMaterialDialogPresenter<S : MaterialDialogSetup<S>> {

    val setup: S

    var parent: MaterialDialogParent?
    var lifecycleOwner: LifecycleOwner?

    fun requireParent(): MaterialDialogParent
    fun requireLifecycleOwner(): LifecycleOwner

    fun requireContext(): Context = requireParent().context

    // functions that need to be initialised as soon as possible by a presenter
    var dismiss: (() -> Unit)?
    var eventCallback: ((IMaterialDialogEvent) -> Unit)?

    fun setButtonEnabled(button: MaterialDialogButton, enabled: Boolean)
    fun setButtonVisible(button: MaterialDialogButton, visible: Boolean)
    fun setButtonText(button: MaterialDialogButton, text: CharSequence)
}