package com.michaelflisar.dialogs

import com.michaelflisar.dialogs.classes.*
import com.michaelflisar.dialogs.interfaces.IMaterialDialogPresenter
import com.michaelflisar.dialogs.interfaces.IMaterialEventManager

internal class GDPREventManager(
    private val setup: DialogGDPR
) : IMaterialEventManager<DialogGDPR> {

    override fun onEvent(
        presenter: IMaterialDialogPresenter<DialogGDPR>,
        action: MaterialDialogAction
    ) {
        DialogGDPR.Event.Action(setup.id, setup.extra, action).send(presenter)
    }

    override fun onButton(
        presenter: IMaterialDialogPresenter<DialogGDPR>,
        button: MaterialDialogButton
    ): Boolean {
        // there are no button events!
        //DialogGDPR.Event.Result(setup.id, setup.extra, null).send(presenter)
        return true
    }

    fun sendEvent(
        presenter: IMaterialDialogPresenter<DialogGDPR>,
        state: GDPRConsentState
    ) {
        DialogGDPR.Event.Result(setup.id, setup.extra, state).send(presenter)
    }
}