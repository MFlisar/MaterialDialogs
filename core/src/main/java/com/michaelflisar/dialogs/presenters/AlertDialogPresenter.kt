package com.michaelflisar.dialogs.presenters

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.ScrollView
import androidx.appcompat.app.AlertDialog
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.viewbinding.ViewBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.michaelflisar.dialogs.MaterialDialogSetup
import com.michaelflisar.dialogs.MaterialDialogUtil
import com.michaelflisar.dialogs.classes.*
import com.michaelflisar.dialogs.core.R
import com.michaelflisar.dialogs.core.databinding.MdfDialogBinding
import com.michaelflisar.dialogs.interfaces.IMaterialDialogAnimation
import com.michaelflisar.dialogs.interfaces.IMaterialDialogEvent
import com.michaelflisar.text.Text

fun <S : MaterialDialogSetup<S>, E : IMaterialDialogEvent> MaterialDialogSetup<*>.showAlertDialog(
    context: Context,
    style: DialogStyle = DialogStyle(),
    callback: ((event: E) -> Unit)? = null
) = AlertDialogPresenter<S, E>(this as S).show(context, style, callback)

fun <S : MaterialDialogSetup<S>, E : IMaterialDialogEvent> MaterialDialogSetup<S>.showAlertDialog(
    parent: MaterialDialogParent,
    style: DialogStyle = DialogStyle(),
    callback: ((event: E) -> Unit)? = null
) = AlertDialogPresenter<S, E>(this as S).show(parent.context, style, callback)

fun <S : MaterialDialogSetup<S>> MaterialDialogSetup<S>.showAlertDialog(
    context: Context,
    style: DialogStyle = DialogStyle()
) = AlertDialogPresenter<S, IMaterialDialogEvent>(this as S).show(context, style)

fun <S : MaterialDialogSetup<S>> MaterialDialogSetup<S>.showAlertDialog(
    parent: MaterialDialogParent,
    style: DialogStyle = DialogStyle()
) = AlertDialogPresenter<S, IMaterialDialogEvent>(this as S).show(parent.context, style)

class AlertDialogPresenter<S : MaterialDialogSetup<S>, E : IMaterialDialogEvent>(
    override val setup: S
) : BaseMaterialDialogPresenter<S>(), LifecycleOwner {

    internal fun show(
        context: Context,
        style: DialogStyle,
        callback: ((event: E) -> Unit)?
    ) {
        this.callback = callback
        createDialog(context, style, null, null)
            .dialog
            .show()
    }

    internal fun show(
        context: Context,
        style: DialogStyle
    ) {
        show(context, style, null)
    }

    // ----------------
    // custom lifecylce
    // ----------------

    private var callback: ((event: E) -> Unit)? = null
    private var dismissedByEvent = false
    private var dismissing = false
    private var lifecycleRegistry: LifecycleRegistry? = null
    override fun getLifecycle(): Lifecycle = lifecycleRegistry ?: lifecycleOwner!!.lifecycle

    private var buttons = HashMap<MaterialDialogButton, Button>()

    //lateinit var viewManager: IMaterialViewManager<S, B>
    //lateinit var eventManager: IMaterialEventManager<S, B>

    // ----------------
    // Dialog
    // ----------------

    init {
        setup.viewManager.onPresenterAvailable(this)
    }

    fun createDialog(
        context: Context,
        style: DialogStyle,
        savedInstanceState: Bundle?,
        fragment: Fragment?
    ): DialogData {
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
        setup.viewManager.createContentViewBinding(layoutInflater, null, false)
        setup.viewManager.initBinding(savedInstanceState)
        val content = setup.viewManager.binding

        // 2) Set title and button(s) of the builder
        //    => NO TITLE, titles are part of the content layout in my design!
        //       REASON: I faced some issues when using title and icon in combination
        //               (could not get imageview of it to support custom image loader, ...)
        //       REASON2: we want to support menus so we use a toolbar instead of a simple title!
        val wrappedContent = wrapContentViewAndSetupView(layoutInflater, content, style)
        builder.setView(wrappedContent)
        initButtons(context, builder)
        builder.setCancelable(setup.cancelable)

        // 3) create dialog + furhter initialisation as soon as the dialog is shown
        val dlg = builder.create()
        dlg.setOnShowListener {
            if (setup.cancelable) {
                catchBackpress(dlg) { dismissDialog(dlg, style.animation) }
                catchTouchOutside(dlg) { dismissDialog(dlg, style.animation) }
            }
            style.animation?.show(dlg.window!!.decorView)
            this.dismiss = {
                dismissedByEvent = true
                dismissDialog(dlg, style.animation)
            }
            this.eventCallback = {
                callback?.invoke(it as E)
            }
            lifecycleRegistry?.handleLifecycleEvent(Lifecycle.Event.ON_START)
            initWhenDialogIsShown(dlg, context) {
                dismissDialog(dlg, style.animation)
            }
        }
        //dlg.setOnDismissListener {
        //    onDismiss(content)
        //}

        return DialogData(dlg)
    }

    // -----------------
    // helper functions
    // -----------------

    private fun wrapContentViewAndSetupView(
        layoutInflater: LayoutInflater,
        content: ViewBinding,
        style: DialogStyle
    ): View {
        val b = MdfDialogBinding.inflate(layoutInflater)

        // custom style
        MaterialDialogTitleViewManager.applyStyle(
            setup.title,
            setup.icon,
            b.root,
            b.mdfToolbar,
            b.mdfTitle,
            b.mdfToolbarIcon,
            b.mdfIcon,
            style.title,
            MaterialDialogUtil.dpToPx(4),
            MaterialDialogUtil.dpToPx(4)
        )

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

        setup.menu?.let {
            MaterialDialogUtil.initToolbarMenu(this, b.mdfToolbar, it, setup.eventManager)
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
        context: Context,
        dismiss: () -> Unit
    ) {
        buttons.clear()
        setup.buttonsData.forEach {
            val text = it.first
            val button = it.second
            text.get(context).takeIf { it.isNotEmpty() }?.let {
                val btn = dialog.getButton(button.alertButton)
                buttons[button] = btn
                btn.setOnClickListener {
                    if (setup.viewManager.onInterceptButtonClick(it, button)) {
                        // view manager wants to intercept this click => it can do whatever it wants with this event
                    } else if (setup.eventManager.onButton(this, button)) {
                        dismissedByEvent = true
                        dismiss()
                    }
                }
            }
        }
        setup.viewManager.onButtonsReady()
    }

    private fun catchBackpress(dlg: Dialog, onBackPress: () -> Unit) {
        dlg.setOnKeyListener { _, keyCode, keyEvent ->
            if (keyEvent.action == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                if (setup.viewManager.onBackPress())
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

    private fun onDismissed() {
        this.dismiss = null
        lifecycleRegistry?.handleLifecycleEvent(Lifecycle.Event.ON_STOP)
        lifecycleRegistry?.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        if (!dismissedByEvent) {
            setup.eventManager.onEvent(this, MaterialDialogAction.Cancelled)
        }
        this.eventCallback = null
        onDestroy()
    }

    private fun dismissDialog(
        dialog: Dialog,
        animation: IMaterialDialogAnimation?
    ): Boolean {
        if (dismissing)
            return false
        val callOnDismissed = true
        dismissing = true
        if (animation == null) {
            setup.viewManager.onBeforeDismiss()
            dialog.dismiss()
            if (callOnDismissed)
                onDismissed()
        } else {
            animation.prepareHide(dialog.window!!.decorView)
            animation.hide(dialog.window!!.decorView) {
                setup.viewManager.onBeforeDismiss()
                dialog.dismiss()
                if (callOnDismissed)
                    onDismissed()
            }
        }
        return true
    }

    override fun onDestroy() {
        setup.viewManager.onDestroy()
        super.onDestroy()
    }

    override fun setButtonEnabled(button: MaterialDialogButton, enabled: Boolean) {
        buttons.getOrDefault(button, null)?.isEnabled = enabled
    }

    override fun setButtonVisible(button: MaterialDialogButton, visible: Boolean) {
        buttons.getOrDefault(button, null)?.visibility = if (visible) View.VISIBLE else View.INVISIBLE
    }

    override fun setButtonText(button: MaterialDialogButton, text: CharSequence) {
        buttons.getOrDefault(button, null)?.text = text
    }

    // -----------------
    // helper class
    // -----------------

    class DialogData(
        val dialog: AlertDialog
    )
}