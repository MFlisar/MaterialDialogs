package com.michaelflisar.dialogs

import androidx.lifecycle.LifecycleOwner
import com.michaelflisar.dialogs.classes.MaterialDialogAction
import com.michaelflisar.dialogs.classes.MaterialDialogKey
import com.michaelflisar.dialogs.classes.MaterialDialogParent
import com.michaelflisar.dialogs.classes.BaseMaterialDialogPresenter
import com.michaelflisar.dialogs.interfaces.IMaterialDialogEvent
import com.michaelflisar.dialogs.interfaces.IMaterialDialogImageLoader
import com.michaelflisar.dialogs.interfaces.IMaterialDialogPresenter
import com.michaelflisar.dialogs.internal.MaterialDialogEventListenerWrapper

inline fun <reified E : IMaterialDialogEvent> LifecycleOwner.onMaterialDialogEvent(
    id: Int? = null,
    noinline listener: (event: E) -> Unit
) {
    MaterialDialog.onEvent(this, id, listener)
}

object MaterialDialog {

    private val callbacks: ArrayList<(IMaterialDialogPresenter, IMaterialDialogEvent) -> Unit> = ArrayList()
    private val listeners: ArrayList<MaterialDialogEventListenerWrapper<*>> = ArrayList()
    private val activeListeners: ArrayList<MaterialDialogEventListenerWrapper<*>> = ArrayList()

    // --------------
    // public functions
    // --------------

    var defaults = MaterialDefaultSettings()

    var imageLoader: IMaterialDialogImageLoader? = null

    inline fun <reified E : IMaterialDialogEvent> onEvent(
        lifecycleOwner: LifecycleOwner,
        id: Int? = null,
        noinline listener: (event: E) -> Unit
    ) {
        // create the wrapper - it takes care of everything!
        val key = if (id == null) MaterialDialogKey.Simple(
            E::class.java
        ) else MaterialDialogKey.ID(
            E::class.java,
            id
        )
        val wrapper = createWrapper(lifecycleOwner, key, listener)
    }

    /*
     custom callbacks - be careful, those are not managed
     */
    fun addGlobalCallback(callback: (IMaterialDialogPresenter, IMaterialDialogEvent) -> Unit) {
        callbacks.add(callback)
    }

    fun removeGlobalCallback(callback: (IMaterialDialogPresenter, IMaterialDialogEvent) -> Unit) {
        callbacks.remove(callback)
    }

    internal fun <E : IMaterialDialogEvent> sendEvent(presenter: IMaterialDialogPresenter, event: E) {
        if (!defaults.sendCancelEvents && event is IMaterialDialogEvent.Action && event.data == MaterialDialogAction.Cancelled) {
            return
        }

        // Filter: Listener must be for same dialog setup and for the event class (or any sub class)
        activeListeners
            // check 1: event class must be of type E or any sub class
            .filter { it.key.classEvent.isAssignableFrom(event::class.java) }
            // check 2: eventually check the event id
            .filter {
                when (it.key) {
                    is MaterialDialogKey.ID -> it.key.id == event.id
                    is MaterialDialogKey.Simple -> true// event class was already checked
                }
            }.forEach {
                // cast is safe because of check 2
                (it.listener as (event: E) -> Unit).invoke(event)
            }
        callbacks.forEach { it.invoke(presenter, event) }
    }

    // --------------
    // Listener
    // --------------

    internal fun addListener(listener: MaterialDialogEventListenerWrapper<*>) {
        listeners.add(listener)
    }

    internal fun removeListener(listener: MaterialDialogEventListenerWrapper<*>) {
        listeners.remove(listener)
        activeListeners.remove(listener)
    }

    internal fun setState(listener: MaterialDialogEventListenerWrapper<*>, active: Boolean) {
        if (active)
            activeListeners.add(listener)
        else
            activeListeners.remove(listener)
    }

    // --------------
    // helper function to hide MaterialDialogEventListenerWrapper as internal class
    // --------------

    fun <E : IMaterialDialogEvent> createWrapper(
        lifecycleOwner: LifecycleOwner,
        key: MaterialDialogKey,
        listener: (event: E) -> Unit
    ) {
        val wrapper = MaterialDialogEventListenerWrapper(lifecycleOwner, key, listener)
    }
}