package com.michaelflisar.dialogs

import com.michaelflisar.dialogs.classes.MaterialDialogButton
import com.michaelflisar.dialogs.input.databinding.MdfContentInputBinding
import com.michaelflisar.dialogs.interfaces.IMaterialEventManager

internal class InputEventManager(
    private val setup: DialogInput
): IMaterialEventManager<DialogInput, MdfContentInputBinding> {

    override fun onCancelled() {
        DialogInput.Event.Cancelled(setup.id, setup.extra).send(setup)
    }

    override fun onButton(
        binding: MdfContentInputBinding,
        button: MaterialDialogButton
    ): Boolean {
        val viewManager = setup.viewManager as InputViewManager
        val input = viewManager.getCurrentInput(binding)
        return if (setup.validator.isValid(input)) {
            DialogInput.Event.Result(setup.id, setup.extra, input, button).send(setup)
            true
        } else {
            viewManager.setError(binding, setup.validator.getError(binding.root.context, input))
            false
        }
    }

}