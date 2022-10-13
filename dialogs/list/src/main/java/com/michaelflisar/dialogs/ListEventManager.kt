package com.michaelflisar.dialogs

import com.michaelflisar.dialogs.classes.MaterialDialogButton
import com.michaelflisar.dialogs.interfaces.IListItem
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

    override fun onMenuButton(binding: MdfContentListBinding, menuId: Int) {
        DialogList.Event.Menu(setup.id, setup.extra, menuId).send(setup)
    }

    internal fun sendEvent(
        item: IListItem,
        longPressed: Boolean = false
    ) {
        if (longPressed)
            DialogList.Event.LongPressed(setup.id, setup.extra, item).send(setup)
        else
            DialogList.Event.Result(setup.id, setup.extra, listOf(item), null).send(setup)
    }
}