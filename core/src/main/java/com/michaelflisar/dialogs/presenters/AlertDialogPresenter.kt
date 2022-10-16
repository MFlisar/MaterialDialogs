package com.michaelflisar.dialogs.presenters

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.ScrollView
import androidx.appcompat.app.AlertDialog
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.viewbinding.ViewBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.michaelflisar.dialogs.MaterialDialog
import com.michaelflisar.dialogs.MaterialDialogSetup
import com.michaelflisar.dialogs.MaterialDialogUtil
import com.michaelflisar.dialogs.classes.BaseMaterialDialogPresenter
import com.michaelflisar.dialogs.classes.MaterialDialogAction
import com.michaelflisar.dialogs.classes.MaterialDialogParent
import com.michaelflisar.dialogs.core.R
import com.michaelflisar.dialogs.core.databinding.MdfDialogBinding
import com.michaelflisar.dialogs.interfaces.IMaterialDialogAnimation
import com.michaelflisar.dialogs.interfaces.IMaterialDialogEvent
import com.michaelflisar.dialogs.interfaces.IMaterialViewManager

fun <S : MaterialDialogSetup<S, B, E>, B : ViewBinding, E : IMaterialDialogEvent> MaterialDialogSetup<S, B, E>.showAlertDialog(
    context: Context,
    animation: IMaterialDialogAnimation? = MaterialDialog.defaults.animation,
    callback: ((event: E) -> Unit)? = null
) = AlertDialogPresenter(this as S).show(context, animation, callback)

class AlertDialogPresenter<S : MaterialDialogSetup<S, B, E>, B : ViewBinding, E : IMaterialDialogEvent>(
    val setup: S
) : BaseMaterialDialogPresenter(), LifecycleOwner {

    internal fun show(
        context: Context,
        animation: IMaterialDialogAnimation?,
        callback: ((event: E) -> Unit)?
    ) {
        this.callback = callback
        createDialog(context, animation, null, null)
            .dialog
            .show()
    }

    // ----------------
    // custom lifecylce
    // ----------------

    private var callback: ((event: E) -> Unit)? = null
    private var dismissedByEvent = false
    private var dismissing = false
    private var lifecycleRegistry: LifecycleRegistry? = null
    override fun getLifecycle(): Lifecycle = lifecycleRegistry ?: lifecycleOwner!!.lifecycle

    lateinit var viewManager: IMaterialViewManager<S, B>

    // ----------------
    // Dialog
    // ----------------

    fun createDialog(
        context: Context,
        animation: IMaterialDialogAnimation?,
        savedInstanceState: Bundle?,
        fragment: Fragment?
    ): DialogData<B> {
        if (fragment != null) {
            this.lifecycleOwner = fragment
        } else {
            this.lifecycleOwner = this
            lifecycleRegistry = LifecycleRegistry(this)
            lifecycleRegistry?.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
        }

        onLifecycleOwnerAvailable(this.lifecycleOwner!!)
        if (fragment == null) {
            onParentAvailable(MaterialDialogParent.create(context))
        } else {
            onParentAvailable(fragment.requireActivity(), fragment.parentFragment)
        }

        // 1) init builder, layoutInflater and content view
        val builder = MaterialAlertDialogBuilder(context)
        val layoutInflater = LayoutInflater.from(builder.context)
        val content = setup.viewManager.createContentViewBinding(layoutInflater, null, false)
        setup.viewManager.initBinding(this, content, savedInstanceState)

        // 2) Set title and button(s) of the builder
        //    => NO TITLE, titles are part of the content layout in my design!
        //       REASON: I faced some issues when using title and icon in combination
        //               (could not get imageview of it to support custom image loader, ...)
        //       REASON2: we want to support menus so we use a toolbar instead of a simple title!
        val wrappedContent = wrapContentViewAndSetupMenu(layoutInflater, content)
        builder.setView(wrappedContent)
        initButtons(context, builder)
        builder.setCancelable(setup.cancelable)

        // 3) create dialog + furhter initialisation as soon as the dialog is shown
        val dlg = builder.create()
        dlg.setOnShowListener {
            catchBackpress(dlg, content) { dismissDialog(dlg, content, animation) }
            catchTouchOutside(dlg) { dismissDialog(dlg, content, animation) }
            animation?.show(dlg.window!!.decorView)
            this.dismiss = {
                dismissedByEvent = true
                dismissDialog(dlg, content, animation)
            }
            this.eventCallback = {
                callback?.invoke(it as E)
            }
            lifecycleRegistry?.handleLifecycleEvent(Lifecycle.Event.ON_START)
            initWhenDialogIsShown(dlg, content) {
                dismissDialog(dlg, content, animation)
            }
        }
        //dlg.setOnDismissListener {
        //    onDismiss(content)
        //}

        return DialogData(dlg, content)
    }

    // -----------------
    // helper functions
    // -----------------

    private fun wrapContentViewAndSetupMenu(layoutInflater: LayoutInflater, content: B): View {
        val b = MdfDialogBinding.inflate(layoutInflater)
        // if we have no buttons, the MaterialDialogBuilder won't add a bottom padding for us so we do this manually here
        if (setup.buttonsData.count { it.first.get(layoutInflater.context).length > 0 } == 0) {
            b.root.updatePadding(bottom = layoutInflater.context.resources.getDimensionPixelSize(R.dimen.mdf_dialog_content_bottom_margin_no_buttons))
        }
        b.mdfContent.addView(content.root)
        if (setup.viewManager.wrapInScrollContainer) {
            val scrollView = ScrollView(layoutInflater.context)
            val lp = b.mdfContent.layoutParams
            val parent = b.mdfContent.parent as ViewGroup
            val index = parent.indexOfChild(b.mdfContent)
            parent.removeView(b.mdfContent)
            scrollView.addView(
                b.mdfContent,
                ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
            )
            parent.addView(scrollView, index, lp)
        }

        val title = setup.title.display(b.mdfTitle)
        b.mdfTitle.visibility = if (title.isNotEmpty()) View.VISIBLE else View.GONE
        b.mdfToolbar.visibility = b.mdfTitle.visibility

        val hasIcon = setup.icon.display(b.mdfIcon)
        b.mdfIcon.visibility = if (hasIcon) View.VISIBLE else View.GONE

        // if we have a icon, we center the title text as well
        if (hasIcon) {
            b.mdfTitle.gravity = Gravity.CENTER
        }

        setup.menu?.let {
            MaterialDialogUtil.initToolbarMenu(this, b.mdfToolbar, content, it, setup.eventManager)
        }

        return b.root
    }

    private fun initButtons(
        context: Context,
        builder: MaterialAlertDialogBuilder
    ) {
        // if we set button click listeners here, we cant control if the dialog gets dismissed or not!
        setup.buttonPositive.get(context).takeIf { it.isNotEmpty() }
            ?.let {
                builder.setPositiveButton(it, null)
            }
        setup.buttonNegative.get(context).takeIf { it.isNotEmpty() }
            ?.let {
                builder.setNegativeButton(it, null)
            }
        setup.buttonNeutral.get(context).takeIf { it.isNotEmpty() }
            ?.let {
                builder.setNeutralButton(it, null)
            }
    }

    private fun initWhenDialogIsShown(
        dialog: AlertDialog,
        binding: B,
        dismiss: () -> Unit
    ) {
        setup.buttonsData.forEach {
            val text = it.first
            val button = it.second
            text.get(binding.root.context).takeIf { it.isNotEmpty() }?.let {
                dialog.getButton(button.alertButton).setOnClickListener {
                    if (setup.viewManager.onInterceptButtonClick(it, button)) {
                        // view manager wants to intercept this click => it can do whatever it wants with this event
                    } else if (setup.eventManager.onButton(this, binding, button)) {
                        dismissedByEvent = true
                        dismiss()
                    }
                }
            }
        }
    }

    private fun catchBackpress(dlg: Dialog, binding: B, onBackPress: () -> Unit) {
        dlg.setOnKeyListener { _, keyCode, keyEvent ->
            if (keyEvent.action == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                if (setup.viewManager.onBackPress(binding))
                    true
                else {
                    onBackPress()
                    true
                }
            } else false
        }
    }

    @SuppressLint("ClickableViewAccessibility", "RestrictedApi")
    private fun catchTouchOutside(dlg: Dialog, onBackPress: () -> Unit) {
        val decorView = dlg.window!!.decorView
        val contentView = decorView.findViewById<View>(android.R.id.content)
        val bounds = MaterialDialogUtil.getBoundsOnScreen(contentView)
        decorView.setOnTouchListener { view, event ->
            val rawX = event.rawX
            val rawY = event.rawY
            if (rawX < bounds.left || rawX > bounds.right || rawY < bounds.top || rawY > bounds.bottom) {
                onBackPress()
                true
            } else {
                false
            }
        }
    }

    private fun onDismissed(binding: B) {
        this.dismiss = null
        lifecycleRegistry?.handleLifecycleEvent(Lifecycle.Event.ON_STOP)
        lifecycleRegistry?.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        if (!dismissedByEvent) {
            setup.eventManager.onEvent(this, binding, MaterialDialogAction.Cancelled)
        }
        this.eventCallback = null
        onDestroy()
    }

    private fun dismissDialog(
        dialog: Dialog,
        binding: B,
        animation: IMaterialDialogAnimation?
    ): Boolean {
        if (dismissing)
            return false
        val callOnDismissed = true
        dismissing = true
        if (animation == null) {
            setup.viewManager.onBeforeDismiss(binding)
            dialog.dismiss()
            if (callOnDismissed)
                onDismissed(binding)
        } else {
            animation.prepare(dialog.window!!.decorView)
            animation.hide(dialog.window!!.decorView) {
                setup.viewManager.onBeforeDismiss(binding)
                dialog.dismiss()
                if (callOnDismissed)
                    onDismissed(binding)
            }
        }
        return true
    }
    // -----------------
    // helper class
    // -----------------

    class DialogData<B : ViewBinding>(
        val dialog: AlertDialog,
        val binding: B
    )
}