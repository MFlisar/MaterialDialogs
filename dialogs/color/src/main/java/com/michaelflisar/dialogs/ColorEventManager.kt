package com.michaelflisar.dialogs

import com.michaelflisar.dialogs.classes.MaterialDialogAction
import com.michaelflisar.dialogs.classes.MaterialDialogButton
import com.michaelflisar.dialogs.classes.MaterialDialogParent
import com.michaelflisar.dialogs.color.databinding.MdfContentColorBinding
import com.michaelflisar.dialogs.interfaces.IMaterialDialogPresenter
import com.michaelflisar.dialogs.interfaces.IMaterialEventManager

internal class ColorEventManager(
    private val setup: DialogColor
) : IMaterialEventManager<DialogColor, MdfContentColorBinding> {

    override fun onEvent(presenter: IMaterialDialogPresenter, binding: MdfContentColorBinding, action: MaterialDialogAction) {
        DialogColor.Event.Action(setup.id, setup.extra, action).send(presenter, setup)
    }

    override fun onButton(
        presenter: IMaterialDialogPresenter,
        binding: MdfContentColorBinding,
        button: MaterialDialogButton
    ): Boolean {
        val viewManager = setup.viewManager as ColorViewManager
        val color = viewManager.getSelectedColor()
        DialogColor.Event.Result(setup.id, setup.extra, color, button).send(presenter, setup)
        return true
    }
}