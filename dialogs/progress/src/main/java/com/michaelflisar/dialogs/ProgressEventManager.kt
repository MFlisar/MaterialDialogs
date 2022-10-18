package com.michaelflisar.dialogs

import com.michaelflisar.dialogs.classes.MaterialDialogAction
import com.michaelflisar.dialogs.classes.MaterialDialogButton
import com.michaelflisar.dialogs.interfaces.IMaterialDialogPresenter
import com.michaelflisar.dialogs.interfaces.IMaterialEventManager
import com.michaelflisar.dialogs.progress.databinding.MdfContentProgressBinding

internal class ProgressEventManager(
    private val setup: DialogProgress
) : IMaterialEventManager<DialogProgress, MdfContentProgressBinding> {

    override fun onButton(
        presenter: IMaterialDialogPresenter<DialogProgress, MdfContentProgressBinding, *>,
        button: MaterialDialogButton
    ): Boolean {
        DialogProgress.Event.Result(setup.id, setup.extra, button).send(presenter)
        return when (button) {
            MaterialDialogButton.Negative -> setup.dismissOnNegative
            MaterialDialogButton.Neutral -> setup.dismissOnNeutral
            MaterialDialogButton.Positive -> setup.dismissOnPositive
        }
    }

    override fun onEvent(
        presenter: IMaterialDialogPresenter<DialogProgress, MdfContentProgressBinding, *>,
        action: MaterialDialogAction
    ) {
        DialogProgress.Event.Action(setup.id, setup.extra, action).send(presenter)
    }
}