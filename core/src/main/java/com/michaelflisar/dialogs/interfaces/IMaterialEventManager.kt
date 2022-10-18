package com.michaelflisar.dialogs.interfaces

import androidx.viewbinding.ViewBinding
import com.michaelflisar.dialogs.MaterialDialogSetup
import com.michaelflisar.dialogs.classes.MaterialDialogAction
import com.michaelflisar.dialogs.classes.MaterialDialogButton

interface IMaterialEventManager<S: MaterialDialogSetup<S, *>> {
    fun onButton(presenter: IMaterialDialogPresenter<S, *>, button: MaterialDialogButton): Boolean
    fun onEvent(presenter: IMaterialDialogPresenter<S, *>, action: MaterialDialogAction)
}