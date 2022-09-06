package com.michaelflisar.dialogs.internal

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.michaelflisar.dialogs.MaterialDialog
import com.michaelflisar.dialogs.interfaces.IMaterialDialogEvent
import com.michaelflisar.dialogs.classes.MaterialDialogKey

internal class MaterialDialogEventListenerWrapper<E: IMaterialDialogEvent>(
    lifecycleOwner: LifecycleOwner,
    val key: MaterialDialogKey,
    val listener: (event: E) -> Unit
) : DefaultLifecycleObserver {

    init {
        lifecycleOwner.lifecycle.addObserver(this)
        MaterialDialog.addListener(this)
    }

    override fun onStart(owner: LifecycleOwner) {
        MaterialDialog.setState(this, true)
    }

    override fun onStop(owner: LifecycleOwner) {
        MaterialDialog.setState(this, false)
    }

    override fun onDestroy(owner: LifecycleOwner) {
        MaterialDialog.removeListener(this)
    }
}