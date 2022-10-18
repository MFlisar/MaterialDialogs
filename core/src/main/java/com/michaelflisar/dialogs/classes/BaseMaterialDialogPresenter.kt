package com.michaelflisar.dialogs.classes

import androidx.annotation.CallSuper
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner
import androidx.viewbinding.ViewBinding
import com.michaelflisar.dialogs.MaterialDialogSetup
import com.michaelflisar.dialogs.interfaces.IMaterialDialogEvent
import com.michaelflisar.dialogs.interfaces.IMaterialDialogPresenter

abstract class BaseMaterialDialogPresenter<S : MaterialDialogSetup<S, B>, B : ViewBinding> : IMaterialDialogPresenter<S, B> {

    override var parent: MaterialDialogParent? = null
    override var lifecycleOwner: LifecycleOwner? = null

    fun onLifecycleOwnerAvailable(lifecycleOwner: LifecycleOwner) {
        this.lifecycleOwner = lifecycleOwner
    }

    fun onParentAvailable(activity: FragmentActivity, parentFragment: Fragment?) {
        if (parentFragment != null) {
            onParentAvailable(MaterialDialogParent.Fragment(parentFragment))
        } else {
            onParentAvailable(MaterialDialogParent.Activity(activity))
        }
    }

    fun onParentAvailable(parent: MaterialDialogParent) {
        this.parent = parent
    }

    @CallSuper
    open fun onDestroy() {
        this.parent = null
        this.dismiss = null
        this.eventCallback = null
    }

    override fun requireParent() = parent!!
    override fun requireLifecycleOwner() = lifecycleOwner!!

    override var dismiss: (() -> Unit)? = null
    override var eventCallback: ((IMaterialDialogEvent) -> Unit)? = null
}