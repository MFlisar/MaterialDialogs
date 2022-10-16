package com.michaelflisar.dialogs

import android.content.Context
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
    activity: AppCompatActivity
) = showFullscreenFragment(activity.supportFragmentManager)

fun <S : MaterialDialogSetup<S, B, E>, B : ViewBinding, E : IMaterialDialogEvent> MaterialDialogSetup<S, B, E>.showFullscreenFragment(
    activity: FragmentActivity
) = showFullscreenFragment(activity.supportFragmentManager)

fun <S : MaterialDialogSetup<S, B, E>, B : ViewBinding, E : IMaterialDialogEvent> MaterialDialogSetup<S, B, E>.showFullscreenFragment(
    fragment: Fragment
) = showFullscreenFragment(fragment.childFragmentManager)

fun <S : MaterialDialogSetup<S, B, E>, B : ViewBinding, E : IMaterialDialogEvent> MaterialDialogSetup<S, B, E>.showFullscreenFragment(
    fragmentManager: FragmentManager
) {
    val f = MaterialFullscreenDialogFragment.create(this as S)
    f.show(fragmentManager, f::class.java.name)
}

class FullscreenFragmentPresenter<S : MaterialDialogSetup<S, B, E>, B : ViewBinding, E : IMaterialDialogEvent>(
    internal val setup: S,
    private val fragment: MaterialFullscreenDialogFragment<S, B, E>
) : BaseMaterialDialogPresenter() {

    private lateinit var binding: B

    fun onCreate(savedInstanceState: Bundle?, activity: FragmentActivity, parentFragment: Fragment?) {
        fragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialog)
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

        val viewTitle = ViewData.Title.Toolbar(toolbar, icon)
        val viewButtons = ViewData.Buttons(buttonPositive, buttonNegative, buttonNeutral)

        val viewData = ViewData(viewTitle, viewButtons) {
            fragment.dismiss()
        }
        viewData.init(this, binding, setup)

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

    override fun onDestroy() {
        setup.dismiss = null
        super.onDestroy()
    }

    fun onBackPress() : Boolean {
        return setup.viewManager.onBackPress(binding)
    }
}