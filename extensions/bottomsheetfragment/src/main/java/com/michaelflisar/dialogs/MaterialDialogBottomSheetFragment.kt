package com.michaelflisar.dialogs

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.michaelflisar.dialogs.interfaces.IMaterialDialogEvent

internal class MaterialDialogBottomSheetFragment<S : MaterialDialogSetup<S>> :
    BottomSheetDialogFragment() {

    companion object {

        const val ARG_SETUP = "MaterialDialogBottomSheetFragment|SETUP"
        const val ARG_STYLE = "MaterialDialogBottomSheetFragment|STYLE"

        fun <S : MaterialDialogSetup<S>> create(
            setup: S,
            style: BottomSheetDialogStyle
        ): MaterialDialogBottomSheetFragment<S> {
            return MaterialDialogBottomSheetFragment<S>().apply {
                val args = Bundle()
                args.putParcelable(ARG_SETUP, setup)
                args.putParcelable(ARG_STYLE, style)
                arguments = args
            }
        }
    }

    private lateinit var presenter: BottomSheetFragmentPresenter<S>

    // ------------------
    // Fragment
    // ------------------

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        presenter =
            BottomSheetFragmentPresenter(requireArguments().getParcelable(ARG_SETUP)!!, requireArguments().getParcelable(ARG_STYLE)!!, this)
        presenter.onCreate(savedInstanceState, requireActivity(), parentFragment)
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