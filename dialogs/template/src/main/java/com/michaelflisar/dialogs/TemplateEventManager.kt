package com.michaelflisar.dialogs

import com.michaelflisar.dialogs.classes.MaterialDialogAction
import com.michaelflisar.dialogs.classes.MaterialDialogButton
import com.michaelflisar.dialogs.interfaces.IMaterialDialogPresenter
import com.michaelflisar.dialogs.interfaces.IMaterialEventManager

internal class TemplateEventManager(
    private val setup: DialogTemplate
) : IMaterialEventManager<DialogTemplate> {

    override fun onEvent(
        presenter: IMaterialDialogPresenter<DialogTemplate>,
        action: MaterialDialogAction
    ) {
        DialogTemplate.Event.Action(setup.id, setup.extra, action).send(presenter)
    }

    override fun onButton(
        presenter: IMaterialDialogPresenter<DialogTemplate>,
        button: MaterialDialogButton
    ): Boolean {
        DialogTemplate.Event.Result(setup.id, setup.extra, button).send(presenter)
        return true
    }
}