package com.michaelflisar.dialogs

import com.michaelflisar.dialogs.classes.MaterialDialogAction
import com.michaelflisar.dialogs.classes.MaterialDialogButton
import com.michaelflisar.dialogs.color.databinding.MdfContentColorBinding
import com.michaelflisar.dialogs.interfaces.IMaterialEventManager

internal class ColorEventManager(
    private val setup: DialogColor
) : IMaterialEventManager<DialogColor, MdfContentColorBinding> {

    override fun onEvent(binding: MdfContentColorBinding, action: MaterialDialogAction) {
        DialogColor.Event.Action(setup.id, setup.extra, action).send(setup)
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
}