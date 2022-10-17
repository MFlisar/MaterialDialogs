package com.michaelflisar.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.viewbinding.ViewBinding
import com.google.android.material.appbar.MaterialToolbar
import com.michaelflisar.dialogs.classes.*
import com.michaelflisar.dialogs.fullscreenfragment.R
import com.michaelflisar.dialogs.interfaces.IMaterialDialogEvent
import com.michaelflisar.dialogs.views.ButtonsView

fun <S : MaterialDialogSetup<S, B, E>, B : ViewBinding, E : IMaterialDialogEvent> MaterialDialogSetup<S, B, E>.showFullscreenFragment(
    activity: AppCompatActivity,
    style: FullscreenDialogStyle = FullscreenDialogStyle()
) = showFullscreenFragment(activity.supportFragmentManager, style)

fun <S : MaterialDialogSetup<S, B, E>, B : ViewBinding, E : IMaterialDialogEvent> MaterialDialogSetup<S, B, E>.showFullscreenFragment(
    activity: FragmentActivity,
    style: FullscreenDialogStyle = FullscreenDialogStyle()
) = showFullscreenFragment(activity.supportFragmentManager, style)

fun <S : MaterialDialogSetup<S, B, E>, B : ViewBinding, E : IMaterialDialogEvent> MaterialDialogSetup<S, B, E>.showFullscreenFragment(
    fragment: Fragment,
    style: FullscreenDialogStyle = FullscreenDialogStyle()
) = showFullscreenFragment(fragment.childFragmentManager, style)

fun <S : MaterialDialogSetup<S, B, E>, B : ViewBinding, E : IMaterialDialogEvent> MaterialDialogSetup<S, B, E>.showFullscreenFragment(
    fragmentManager: FragmentManager,
    style: FullscreenDialogStyle = FullscreenDialogStyle()
) {
    val f = MaterialFullscreenDialogFragment.create(this as S, style)
    f.show(fragmentManager, f::class.java.name)
}

fun <S : MaterialDialogSetup<S, B, E>, B : ViewBinding, E : IMaterialDialogEvent> MaterialDialogSetup<S, B, E>.showDialogFragment(
    parent: MaterialDialogParent,
    style: FullscreenDialogStyle = FullscreenDialogStyle()
) {
    when (parent) {
        is MaterialDialogParent.Activity -> showFullscreenFragment(parent.activity, style)
        is MaterialDialogParent.Context -> {
            throw RuntimeException("This presenter needs an activity or fragment parent context!")
        }
        is MaterialDialogParent.Fragment -> showFullscreenFragment(parent.fragment, style)
    }
}

class FullscreenFragmentPresenter<S : MaterialDialogSetup<S, B, E>, B : ViewBinding, E : IMaterialDialogEvent>(
    private val setup: S,
    private val style: FullscreenDialogStyle,
    private val fragment: MaterialFullscreenDialogFragment<S, B, E>
) : BaseMaterialDialogPresenter() {

    private lateinit var binding: B

    fun onCreate(
        savedInstanceState: Bundle?,
        activity: FragmentActivity,
        parentFragment: Fragment?
    ) {
        fragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialog)
        this.dismiss = { fragment.dismiss() }
        fragment.isCancelable = setup.cancelable
        onParentAvailable(activity, parentFragment)
        onLifecycleOwnerAvailable(fragment)
    }

    fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.mdf_fullscreen_dialog, container, false)
    }

    fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        val containerContent = view.findViewById<FrameLayout>(R.id.mdf_content)
        val layoutInflater = LayoutInflater.from(containerContent.context)
        binding =
            setup.viewManager.createContentViewBinding(layoutInflater, containerContent, false)
        val v = MaterialDialogUtil.createContentView(setup, binding.root)
        containerContent.addView(v)
        setup.viewManager.initBinding(this, binding, savedInstanceState)

        val toolbar = view.findViewById<MaterialToolbar>(R.id.mdf_toolbar)
        if (setup.cancelable) {
            toolbar.setNavigationIcon(R.drawable.mdf_close)
            toolbar.setNavigationOnClickListener {
                fragment.dismiss()
                setup.eventManager.onEvent(this, binding, MaterialDialogAction.Cancelled)
            }
        }
        val icon = view.findViewById<ImageView>(R.id.mdf_icon)

        val buttons = view.findViewById<ButtonsView>(R.id.mdf_bottom_buttons)
        buttons.setButtonVisibility(MaterialDialogButton.Positive, View.GONE)

        val buttonPositive = view.findViewById<Button>(R.id.mdf_button_positive_toolbar)
        val buttonNegative = buttons.getButton(MaterialDialogButton.Negative)
        val buttonNeutral = buttons.getButton(MaterialDialogButton.Neutral)

        val viewTitle = TitleViews.Toolbar(toolbar, icon)
        val viewButtons = ButtonViews(buttonPositive, buttonNegative, buttonNeutral)

        viewTitle.init(setup)
        viewButtons.init(this, binding, setup) {
            fragment.dismiss()
        }

        setup.menu?.let {
            MaterialDialogUtil.initToolbarMenu(this, toolbar, binding, it, setup.eventManager)
        }
    }

    fun onStart() {
        fragment.dialog!!.let {
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.MATCH_PARENT
            it.window!!.setLayout(width, height)
            it.window!!.setWindowAnimations(android.R.style.Animation_Dialog)
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

    fun onBackPress(): Boolean {
        return setup.viewManager.onBackPress(binding)
    }
}