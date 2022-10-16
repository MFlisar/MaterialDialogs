package com.michaelflisar.dialogs

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.viewbinding.ViewBinding
import com.michaelflisar.dialogs.interfaces.IMaterialDialogAnimation
import com.michaelflisar.dialogs.interfaces.IMaterialDialogEvent

internal class MaterialDialogFragment<S : MaterialDialogSetup<S, B, E>, B : ViewBinding, E: IMaterialDialogEvent> : AppCompatDialogFragment() {

    companion object {

        const val KEY_ANIMATION_STATE = "MaterialDialogFragment|ANIMATIONSTATE"
        const val ARG_SETUP = "MaterialDialogFragment|SETUP"
        const val ARG_ANIMATION = "MaterialDialogFragment|ANIMATION"

        fun <S : MaterialDialogSetup<S, B, E>, B : ViewBinding, E: IMaterialDialogEvent> create(
            setup: S,
            animation: IMaterialDialogAnimation?
        ): MaterialDialogFragment<S, B, E> {
            return MaterialDialogFragment<S, B, E>().apply {
                val args = Bundle()
                args.putParcelable(ARG_SETUP, setup)
                args.putParcelable(ARG_ANIMATION, animation)
                arguments = args
            }
        }
    }

    private lateinit var presenter: DialogFragmentPresenter<S, B, E>
    private var animationDone: Boolean = false

    // ------------------
    // Fragment
    // ------------------

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val animation = requireArguments().getParcelable<IMaterialDialogAnimation?>(ARG_ANIMATION)
        presenter = DialogFragmentPresenter(requireArguments().getParcelable(ARG_SETUP)!!, this)
        presenter.onCreate(savedInstanceState, requireActivity(), parentFragment, animation)
        animationDone = savedInstanceState?.getBoolean(KEY_ANIMATION_STATE) ?: false
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return presenter.onCreateDialog(requireContext(), savedInstanceState)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        presenter.saveViewState(outState)
        outState.putBoolean(KEY_ANIMATION_STATE, animationDone)
    }

    override fun onDestroy() {
        presenter.onDestroy()
        super.onDestroy()
    }

    override fun dismiss() {
        if (presenter.onBeforeDismiss(false))
            super.dismiss()
    }

    override fun dismissAllowingStateLoss() {
        if (presenter.onBeforeDismiss(true))
            super.dismiss()
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        presenter.onCancelled()
    }
}