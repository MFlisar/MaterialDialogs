package com.michaelflisar.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.viewbinding.ViewBinding
import com.michaelflisar.dialogs.bottomsheetfragments.R
import com.michaelflisar.dialogs.classes.ViewData
import com.michaelflisar.dialogs.interfaces.IMaterialDialogEvent

fun <S : MaterialDialogSetup<S, B, E>, B : ViewBinding, E: IMaterialDialogEvent> MaterialDialogSetup<S, B, E>.showBottomSheetDialogFragment(
    activity: FragmentActivity
) = showBottomSheetDialogFragment(activity.supportFragmentManager)

fun <S : MaterialDialogSetup<S, B, E>, B : ViewBinding, E: IMaterialDialogEvent> MaterialDialogSetup<S, B, E>.showBottomSheetDialogFragment(
    fragment: Fragment
) = showBottomSheetDialogFragment(fragment.childFragmentManager)

private fun <S : MaterialDialogSetup<S, B, E>, B : ViewBinding, E: IMaterialDialogEvent> MaterialDialogSetup<S, B, E>.showBottomSheetDialogFragment(
    fragmentManager: FragmentManager
) {
    val f = MaterialDialogBottomSheetFragment.create(this as S)
    f.show(fragmentManager, f::class.java.name)
}

internal class BottomSheetFragmentPresenter<S : MaterialDialogSetup<S, B, E>, B : ViewBinding, E: IMaterialDialogEvent>(
    private val setup: S,
    private val fragment: MaterialDialogBottomSheetFragment<S, B, E>
) {

    // ----------------
    // Fragment
    // ----------------

    private lateinit var binding: B

    fun onCreate(savedInstanceState: Bundle?) {
        setup.dismiss = { fragment.dismiss() }
        fragment.isCancelable = setup.cancelable
    }

    fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.mdf_bottom_sheet_dialog, container, false)
    }

    fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        //val buttons = view.findViewById<View>(R.id.mdf_buttons)

        val containerContent = view.findViewById<FrameLayout>(R.id.mdf_content)
        binding = setup.viewManager.createContentViewBinding(
            LayoutInflater.from(view.context),
            containerContent,
            false
        )
        //fragment.setBindingInstance(content)
        val v = MaterialDialogUtil.createContentView(setup, binding.root)
        containerContent.addView(v)
        setup.viewManager.initBinding(fragment, binding, savedInstanceState)

        val title = view.findViewById<TextView>(R.id.mdf_title)
        val icon = view.findViewById<ImageView>(R.id.mdf_icon)
        icon.isEnabled = setup.cancelable

        val buttonPositive = view.findViewById<Button>(R.id.mdf_button_positive)
        val buttonNegative = view.findViewById<Button>(R.id.mdf_button_negative)
        val buttonNeutral = view.findViewById<Button>(R.id.mdf_button_neutral)

        val viewTitle = ViewData.Title.TextView(title, icon)
        val viewButtons = ViewData.Buttons(buttonPositive, buttonNegative, buttonNeutral)

        val viewData = ViewData(viewTitle, viewButtons) {
            fragment.dismiss()
        }
        viewData.init(binding, setup)
    }

    fun saveViewState(outState: Bundle) {
        setup.viewManager.saveViewState(binding, outState)
    }

    fun onCancelled() {
        setup.eventManager.onCancelled()
    }

    fun onBeforeDismiss(allowingStateLoss: Boolean): Boolean {
        setup.viewManager.onBeforeDismiss(binding)
        return true
    }

    fun onDestroy() {
        setup.dismiss = null
    }
}