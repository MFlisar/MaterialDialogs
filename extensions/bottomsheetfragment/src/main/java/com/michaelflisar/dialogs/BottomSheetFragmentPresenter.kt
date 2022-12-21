package com.michaelflisar.dialogs

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.michaelflisar.dialogs.bottomsheetfragments.databinding.MdfBottomSheetDialogBinding
import com.michaelflisar.dialogs.classes.*

fun <S : MaterialDialogSetup<S>> MaterialDialogSetup<S>.showBottomSheetDialogFragment(
    activity: FragmentActivity,
    style: BottomSheetDialogStyle = BottomSheetDialogStyle()
) = showBottomSheetDialogFragment(activity.supportFragmentManager, style)

fun <S : MaterialDialogSetup<S>> MaterialDialogSetup<S>.showBottomSheetDialogFragment(
    fragment: Fragment,
    style: BottomSheetDialogStyle = BottomSheetDialogStyle()
) = showBottomSheetDialogFragment(fragment.childFragmentManager, style)

fun <S : MaterialDialogSetup<S>> MaterialDialogSetup<S>.showBottomSheetDialogFragment(
    fragmentManager: FragmentManager,
    style: BottomSheetDialogStyle = BottomSheetDialogStyle()
) {
    @Suppress("UNCHECKED_CAST")
    val f = MaterialDialogBottomSheetFragment.create(this as S, style)
    f.show(fragmentManager, f::class.java.name)
}

fun <S : MaterialDialogSetup<S>> MaterialDialogSetup<S>.showDialogFragment(
    parent: MaterialDialogParent,
    style: BottomSheetDialogStyle = BottomSheetDialogStyle()
) {
    when (parent) {
        is MaterialDialogParent.Activity -> showBottomSheetDialogFragment(parent.activity, style)
        is MaterialDialogParent.Context -> {
            throw RuntimeException("This presenter needs an activity or fragment parent context!")
        }
        is MaterialDialogParent.Fragment -> showBottomSheetDialogFragment(parent.fragment, style)
    }
}

internal class BottomSheetFragmentPresenter<S : MaterialDialogSetup<S>>(
    override val setup: S,
    private val style: BottomSheetDialogStyle,
    private val fragment: MaterialDialogBottomSheetFragment<S>
) : BaseMaterialDialogPresenter<S>() {

    // ----------------
    // Fragment
    // ----------------

    private lateinit var rootBinding: MdfBottomSheetDialogBinding
    private lateinit var buttonViews: ButtonViews

    fun onCreate(
        savedInstanceState: Bundle?,
        activity: FragmentActivity,
        parentFragment: Fragment?
    ) {
        this.dismiss = { fragment.dismiss() }
        fragment.isCancelable = setup.cancelable
        onParentAvailable(activity, parentFragment)
        onLifecycleOwnerAvailable(fragment)
        setup.viewManager.onPresenterAvailable(this)
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
        setup.viewManager.createContentViewBinding(
            LayoutInflater.from(view.context),
            containerContent,
            false
        )

        val content = setup.viewManager.binding

        val v = MaterialDialogUtil.createContentView(setup, content.root)
        containerContent.addView(v)
        setup.viewManager.initBinding(savedInstanceState)

        val title = rootBinding.mdfTitle
        val icon = rootBinding.mdfIcon
        val toolbar = rootBinding.mdfToolbar
        icon.isEnabled = setup.cancelable

        val buttons = rootBinding.mdfBottomButtons

        buttons.adjustBackgroundToParent(rootBinding.root.parent as View)

        buttonViews = ButtonViews(
            buttons.getButton(MaterialDialogButton.Positive),
            buttons.getButton(MaterialDialogButton.Negative),
            buttons.getButton(MaterialDialogButton.Neutral)
        )

        // custom style
        MaterialDialogTitleViewManager.applyStyle(
            setup.title,
            setup.icon,
            rootBinding.root,
            rootBinding.mdfToolbar,
            rootBinding.mdfTitle,
            rootBinding.mdfToolbarIcon,
            rootBinding.mdfIcon,
            style.title,
            MaterialDialogUtil.dpToPx(4),
            MaterialDialogUtil.dpToPx(4),
        )

        buttonViews.init(this, content, setup) {
            fragment.dismiss()
        }
        setup.viewManager.onButtonsReady()

        initButtonsDragDependency(dialog)
        initInitialBottomSheetState(content.root, dialog)

        setup.menu?.let {
            MaterialDialogUtil.initToolbarMenu(
                this,
                rootBinding.mdfToolbar,
                it,
                setup.eventManager
            )
        }
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

    private fun initInitialBottomSheetState(view: View, dialog: Dialog?) {
        if (dialog is BottomSheetDialog && style.expand.expand(view.context)) {
            val behavior: BottomSheetBehavior<*> = dialog.behavior
            style.peekHeight?.let {
                behavior.peekHeight = it
            }
            // post to animate this change
            view.post {
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
            }
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

    fun onBackPress(): Boolean {
        return setup.viewManager.onBackPress()
    }

    override fun setButtonEnabled(button: MaterialDialogButton, enabled: Boolean) {
        buttonViews.getButton(button).isEnabled = enabled
    }

    override fun setButtonVisible(button: MaterialDialogButton, visible: Boolean) {
        buttonViews.getButton(button).visibility = if (visible) View.VISIBLE else View.INVISIBLE
    }

    override fun setButtonText(button: MaterialDialogButton, text: CharSequence) {
        buttonViews.getButton(button).text = text
    }
}