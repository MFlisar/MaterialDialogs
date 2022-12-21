package com.michaelflisar.dialogs.classes

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.viewbinding.ViewBinding
import com.michaelflisar.dialogs.MaterialDialogSetup
import com.michaelflisar.dialogs.interfaces.IMaterialDialogPresenter
import com.michaelflisar.dialogs.interfaces.IMaterialViewManager

abstract class BaseMaterialViewManager<S: MaterialDialogSetup<S>, B : ViewBinding> : IMaterialViewManager<S, B> {

    private var _binding: B? = null
    override val binding: B
        get() = _binding!!

    private var _presenter: IMaterialDialogPresenter<S>? = null
    val presenter: IMaterialDialogPresenter<S>
        get() = _presenter!!

    override fun createContentViewBinding(
        layoutInflater: LayoutInflater,
        parent: ViewGroup?,
        attachToParent: Boolean
    ) {
        _binding = onCreateContentViewBinding(layoutInflater, parent, attachToParent)
    }

    override fun onPresenterAvailable(presenter: IMaterialDialogPresenter<S>) {
        _presenter = presenter
    }

    abstract fun onCreateContentViewBinding(
        layoutInflater: LayoutInflater,
        parent: ViewGroup?,
        attachToParent: Boolean
    ) : B

    override fun onButtonsReady() {
    }

    @CallSuper
    override fun onDestroy() {
        _binding = null
        _presenter = null
    }
}