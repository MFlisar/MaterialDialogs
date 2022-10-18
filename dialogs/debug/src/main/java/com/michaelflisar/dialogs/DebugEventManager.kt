package com.michaelflisar.dialogs

import com.michaelflisar.dialogs.classes.MaterialDialogAction
import com.michaelflisar.dialogs.classes.MaterialDialogButton
import com.michaelflisar.dialogs.debug.databinding.MdfContentDebugBinding
import com.michaelflisar.dialogs.interfaces.IMaterialDialogPresenter
import com.michaelflisar.dialogs.interfaces.IMaterialEventManager

internal class DebugEventManager(
    private val setup: DialogDebug
) : IMaterialEventManager<DialogDebug, MdfContentDebugBinding> {

    override fun onButton(
        presenter: IMaterialDialogPresenter<DialogDebug, MdfContentDebugBinding, *>,
        button: MaterialDialogButton
    ): Boolean {
        DialogDebug.Event.Result(setup.id, setup.extra, button = button).send(presenter)
        // closing is manually handled by observing the events inside the view manager!
        return false
    }

    override fun onEvent(
        presenter: IMaterialDialogPresenter<DialogDebug, MdfContentDebugBinding, *>,
        action: MaterialDialogAction
    ) {
        DialogDebug.Event.Action(setup.id, setup.extra, action).send(presenter)
    }
}