package com.michaelflisar.dialogs

import com.michaelflisar.dialogs.classes.MaterialDialogAction
import com.michaelflisar.dialogs.classes.MaterialDialogButton
import com.michaelflisar.dialogs.info.databinding.MdfContentInfoBinding
import com.michaelflisar.dialogs.interfaces.IMaterialDialogPresenter
import com.michaelflisar.dialogs.interfaces.IMaterialEventManager

internal class InfoEventManager(
    private val setup: DialogInfo
) : IMaterialEventManager<DialogInfo, MdfContentInfoBinding> {

    override fun onEvent(
        presenter: IMaterialDialogPresenter<DialogInfo, MdfContentInfoBinding, *>,
        action: MaterialDialogAction
    ) {
        DialogInfo.Event.Action(setup.id, setup.extra, action).send(presenter)
    }

    override fun onButton(
        presenter: IMaterialDialogPresenter<DialogInfo, MdfContentInfoBinding, *>,
        button: MaterialDialogButton
    ): Boolean {
        DialogInfo.Event.Result(setup.id, setup.extra, button).send(presenter)
        return true
    }
}