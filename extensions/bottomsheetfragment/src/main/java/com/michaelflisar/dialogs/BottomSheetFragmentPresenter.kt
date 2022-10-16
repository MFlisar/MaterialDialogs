package com.michaelflisar.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.viewbinding.ViewBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.michaelflisar.dialogs.bottomsheetfragments.databinding.MdfBottomSheetDialogBinding
import com.michaelflisar.dialogs.classes.*
import com.michaelflisar.dialogs.interfaces.IMaterialDialogEvent

fun <S : MaterialDialogSetup<S, B, E>, B : ViewBinding, E : IMaterialDialogEvent> MaterialDialogSetup<S, B, E>.showBottomSheetDialogFragment(
    activity: FragmentActivity,
    expand: Boolean = MaterialDialog.defaults.expandBottomSheetInitially
) = showBottomSheetDialogFragment(activity.supportFragmentManager, expand)

fun <S : MaterialDialogSetup<S, B, E>, B : ViewBinding, E : IMaterialDialogEvent> MaterialDialogSetup<S, B, E>.showBottomSheetDialogFragment(
    fragment: Fragment,
    expand: Boolean = MaterialDialog.defaults.expandBottomSheetInitially
) = showBottomSheetDialogFragment(fragment.childFragmentManager, expand)

fun <S : MaterialDialogSetup<S, B, E>, B : ViewBinding, E : IMaterialDialogEvent> MaterialDialogSetup<S, B, E>.showBottomSheetDialogFragment(
    fragmentManager: FragmentManager,
    expand: Boolean = MaterialDialog.defaults.expandBottomSheetInitially
) {
    val f = MaterialDialogBottomSheetFragment.create(this as S, expand)
    f.show(fragmentManager, f::class.java.name)
}

internal class BottomSheetFragmentPresenter<S : MaterialDialogSetup<S, B, E>, B : ViewBinding, E : IMaterialDialogEvent>(
    private val setup: S,
    private val expand: Boolean,
    private val fragment: MaterialDialogBottomSheetFragment<S, B, E>
) : BaseMaterialDialogPresenter() {

    // ----------------
    // Fragment
    // ----------------

    private lateinit var rootBinding: MdfBottomSheetDialogBinding
    private lateinit var binding: B

    fun onCreate(savedInstanceState: Bundle?, activity: FragmentActivity, parentFragment: Fragment?) {
        setup.dismiss = { fragment.dismiss() }
        fragment.isCancelable = setup.cancelable
        onParentAvailable(activity, parentFragment)
        onLifecycleOwnerAvailable(fragment)
    }

    fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        rootBinding = MdfBottomSheetDialogBinding.inflate(inflater, container, false)
        return rootBinding.root
    }

    fun onViewCreated(dialog: Dialog?, view: View, savedInstanceState: Bundle?) {

        //val buttons = view.findViewById<View>(R.id.mdf_buttons)

        val containerContent = rootBinding.mdfContent
        binding = setup.viewManager.createContentViewBinding(
            LayoutInflater.from(view.context),
            containerContent,
            false
        )

        val v = MaterialDialogUtil.createContentView(setup, binding.root)
        containerContent.addView(v)
        setup.viewManager.initBinding(this, binding, savedInstanceState)

        val title = rootBinding.mdfTitle
        val icon = rootBinding.mdfIcon
        val toolbar = rootBinding.mdfToolbar
        icon.isEnabled = setup.cancelable

        val buttons = rootBinding.mdfBottomButtons

        buttons.adjustBackgroundToParent(rootBinding.root.parent as View)

        val buttonPositive = buttons.getButton(MaterialDialogButton.Positive)
        val buttonNegative = buttons.getButton(MaterialDialogButton.Negative)
        val buttonNeutral = buttons.getButton(MaterialDialogButton.Neutral)

        val viewTitle = ViewData.Title.TextView(title, icon, toolbar)
        val viewButtons = ViewData.Buttons(buttonPositive, buttonNegative, buttonNeutral)

        val viewData = ViewData(viewTitle, viewButtons) {
            fragment.dismiss()
        }
        viewData.init(this, binding, setup)

        initButtonsDragDependency(dialog)
        initInitialBottomSheetState(dialog)

        setup.menu?.let {
            MaterialDialogUtil.initToolbarMenu(this, rootBinding.mdfToolbar, binding, it, setup.eventManager)
        }
    }

    fun saveViewState(outState: Bundle) {
        setup.viewManager.saveViewState(binding, outState)
    }

    fun onCancelled() {
        setup.eventManager.onEvent(this, binding, MaterialDialogAction.Cancelled)
    }

    fun onBeforeDismiss(allowingStateLoss: Boolean): Boolean {
        setup.viewManager.onBeforeDismiss(binding)
        return true
    }

    override fun onDestroy() {
        setup.dismiss = null
        super.onDestroy()
    }

    private fun initButtonsDragDependency(dialog: Dialog?) {
        if (dialog is BottomSheetDialog) {
            val bottomSheet = rootBinding.root.parent as View
            val behavior: BottomSheetBehavior<*> = dialog.behavior

            behavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                }

                override fun onSlide(bottomSheet: View, slideOffset: Float) {
                    updateButtonsPosition(bottomSheet)
                }
            })
            updateButtonsPosition(bottomSheet, true)

            bottomSheet.addOnLayoutChangeListener { v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom ->
                if (oldTop != top || oldBottom != bottom)
                    updateButtonsPosition(bottomSheet, true)
            }
        }
    }

    private fun initInitialBottomSheetState(dialog: Dialog?) {
        if (expand && dialog is BottomSheetDialog) {
            val behavior: BottomSheetBehavior<*> = dialog.behavior
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
        }
    }
    fun updateButtonsPosition(bottomSheet: View, post: Boolean = false) {
        val update = {
            val y =
                ((bottomSheet.parent as View).height - bottomSheet.top - rootBinding.mdfBottomButtons.height).toFloat()
            val adjustment = if (y < rootBinding.mdfBottomButtons.height) {
                rootBinding.mdfBottomButtons.height - y
            } else 0f
            rootBinding.mdfBottomButtons.y = y + adjustment

        }
        if (post)
            rootBinding.root.post { update() }
        else
            update()
    }

    fun onBackPress() : Boolean {
        return setup.viewManager.onBackPress(binding)
    }
}