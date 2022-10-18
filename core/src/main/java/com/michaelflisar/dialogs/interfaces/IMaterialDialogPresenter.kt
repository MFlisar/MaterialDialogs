package com.michaelflisar.dialogs.interfaces

import android.content.Context
import androidx.lifecycle.LifecycleOwner
import androidx.viewbinding.ViewBinding
import com.michaelflisar.dialogs.MaterialDialogSetup
import com.michaelflisar.dialogs.classes.MaterialDialogParent

interface IMaterialDialogPresenter<S : MaterialDialogSetup<S, B, E>, B : ViewBinding, E : IMaterialDialogEvent> {

    val setup: S

    var parent: MaterialDialogParent?
    var lifecycleOwner: LifecycleOwner?

    fun requireParent(): MaterialDialogParent
    fun requireLifecycleOwner(): LifecycleOwner

    fun requireContext(): Context = requireParent().context

    // functions that need to be initialised as soon as possible by a presenter
    var dismiss: (() -> Unit)?
    var eventCallback: ((IMaterialDialogEvent) -> Unit)?
}