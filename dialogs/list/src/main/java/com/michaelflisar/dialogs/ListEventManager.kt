package com.michaelflisar.dialogs

import com.michaelflisar.dialogs.classes.MaterialDialogAction
import com.michaelflisar.dialogs.classes.MaterialDialogButton
import com.michaelflisar.dialogs.interfaces.IListItem
import com.michaelflisar.dialogs.interfaces.IMaterialDialogPresenter
import com.michaelflisar.dialogs.interfaces.IMaterialEventManager
import com.michaelflisar.dialogs.list.databinding.MdfContentListBinding

internal class ListEventManager(
    private val setup: DialogList
) : IMaterialEventManager<DialogList, MdfContentListBinding> {

    override fun onEvent(
        presenter: IMaterialDialogPresenter<DialogList, MdfContentListBinding, *>,
        action: MaterialDialogAction
    ) {
        DialogList.Event.Action(setup.id, setup.extra, action).send(presenter)
    }

    override fun onButton(
        presenter: IMaterialDialogPresenter<DialogList, MdfContentListBinding, *>,
        button: MaterialDialogButton
    ): Boolean {
        val viewManager = setup.viewManager as ListViewManager
        val selectedItems = viewManager.getSelectedItemsForResult()
        DialogList.Event.Result(setup.id, setup.extra, selectedItems, button).send(presenter)
        return true
    }

    internal fun sendEvent(
        presenter: IMaterialDialogPresenter<*, *, *>,
        item: IListItem,
        longPressed: Boolean = false
    ) {
        if (longPressed)
            DialogList.Event.LongPressed(setup.id, setup.extra, item).send(presenter)
        else
            DialogList.Event.Result(setup.id, setup.extra, listOf(item), null).send(presenter)
    }
}