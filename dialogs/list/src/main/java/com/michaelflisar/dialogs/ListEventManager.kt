package com.michaelflisar.dialogs

import com.michaelflisar.dialogs.classes.MaterialDialogButton
import com.michaelflisar.dialogs.interfaces.IMaterialEventManager
import com.michaelflisar.dialogs.list.databinding.MdfContentListBinding

internal class ListEventManager(
    private val setup: DialogList
) : IMaterialEventManager<DialogList, MdfContentListBinding> {

    override fun onCancelled() {
        DialogList.Event.Cancelled(setup.id, setup.extra).send(setup)
    }

    override fun onButton(
        binding: MdfContentListBinding,
        button: MaterialDialogButton
    ): Boolean {
        val viewManager = setup.viewManager as ListViewManager
        val selectedItems = viewManager.getSelectedItemsForResult()
        DialogList.Event.Result(setup.id, setup.extra, selectedItems, button).send(setup)
        return true
    }

    internal fun sendEvent(
        item: DialogList.ListItem
    ) {
        DialogList.Event.Result(setup.id, setup.extra, listOf(item), null).send(setup)
    }
}