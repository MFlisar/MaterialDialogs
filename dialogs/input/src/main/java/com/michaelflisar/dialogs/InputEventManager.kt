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
        val inputs = viewManager.getCurrentInputs(binding)
        val valids = setup.input.getSingles().mapIndexed { index, single ->
            val input = inputs[index]
            if (single.validator.isValid(input)) {
                true
            } else {
                viewManager.setError(binding, index, single.validator.getError(binding.root.context, input))
                false
            }
        }
        return if (!valids.contains(false)) {
            DialogInput.Event.Result(setup.id, setup.extra, inputs, button).send(setup)
            true
        } else false
    }

    override fun onMenuButton(binding: MdfContentInputBinding, menuId: Int) {
        DialogInput.Event.Menu(setup.id, setup.extra, menuId).send(setup)
    }

}