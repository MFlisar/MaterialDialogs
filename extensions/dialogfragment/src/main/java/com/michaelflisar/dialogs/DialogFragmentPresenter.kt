package com.michaelflisar.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.michaelflisar.dialogs.classes.BaseMaterialDialogPresenter
import com.michaelflisar.dialogs.classes.MaterialDialogAction
import com.michaelflisar.dialogs.classes.MaterialDialogButton
import com.michaelflisar.dialogs.classes.MaterialDialogParent
import com.michaelflisar.dialogs.interfaces.IMaterialDialogEvent
import com.michaelflisar.dialogs.presenters.AlertDialogPresenter
import com.michaelflisar.dialogs.presenters.DialogStyle

fun <S : MaterialDialogSetup<S>> MaterialDialogSetup<S>.showDialogFragment(
    activity: AppCompatActivity,
    style: DialogStyle = DialogStyle()
) = showDialogFragment(activity.supportFragmentManager, style)

fun <S : MaterialDialogSetup<S>> MaterialDialogSetup<S>.showDialogFragment(
    activity: FragmentActivity,
    style: DialogStyle = DialogStyle()
) = showDialogFragment(activity.supportFragmentManager, style)

fun <S : MaterialDialogSetup<S>> MaterialDialogSetup<S>.showDialogFragment(
    fragment: Fragment,
    style: DialogStyle = DialogStyle()
) = showDialogFragment(fragment.childFragmentManager, style)

fun <S : MaterialDialogSetup<S>> MaterialDialogSetup<S>.showDialogFragment(
    fragmentManager: FragmentManager,
    style: DialogStyle = DialogStyle()
) {
    @Suppress("UNCHECKED_CAST")
    val f = MaterialDialogFragment.create(this as S, style)
    f.show(fragmentManager, f::class.java.name)
}

fun <S : MaterialDialogSetup<S>> MaterialDialogSetup<S>.showDialogFragment(
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

internal class DialogFragmentPresenter<S : MaterialDialogSetup<S>>(
    override val setup: S,
    private val fragment: MaterialDialogFragment<S>
) : BaseMaterialDialogPresenter<S>() {

    // ----------------
    // Fragment
    // ----------------

    private lateinit var dialogData: DialogFragmentData<S>
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
        val alertDialogPresenter = AlertDialogPresenter<S, IMaterialDialogEvent>(setup)
        dialogData = DialogFragmentData(
            alertDialogPresenter.createDialog(context, style, savedInstanceState, fragment),
            alertDialogPresenter
        )
        return dialogData.dialogData.dialog
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

    override fun setButtonEnabled(button: MaterialDialogButton, enabled: Boolean) {
        dialogData.presenter.setButtonEnabled(button, enabled)
    }

    override fun setButtonVisible(button: MaterialDialogButton, visible: Boolean) {
        dialogData.presenter.setButtonVisible(button, visible)
    }

    override fun setButtonText(button: MaterialDialogButton, text: CharSequence) {
        dialogData.presenter.setButtonText(button, text)
    }

    // -----------------
    // helper class
    // -----------------

    class DialogFragmentData<S : MaterialDialogSetup<S>>(
        val dialogData: AlertDialogPresenter.DialogData,
        val presenter: AlertDialogPresenter<S, IMaterialDialogEvent>
    )
}