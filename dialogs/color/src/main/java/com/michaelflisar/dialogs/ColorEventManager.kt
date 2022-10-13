package com.michaelflisar.dialogs

import com.michaelflisar.dialogs.classes.MaterialDialogButton
import com.michaelflisar.dialogs.color.databinding.MdfContentColorBinding
import com.michaelflisar.dialogs.interfaces.IMaterialEventManager

internal class ColorEventManager(
    private val setup: DialogColor
) : IMaterialEventManager<DialogColor, MdfContentColorBinding> {

    override fun onCancelled() {
        DialogColor.Event.Cancelled(setup.id, setup.extra).send(setup)
    }

    override fun onButton(
        binding: MdfContentColorBinding,
        button: MaterialDialogButton
    ): Boolean {
        val viewManager = setup.viewManager as ColorViewManager
        val color = viewManager.getSelectedColor()
        DialogColor.Event.Result(setup.id, setup.extra, color, button).send(setup)
        return true
    }

    override fun onMenuButton(binding: MdfContentColorBinding, menuId: Int) {
        DialogColor.Event.Menu(setup.id, setup.extra, menuId).send(setup)
    }

}