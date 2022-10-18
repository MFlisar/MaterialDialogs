package com.michaelflisar.dialogs

import com.michaelflisar.dialogs.classes.MaterialDialogAction
import com.michaelflisar.dialogs.classes.MaterialDialogButton
import com.michaelflisar.dialogs.datetime.databinding.MdfContentDatetimeBinding
import com.michaelflisar.dialogs.interfaces.IMaterialDialogPresenter
import com.michaelflisar.dialogs.interfaces.IMaterialEventManager

internal class DateTimeEventManager<T : DateTimeData>(
    private val setup: DialogDateTime<T>
) : IMaterialEventManager<DialogDateTime<T>> {

    override fun onEvent(
        presenter: IMaterialDialogPresenter<DialogDateTime<T>, *>,
        action: MaterialDialogAction
    ) {
        val event = when (setup.value) {
            is DateTimeData.DateTime -> DialogDateTime.EventDateTime.Action(setup.id, setup.extra, action)
            is DateTimeData.Date -> DialogDateTime.EventDate.Action(setup.id, setup.extra, action)
            is DateTimeData.Time -> DialogDateTime.EventTime.Action(setup.id, setup.extra, action)
            else -> throw RuntimeException()
        }
        event.send(presenter)
    }

    override fun onButton(
        presenter: IMaterialDialogPresenter<DialogDateTime<T>, *>,
        button: MaterialDialogButton
    ): Boolean {
        val viewManager = setup.viewManager as DateTimeViewManager<T>
        val binding = viewManager.binding
        val event = when (setup.value) {
            is DateTimeData.DateTime -> DialogDateTime.EventDateTime.Result(setup.id, setup.extra, viewManager.getCurrentValue(binding) as DateTimeData.DateTime)
            is DateTimeData.Date -> DialogDateTime.EventDate.Result(setup.id, setup.extra, viewManager.getCurrentValue(binding) as DateTimeData.Date)
            is DateTimeData.Time -> DialogDateTime.EventTime.Result(setup.id, setup.extra, viewManager.getCurrentValue(binding) as DateTimeData.Time)
            else -> throw RuntimeException()
        }
        event.send(presenter)
        return true
    }
}