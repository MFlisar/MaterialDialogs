package com.michaelflisar.dialogs.classes

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner
import com.michaelflisar.dialogs.interfaces.IMaterialDialogEvent
import com.michaelflisar.dialogs.interfaces.IMaterialDialogPresenter

abstract class BaseMaterialDialogPresenter : IMaterialDialogPresenter {

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

    fun onDestroy() {
        this.parent = null
        this.dismiss = null
        this.eventCallback = null
    }

    override fun requireParent() = parent!!
    override fun requireLifecycleOwner() = lifecycleOwner!!

    override var dismiss: (() -> Unit)? = null
    override var eventCallback: ((IMaterialDialogEvent) -> Unit)? = null
}