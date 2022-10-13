package com.michaelflisar.dialogs.interfaces

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.viewbinding.ViewBinding
import com.michaelflisar.dialogs.MaterialDialogSetup
import com.michaelflisar.dialogs.classes.MaterialDialogButton

interface IMaterialViewManager<S: MaterialDialogSetup<S, B, *>, B: ViewBinding> {

    val wrapInScrollContainer: Boolean

    fun createContentViewBinding(
        layoutInflater: LayoutInflater,
        parent: ViewGroup?,
        attachToParent: Boolean
    ) : B

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

    fun onBackPress(binding: B) : Boolean {
        // by default we do not handle the backpress
        return false
    }

    fun onInterceptButtonClick(view: View, button: MaterialDialogButton.DialogButton) : Boolean {
        // we don't intercept this button click by default
        return false
    }
}