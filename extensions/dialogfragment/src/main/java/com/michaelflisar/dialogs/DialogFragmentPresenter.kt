package com.michaelflisar.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.viewbinding.ViewBinding
import com.michaelflisar.dialogs.interfaces.IMaterialDialogAnimation
import com.michaelflisar.dialogs.interfaces.IMaterialDialogEvent
import com.michaelflisar.dialogs.presenters.AlertDialogPresenter

fun <S : MaterialDialogSetup<S, B, E>, B : ViewBinding, E: IMaterialDialogEvent> MaterialDialogSetup<S, B, E>.showDialogFragment(
    activity: AppCompatActivity,
    animation: IMaterialDialogAnimation? = MaterialDialog.defaults.animation
) = showDialogFragment(activity.supportFragmentManager, animation)

fun <S : MaterialDialogSetup<S, B, E>, B : ViewBinding, E: IMaterialDialogEvent> MaterialDialogSetup<S, B, E>.showDialogFragment(
    activity: FragmentActivity,
    animation: IMaterialDialogAnimation? = MaterialDialog.defaults.animation
) = showDialogFragment(activity.supportFragmentManager, animation)

fun <S : MaterialDialogSetup<S, B, E>, B : ViewBinding, E: IMaterialDialogEvent> MaterialDialogSetup<S, B, E>.showDialogFragment(
    fragment: Fragment,
    animation: IMaterialDialogAnimation? = MaterialDialog.defaults.animation
) = showDialogFragment(fragment.childFragmentManager, animation)

fun <S : MaterialDialogSetup<S, B, E>, B : ViewBinding, E: IMaterialDialogEvent> MaterialDialogSetup<S, B, E>.showDialogFragment(
    fragmentManager: FragmentManager,
    animation: IMaterialDialogAnimation? = MaterialDialog.defaults.animation
) {
    val f = MaterialDialogFragment.create(this as S, animation)
    f.show(fragmentManager, f::class.java.name)
}

internal class DialogFragmentPresenter<S : MaterialDialogSetup<S, B, E>, B : ViewBinding, E: IMaterialDialogEvent>(
    private val setup: S,
    private val fragment: MaterialDialogFragment<S, B, E>
) {

    // ----------------
    // Fragment
    // ----------------

    private lateinit var dialogData: AlertDialogPresenter.DialogData<B>
    private var animation: IMaterialDialogAnimation? = null

    fun onCreate(savedInstanceState: Bundle?, animation: IMaterialDialogAnimation?) {
        this.animation = animation
        setup.dismiss = { fragment.dismiss() }
        fragment.isCancelable = setup.cancelable
    }

    fun onCreateDialog(context: Context, savedInstanceState: Bundle?): Dialog {
        val alertDialogPresenter = AlertDialogPresenter<S, B, E>(setup)
        dialogData = alertDialogPresenter.createDialog(context, animation, savedInstanceState, fragment)
        return dialogData.dialog
    }

    fun saveViewState(outState: Bundle) {
        setup.viewManager.saveViewState(dialogData.binding, outState)
    }

    fun onCancelled() {
        setup.eventManager.onCancelled()
    }

    fun onBeforeDismiss(allowingStateLoss: Boolean) : Boolean {
        setup.viewManager.onBeforeDismiss(dialogData.binding)
        return true
    }

    fun onDestroy() {
        setup.dismiss = null
    }
}