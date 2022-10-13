package com.michaelflisar.dialogs

import com.michaelflisar.dialogs.classes.MaterialDialogButton
import com.michaelflisar.dialogs.datetime.databinding.MdfContentDatetimeBinding
import com.michaelflisar.dialogs.interfaces.IMaterialEventManager

internal class DateTimeEventManager<T : DateTimeData>(
    private val setup: DialogDateTime<T>
) : IMaterialEventManager<DialogDateTime<T>, MdfContentDatetimeBinding> {

    override fun onCancelled() {
        val event = when (setup.value) {
            is DateTimeData.DateTime -> DialogDateTime.EventDateTime.Cancelled(setup.id, setup.extra)
            is DateTimeData.Date -> DialogDateTime.EventDate.Cancelled(setup.id, setup.extra)
            is DateTimeData.Time -> DialogDateTime.EventTime.Cancelled(setup.id, setup.extra)
            else -> throw RuntimeException()
        }
        event.send(setup)
    }

    override fun onButton(
        binding: MdfContentDatetimeBinding,
        button: MaterialDialogButton
    ): Boolean {
        val viewManager = setup.viewManager as DateTimeViewManager<T>
        val event = when (setup.value) {
            is DateTimeData.DateTime -> DialogDateTime.EventDateTime.Result(setup.id, setup.extra, viewManager.getCurrentValue(binding) as DateTimeData.DateTime)
            is DateTimeData.Date -> DialogDateTime.EventDate.Result(setup.id, setup.extra, viewManager.getCurrentValue(binding) as DateTimeData.Date)
            is DateTimeData.Time -> DialogDateTime.EventTime.Result(setup.id, setup.extra, viewManager.getCurrentValue(binding) as DateTimeData.Time)
            else -> throw RuntimeException()
        }
        event.send(setup)
        return true
    }

    override fun onMenuButton(binding: MdfContentDatetimeBinding, menuId: Int) {
        val event = when (setup.value) {
            is DateTimeData.DateTime -> DialogDateTime.EventDateTime.Menu(setup.id, setup.extra, menuId)
            is DateTimeData.Date -> DialogDateTime.EventDate.Menu(setup.id, setup.extra, menuId)
            is DateTimeData.Time -> DialogDateTime.EventTime.Menu(setup.id, setup.extra, menuId)
            else -> throw RuntimeException()
        }
        event.send(setup)
    }
}