package com.michaelflisar.dialogs

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.viewbinding.ViewBinding
import com.michaelflisar.dialogs.interfaces.IMaterialDialogEvent
import com.michaelflisar.dialogs.presenters.DialogStyle

internal class MaterialDialogFragment<S : MaterialDialogSetup<S, B>, B : ViewBinding> : AppCompatDialogFragment() {

    companion object {

        const val KEY_ANIMATION_STATE = "MaterialDialogFragment|ANIMATIONSTATE"
        const val ARG_SETUP = "MaterialDialogFragment|SETUP"
        const val ARG_STYLE = "MaterialDialogFragment|STYLE"

        fun <S : MaterialDialogSetup<S, B>, B : ViewBinding> create(
            setup: S,
            style: DialogStyle
        ): MaterialDialogFragment<S, B> {
            return MaterialDialogFragment<S, B>().apply {
                val args = Bundle()
                args.putParcelable(ARG_SETUP, setup)
                args.putParcelable(ARG_STYLE, style)
                arguments = args
            }
        }
    }

    private lateinit var presenter: DialogFragmentPresenter<S, B>
    private var animationDone: Boolean = false

    // ------------------
    // Fragment
    // ------------------

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val style = requireArguments().getParcelable<DialogStyle>(ARG_STYLE)!!
        presenter = DialogFragmentPresenter(requireArguments().getParcelable(ARG_SETUP)!!, this)
        presenter.onCreate(savedInstanceState, requireActivity(), parentFragment, style)
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
        if (presenter.onBeforeDismiss(false)) {
            super.dismiss()
        }
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