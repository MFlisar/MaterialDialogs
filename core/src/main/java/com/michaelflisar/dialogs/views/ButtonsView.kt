package com.michaelflisar.dialogs.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import com.google.android.material.shape.MaterialShapeDrawable
import com.michaelflisar.dialogs.classes.MaterialDialogButton
import com.michaelflisar.dialogs.core.databinding.MdfButtonViewBinding

class ButtonsView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : LinearLayout(context, attrs) {

    val binding: MdfButtonViewBinding

    init {
        orientation = HORIZONTAL
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding = MdfButtonViewBinding.inflate(inflater, this)
    }

    // -----------------
    // helper functions
    // -----------------

    fun adjustBackgroundToParent(parent: View) {
        background = MaterialShapeDrawable.createWithElevationOverlay(context, parent.elevation)
    }

    fun setButtonVisibility(button: MaterialDialogButton.DialogButton, visibility: Int) {
        getButton(button).visibility = visibility
    }

    fun getButton(button: MaterialDialogButton.DialogButton): Button {
        return when (button) {
            MaterialDialogButton.Positive -> binding.mdfButtonPositive
            MaterialDialogButton.Negative -> binding.mdfButtonNegative
            MaterialDialogButton.Neutral -> binding.mdfButtonNeutral
        }
    }
}