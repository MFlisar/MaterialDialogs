package com.michaelflisar.dialogs.classes

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.viewbinding.ViewBinding
import com.michaelflisar.dialogs.MaterialDialogSetup
import com.michaelflisar.dialogs.interfaces.IMaterialViewManager

abstract class BaseMaterialViewManager<S: MaterialDialogSetup<S>, B : ViewBinding> : IMaterialViewManager<S, B> {

    private var _binding: B? = null
    override val binding: B
        get() = _binding!!

    override fun createContentViewBinding(
        layoutInflater: LayoutInflater,
        parent: ViewGroup?,
        attachToParent: Boolean
    ) {
        _binding = onCreateContentViewBinding(layoutInflater, parent, attachToParent)
    }

    abstract fun onCreateContentViewBinding(
        layoutInflater: LayoutInflater,
        parent: ViewGroup?,
        attachToParent: Boolean
    ) : B

    @CallSuper
    override fun onDestroy() {
        _binding = null
    }

}