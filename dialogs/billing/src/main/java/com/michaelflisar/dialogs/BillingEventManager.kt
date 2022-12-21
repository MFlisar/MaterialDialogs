package com.michaelflisar.dialogs

import com.michaelflisar.dialogs.classes.MaterialDialogAction
import com.michaelflisar.dialogs.classes.MaterialDialogButton
import com.michaelflisar.dialogs.interfaces.IMaterialDialogPresenter
import com.michaelflisar.dialogs.interfaces.IMaterialEventManager

internal class BillingEventManager(
    private val setup: DialogBilling
) : IMaterialEventManager<DialogBilling> {

    override fun onEvent(
        presenter: IMaterialDialogPresenter<DialogBilling>,
        action: MaterialDialogAction
    ) {
        DialogBilling.Event.Action(setup.id, setup.extra, action).send(presenter)
    }

    override fun onButton(
        presenter: IMaterialDialogPresenter<DialogBilling>,
        button: MaterialDialogButton
    ): Boolean {
        DialogBilling.Event.Result(setup.id, setup.extra, button).send(presenter)
        return true
    }
}