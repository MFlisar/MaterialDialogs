package com.michaelflisar.dialogs

import com.michaelflisar.dialogs.classes.MaterialDialogButton
import com.michaelflisar.dialogs.interfaces.IMaterialEventManager
import com.michaelflisar.dialogs.number.R
import com.michaelflisar.dialogs.number.databinding.MdfContentNumberBinding

internal class NumberEventManager<T : Number>(
    private val setup: DialogNumber<T>
) : IMaterialEventManager<DialogNumber<T>, MdfContentNumberBinding> {

    override fun onCancelled() {
        val event = when (setup.firstValue()) {
            is Int -> DialogNumber.EventInt.Cancelled(setup.id, setup.extra)
            is Long -> DialogNumber.EventLong.Cancelled(setup.id, setup.extra)
            is Float -> DialogNumber.EventFloat.Cancelled(setup.id, setup.extra)
            is Double -> DialogNumber.EventDouble.Cancelled(setup.id, setup.extra)
            else -> throw RuntimeException()
        }
        event.send(setup)
    }

    override fun onButton(
        binding: MdfContentNumberBinding,
        button: MaterialDialogButton
    ): Boolean {
        val viewManager = setup.viewManager as NumberViewManager<T>
        val inputs = viewManager.getCurrentValues(binding)
        val valids = setup.input.getSingles<T>().mapIndexed { index, single ->
            val input = inputs[index]
            if (single.isValid(input)) {
                true
            } else {
                viewManager.setError(binding, index, binding.root.context.getString(R.string.mdf_error_invalid_number))
                false
            }
        }
        return if (!valids.contains(false)) {
            val event = when (setup.firstValue()) {
                is Int -> DialogNumber.EventInt.Result(setup.id, setup.extra, inputs as List<Int>, button)
                is Long -> DialogNumber.EventLong.Result(setup.id, setup.extra, inputs as List<Long>, button)
                is Float -> DialogNumber.EventFloat.Result(
                    setup.id,
                    setup.extra,
                    inputs as List<Float>,
                    button
                )
                is Double -> DialogNumber.EventDouble.Result(
                    setup.id,
                    setup.extra,
                    inputs as List<Double>,
                    button
                )
                else -> throw RuntimeException()
            }
            event.send(setup)
            true
        } else false
    }
}