package com.michaelflisar.dialogs

import com.michaelflisar.dialogs.classes.MaterialDialogAction
import com.michaelflisar.dialogs.classes.MaterialDialogButton
import com.michaelflisar.dialogs.interfaces.IMaterialDialogPresenter
import com.michaelflisar.dialogs.interfaces.IMaterialEventManager

internal class AdsEventManager(
    private val setup: DialogAds
) : IMaterialEventManager<DialogAds> {

    override fun onEvent(
        presenter: IMaterialDialogPresenter<DialogAds>,
        action: MaterialDialogAction
    ) {
        DialogAds.Event.Action(setup.id, setup.extra, action).send(presenter)
    }

    override fun onButton(
        presenter: IMaterialDialogPresenter<DialogAds>,
        button: MaterialDialogButton
    ): Boolean {
        val viewManager = setup.viewManager as AdsViewManager
        val data = viewManager.getStateForButtonClick()
        sendEvent(presenter, data)
        return true
    }

    internal fun sendEvent(
        presenter: IMaterialDialogPresenter<*>,
        data: DialogAds.Event.Result.Data
    ) {
        DialogAds.Event.Result(setup.id, setup.extra, data).send(presenter)
    }
}