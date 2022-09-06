package com.michaelflisar.dialogs

import com.michaelflisar.dialogs.classes.MaterialDialogButton
import com.michaelflisar.dialogs.interfaces.IMaterialEventManager
import com.michaelflisar.dialogs.number.databinding.MdfContentNumberBinding

internal class NumberEventManager<T : Number>(
    private val setup: DialogNumber<T>
) : IMaterialEventManager<DialogNumber<T>, MdfContentNumberBinding> {

    override fun onCancelled() {
        val event = when (setup.value) {
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
        val input = viewManager.currentValue
        val event = when (setup.value) {
            is Int -> DialogNumber.EventInt.Result(setup.id, setup.extra, input as Int, button)
            is Long -> DialogNumber.EventLong.Result(setup.id, setup.extra, input as Long, button)
            is Float -> DialogNumber.EventFloat.Result(
                setup.id,
                setup.extra,
                input as Float,
                button
            )
            is Double -> DialogNumber.EventDouble.Result(
                setup.id,
                setup.extra,
                input as Double,
                button
            )
            else -> throw RuntimeException()
        }
        event.send(setup)
        return true
    }
}