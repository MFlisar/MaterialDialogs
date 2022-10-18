package com.michaelflisar.dialogs.classes

import com.michaelflisar.dialogs.interfaces.IMaterialDialogPresenter

internal object InstanceManager {

    private val presenters: HashMap<Int, IMaterialDialogPresenter<*>> = HashMap()

    internal fun register(id: Int, presenter: IMaterialDialogPresenter<*>) {
        val existing = presenters[id]
        existing?.dismiss?.invoke()
        presenters[id] = presenter
    }

    internal fun unregister(id: Int) {
        presenters.remove(id)
    }

    internal fun dismiss(id: Int) {
        val existing = presenters[id]
        existing?.dismiss?.invoke()
    }

    internal fun get(id: Int): IMaterialDialogPresenter<*>? {
        return presenters[id]
    }

}