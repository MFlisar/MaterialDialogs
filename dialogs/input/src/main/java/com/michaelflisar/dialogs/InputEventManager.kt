package com.michaelflisar.dialogs

import com.michaelflisar.dialogs.classes.MaterialDialogAction
import com.michaelflisar.dialogs.classes.MaterialDialogButton
import com.michaelflisar.dialogs.input.databinding.MdfContentInputBinding
import com.michaelflisar.dialogs.interfaces.IMaterialDialogPresenter
import com.michaelflisar.dialogs.interfaces.IMaterialEventManager

internal class InputEventManager(
    private val setup: DialogInput
) : IMaterialEventManager<DialogInput> {

    override fun onEvent(
        presenter: IMaterialDialogPresenter<DialogInput, *>,
        action: MaterialDialogAction
    ) {
        DialogInput.Event.Action(setup.id, setup.extra, action).send(presenter)
    }

    override fun onButton(
        presenter: IMaterialDialogPresenter<DialogInput, *>,
        button: MaterialDialogButton
    ): Boolean {
        val viewManager = setup.viewManager as InputViewManager
        val binding = viewManager.binding
        val inputs = viewManager.getCurrentInputs(binding)
        val valids = setup.input.getSingles().mapIndexed { index, single ->
            val input = inputs[index]
            if (single.validator.isValid(input)) {
                true
            } else {
                viewManager.setError(
                    binding,
                    index,
                    single.validator.getError(binding.root.context, input)
                )
                false
            }
        }
        return if (!valids.contains(false)) {
            DialogInput.Event.Result(setup.id, setup.extra, inputs, button).send(presenter)
            true
        } else false
    }
}