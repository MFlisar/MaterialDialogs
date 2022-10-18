package com.michaelflisar.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.viewbinding.ViewBinding
import com.michaelflisar.dialogs.classes.BaseMaterialDialogPresenter
import com.michaelflisar.dialogs.classes.MaterialDialogAction
import com.michaelflisar.dialogs.classes.MaterialDialogParent
import com.michaelflisar.dialogs.interfaces.IMaterialDialogEvent
import com.michaelflisar.dialogs.presenters.AlertDialogPresenter
import com.michaelflisar.dialogs.presenters.DialogStyle

fun <S : MaterialDialogSetup<S, B>, B : ViewBinding> MaterialDialogSetup<S, B>.showDialogFragment(
    activity: AppCompatActivity,
    style: DialogStyle = DialogStyle()
) = showDialogFragment(activity.supportFragmentManager, style)

fun <S : MaterialDialogSetup<S, B>, B : ViewBinding> MaterialDialogSetup<S, B>.showDialogFragment(
    activity: FragmentActivity,
    style: DialogStyle = DialogStyle()
) = showDialogFragment(activity.supportFragmentManager, style)

fun <S : MaterialDialogSetup<S, B>, B : ViewBinding> MaterialDialogSetup<S, B>.showDialogFragment(
    fragment: Fragment,
    style: DialogStyle = DialogStyle()
) = showDialogFragment(fragment.childFragmentManager, style)

fun <S : MaterialDialogSetup<S, B>, B : ViewBinding> MaterialDialogSetup<S, B>.showDialogFragment(
    fragmentManager: FragmentManager,
    style: DialogStyle = DialogStyle()
) {
    val f = MaterialDialogFragment.create(this as S, style)
    f.show(fragmentManager, f::class.java.name)
}

fun <S : MaterialDialogSetup<S, B>, B : ViewBinding> MaterialDialogSetup<S, B>.showDialogFragment(
    parent: MaterialDialogParent,
    style: DialogStyle = DialogStyle()
) {
    when (parent) {
        is MaterialDialogParent.Activity -> showDialogFragment(parent.activity, style)
        is MaterialDialogParent.Context -> {
            throw RuntimeException("This presenter needs an activity or fragment parent context!")
        }
        is MaterialDialogParent.Fragment -> showDialogFragment(parent.fragment, style)
    }
}

internal class DialogFragmentPresenter<S : MaterialDialogSetup<S, B>, B : ViewBinding>(
    override val setup: S,
    private val fragment: MaterialDialogFragment<S, B>
) : BaseMaterialDialogPresenter<S, B>() {

    // ----------------
    // Fragment
    // ----------------

    private lateinit var dialogData: AlertDialogPresenter.DialogData<B>
    private lateinit var style: DialogStyle

    fun onCreate(
        savedInstanceState: Bundle?,
        activity: FragmentActivity,
        parentFragment: Fragment?,
        style: DialogStyle
    ) {
        this.style = style
        this.dismiss = { fragment.dismiss() }
        fragment.isCancelable = setup.cancelable
        onParentAvailable(activity, parentFragment)
        onLifecycleOwnerAvailable(fragment)
    }

    fun onCreateDialog(context: Context, savedInstanceState: Bundle?): Dialog {
        val alertDialogPresenter = AlertDialogPresenter<S, B, IMaterialDialogEvent>(setup)
        dialogData = alertDialogPresenter.createDialog(context, style, savedInstanceState, fragment)
        return dialogData.dialog
    }

    fun saveViewState(outState: Bundle) {
        setup.viewManager.saveViewState(outState)
    }

    fun onCancelled() {
        setup.eventManager.onEvent(this, MaterialDialogAction.Cancelled)
    }

    fun onBeforeDismiss(allowingStateLoss: Boolean): Boolean {
        setup.viewManager.onBeforeDismiss()
        return true
    }

    override fun onDestroy() {
        setup.viewManager.onDestroy()
        super.onDestroy()
    }
}