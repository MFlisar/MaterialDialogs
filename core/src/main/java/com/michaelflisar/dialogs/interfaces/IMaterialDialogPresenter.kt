package com.michaelflisar.dialogs.interfaces

import androidx.lifecycle.LifecycleOwner
import com.michaelflisar.dialogs.classes.MaterialDialogParent

interface IMaterialDialogPresenter {

    var parent: MaterialDialogParent?
    //var context: Context?
    var lifecycleOwner: LifecycleOwner?

    //fun requireContext(): Context
    fun requireParent(): MaterialDialogParent
    fun requireLifecycleOwner(): LifecycleOwner

    var dismiss: (() -> Unit)?
    var eventCallback: ((IMaterialDialogEvent) -> Unit)?
}