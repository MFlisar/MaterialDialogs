package com.michaelflisar.dialogs.classes

import android.content.Context
import androidx.annotation.CallSuper
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner
import com.michaelflisar.dialogs.interfaces.IMaterialDialogPresenter

abstract class BaseMaterialDialogPresenter : IMaterialDialogPresenter {

    override var parent: MaterialDialogParent? = null
    //override var context: Context? = null
    override var lifecycleOwner: LifecycleOwner? = null

    //fun onContextAvailable(context: Context) {
    //    this.context = context
    //}

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
    }

    //override fun requireContext() = context!!
    override fun requireParent() = parent!!
    override fun requireLifecycleOwner() = lifecycleOwner!!
}