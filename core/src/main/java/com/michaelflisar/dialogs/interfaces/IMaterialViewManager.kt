package com.michaelflisar.dialogs.interfaces

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.michaelflisar.dialogs.MaterialDialogSetup
import com.michaelflisar.dialogs.classes.MaterialDialogButton

interface IMaterialViewManager<S: MaterialDialogSetup<S, B, *>, B: ViewBinding> {

    val wrapInScrollContainer: Boolean
    val binding: B

    fun createContentViewBinding(
        layoutInflater: LayoutInflater,
        parent: ViewGroup?,
        attachToParent: Boolean
    )

    /*fun createContentViewBinding(
        layoutInflater: LayoutInflater,
        parent: ViewGroup?,
        attachToParent: Boolean
    ) : B*/

    fun initBinding(
        presenter: IMaterialDialogPresenter<*, *, *>,
        savedInstanceState: Bundle?
    )

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