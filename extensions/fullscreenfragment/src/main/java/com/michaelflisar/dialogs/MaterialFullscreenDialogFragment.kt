package com.michaelflisar.dialogs

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.viewbinding.ViewBinding
import com.michaelflisar.dialogs.interfaces.IMaterialDialogEvent

class MaterialFullscreenDialogFragment<S : MaterialDialogSetup<S, B, E>, B : ViewBinding, E: IMaterialDialogEvent> : AppCompatDialogFragment() {

    companion object {

        const val ARG_SETUP = "MaterialFullscreenDialogFragment|SETUP"

        fun <S : MaterialDialogSetup<S, B, E>, B : ViewBinding, E: IMaterialDialogEvent> create(
            setup: S
        ): MaterialFullscreenDialogFragment<S, B, E> {
            return MaterialFullscreenDialogFragment<S, B, E>().apply {
                val args = Bundle()
                args.putParcelable(ARG_SETUP, setup)
                arguments = args
            }
        }
    }

    private lateinit var presenter: FullscreenFragmentPresenter<S, B, E>

    // ------------------
    // Fragment
    // ------------------

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        presenter = FullscreenFragmentPresenter(requireArguments().getParcelable(ARG_SETUP)!!, this)
        presenter.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return presenter.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        presenter.onViewCreated(view, savedInstanceState)
    }

    override fun onStart() {
        super.onStart()
        presenter.onStart()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        presenter.saveViewState(outState)
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