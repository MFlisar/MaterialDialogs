package com.michaelflisar.dialogs.interfaces

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.michaelflisar.dialogs.MaterialDialogSetup
import com.michaelflisar.dialogs.classes.MaterialDialogButton

interface IMaterialViewManager<S: MaterialDialogSetup<S>, B: ViewBinding> {

    val wrapInScrollContainer: Boolean

    val setup: S
    val binding: B

    val context: Context
        get() = binding.root.context

    fun createContentViewBinding(
        layoutInflater: LayoutInflater,
        parent: ViewGroup?,
        attachToParent: Boolean
    )

    fun onPresenterAvailable(presenter: IMaterialDialogPresenter<S>)

    fun initBinding(savedInstanceState: Bundle?)

    fun onButtonsReady()

    fun saveViewState(outState: Bundle) {
        // empty default implementation
    }

    fun onBeforeDismiss() {
        // empty default implementation
    }

    fun onDestroy() {
        // empty default implementation
    }

    fun onBackPress() : Boolean {
        // by default we do not handle the backpress
        return false
    }

    fun onInterceptButtonClick(view: View, button: MaterialDialogButton) : Boolean {
        // we don't intercept this button click by default
        return false
    }
}