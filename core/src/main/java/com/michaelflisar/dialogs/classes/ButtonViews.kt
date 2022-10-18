package com.michaelflisar.dialogs.classes

import android.view.View
import android.widget.Button
import androidx.viewbinding.ViewBinding
import com.michaelflisar.dialogs.MaterialDialogSetup
import com.michaelflisar.dialogs.interfaces.IMaterialDialogPresenter

class ButtonViews(
    val buttonPositive: Button,
    val buttonNegative: Button,
    val buttonNeutral: Button
) {

    fun <S : MaterialDialogSetup<S, B, *>, B: ViewBinding> init(presenter: IMaterialDialogPresenter<S, B, *>, binding: B, setup: S, dismiss: () -> Unit) {
        // Buttons
        initButton(presenter, binding, setup, MaterialDialogButton.Positive, dismiss)
        initButton(presenter, binding, setup, MaterialDialogButton.Negative, dismiss)
        initButton(presenter, binding, setup, MaterialDialogButton.Neutral, dismiss)
    }

    private fun <S : MaterialDialogSetup<S, B, *>, B: ViewBinding> initButton(presenter: IMaterialDialogPresenter<S, B, *>, binding: B, setup: S, buttonType: MaterialDialogButton, dismiss: () -> Unit) {
        val buttonText = setup.getButtonText(buttonType)
        val button = getButton(buttonType)
        if (buttonText.isEmpty(binding.root.context)) {
            button.visibility = View.GONE
        } else {
            buttonText.display(button) { view, text ->
                view.text = text
            }
            button.setOnClickListener {
                if (setup.viewManager.onInterceptButtonClick(it, buttonType)) {
                    // view manager wants to intercept this click => it can do whatever it wants with this event
                } else if (setup.eventManager.onButton(
                        presenter,
                        buttonType as MaterialDialogButton
                    ))
                    dismiss()
            }
        }
    }

    private fun getButton(buttonType: MaterialDialogButton): Button {
        return when (buttonType) {
            MaterialDialogButton.Positive -> buttonPositive
            MaterialDialogButton.Negative -> buttonNegative
            MaterialDialogButton.Neutral -> buttonNeutral
        }
    }
}