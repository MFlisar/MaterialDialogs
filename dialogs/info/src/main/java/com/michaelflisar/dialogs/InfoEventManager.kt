package com.michaelflisar.dialogs

import com.michaelflisar.dialogs.classes.MaterialDialogButton
import com.michaelflisar.dialogs.info.databinding.MdfContentInfoBinding
import com.michaelflisar.dialogs.interfaces.IMaterialEventManager

internal class InfoEventManager(
    private val setup: DialogInfo
) : IMaterialEventManager<DialogInfo, MdfContentInfoBinding> {

    override fun onCancelled() {
        DialogInfo.Event.Cancelled(setup.id, setup.extra).send(setup)
    }

    override fun onButton(
        binding: MdfContentInfoBinding,
        button: MaterialDialogButton
    ): Boolean {
        DialogInfo.Event.Result(setup.id, setup.extra, button).send(setup)
        return true
    }

    override fun onMenuButton(binding: MdfContentInfoBinding, menuId: Int) {
        DialogInfo.Event.Menu(setup.id, setup.extra, menuId).send(setup)
    }

}