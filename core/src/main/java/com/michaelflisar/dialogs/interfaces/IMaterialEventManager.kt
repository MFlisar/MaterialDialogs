package com.michaelflisar.dialogs.interfaces

import androidx.viewbinding.ViewBinding
import com.michaelflisar.dialogs.MaterialDialogSetup
import com.michaelflisar.dialogs.classes.MaterialDialogAction
import com.michaelflisar.dialogs.classes.MaterialDialogButton

interface IMaterialEventManager<S: MaterialDialogSetup<S, B, *>, B: ViewBinding> {
    fun onButton(binding: B, button: MaterialDialogButton): Boolean
    fun onEvent(binding: B, action: MaterialDialogAction)
}