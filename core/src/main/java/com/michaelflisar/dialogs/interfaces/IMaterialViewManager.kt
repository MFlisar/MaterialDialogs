package com.michaelflisar.dialogs.interfaces

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.viewbinding.ViewBinding
import com.michaelflisar.dialogs.MaterialDialogSetup

interface IMaterialViewManager<S: MaterialDialogSetup<S, B, *>, B: ViewBinding> {

    val wrapInScrollContainer: Boolean

    fun createContentViewBinding(
        layoutInflater: LayoutInflater,
        parent: ViewGroup?,
        attachToParent: Boolean
    ): B

    fun initBinding(
        lifecycleOwner: LifecycleOwner,
        binding: B,
        savedInstanceState: Bundle?
    )

    fun saveViewState(binding: B, outState: Bundle) {
        // empty default implementation
    }

    fun onBeforeDismiss(binding: B) {
        // empty default implementation
    }
}