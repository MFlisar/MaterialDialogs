package com.michaelflisar.dialogs

import android.content.DialogInterface
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.michaelflisar.dialogs.interfaces.IMaterialDialogEvent

internal class MaterialDialogBottomSheetFragment<S : MaterialDialogSetup<S, B, E>, B : ViewBinding, E : IMaterialDialogEvent> :
    BottomSheetDialogFragment() {

    companion object {

        const val ARG_SETUP = "MaterialDialogBottomSheetFragment|SETUP"
        const val ARG_EXPAND = "MaterialDialogBottomSheetFragment|EXPAND"

        fun <S : MaterialDialogSetup<S, B, E>, B : ViewBinding, E : IMaterialDialogEvent> create(
            setup: S,
            expand: Boolean
        ): MaterialDialogBottomSheetFragment<S, B, E> {
            return MaterialDialogBottomSheetFragment<S, B, E>().apply {
                val args = Bundle()
                args.putParcelable(ARG_SETUP, setup)
                args.putBoolean(ARG_EXPAND, expand)
                arguments = args
            }
        }
    }

    private lateinit var presenter: BottomSheetFragmentPresenter<S, B, E>

    // ------------------
    // Fragment
    // ------------------

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        presenter =
            BottomSheetFragmentPresenter(requireArguments().getParcelable(ARG_SETUP)!!, requireArguments().getBoolean(ARG_EXPAND), this)
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
        presenter.onViewCreated(dialog, view, savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        MaterialDialogUtil.interceptDialogBackPress(dialog!!) {
            presenter.onBackPress()
        }
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
        if (presenter.onBeforeDismiss(false)) {
            super.dismiss()
        }
    }

    override fun dismissAllowingStateLoss() {
        if (presenter.onBeforeDismiss(true)) {
            super.dismissAllowingStateLoss()
        }
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        presenter.onCancelled()
    }
}