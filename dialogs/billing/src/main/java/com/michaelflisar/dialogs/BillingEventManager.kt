package com.michaelflisar.dialogs

import com.michaelflisar.dialogs.classes.MaterialDialogAction
import com.michaelflisar.dialogs.classes.MaterialDialogButton
import com.michaelflisar.dialogs.interfaces.IMaterialDialogPresenter
import com.michaelflisar.dialogs.interfaces.IMaterialEventManager
import com.michaelflisar.kotbilling.results.IKBPurchaseResult

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
        if (button == MaterialDialogButton.Positive) {
            // user wants to buy single product
            val viewManager = setup.viewManager as BillingViewManager
            viewManager.buyFirstProduct()
            return false
        }
        DialogBilling.Event.Result(setup.id, setup.extra, button, null).send(presenter)
        return true
    }

    fun sendEvent(presenter: IMaterialDialogPresenter<DialogBilling>, purchase: IKBPurchaseResult) {
        DialogBilling.Event.Result(setup.id, setup.extra, null, purchase).send(presenter)
    }
}