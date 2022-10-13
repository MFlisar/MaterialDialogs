package com.michaelflisar.dialogs.classes

import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.viewbinding.ViewBinding
import com.google.android.material.appbar.MaterialToolbar
import com.michaelflisar.dialogs.MaterialDialogSetup
import com.michaelflisar.text.Text

class ViewData(
    val title: Title,
    val buttons: Buttons,
    val dismiss: () -> Unit
) {

    fun <S : MaterialDialogSetup<S, B, *>, B: ViewBinding> init(binding: B, setup: S) {

        // Title
        this.title.init(setup.title, setup.icon)

        // Buttons
        initButton(binding, setup, MaterialDialogButton.Positive)
        initButton(binding, setup, MaterialDialogButton.Negative)
        initButton(binding, setup, MaterialDialogButton.Neutral)

    }

    private fun <S : MaterialDialogSetup<S, B, *>, B: ViewBinding> initButton(binding: B, setup: S, buttonType: MaterialDialogButton) {
        val buttonText = setup.getButtonText(buttonType)
        val button = buttons.getButton(buttonType)
        if (buttonText.isEmpty(binding.root.context)) {
            button.visibility = View.GONE
        } else {
            buttonText.display(button) { view, text ->
                view.text = text
            }
            button.setOnClickListener {
                if (setup.viewManager.onInterceptButtonClick(it, buttonType)) {
                    // view manager wants to intercept this click => it can do whatever it wants with this event
                } else if (setup.eventManager.onButton(binding, buttonType as MaterialDialogButton))
                    dismiss()
            }
        }
    }

    class Buttons(
        val buttonPositive: Button,
        val buttonNegative: Button,
        val buttonNeutral: Button
    ) {

        fun getButton(buttonType: MaterialDialogButton): Button {
            return when (buttonType) {
                MaterialDialogButton.Positive -> buttonPositive
                MaterialDialogButton.Negative -> buttonNegative
                MaterialDialogButton.Neutral -> buttonNeutral
            }
        }
    }

    sealed class Title {

        abstract fun init(text: Text, icon: Icon)

        protected fun displayIcon(image: ImageView, icon: Icon) {
            val hasIcon = icon.display(image)
            image.visibility = if (hasIcon) View.VISIBLE else View.GONE
        }

        class Toolbar(val toolbar: MaterialToolbar, val image: ImageView) : Title() {
            override fun init(text: Text, icon: Icon) {
                text.display(toolbar) { view, text ->
                    view.title = text
                }
                displayIcon(image, icon)
            }
        }

        class TextView(val textView: android.widget.TextView, val image: ImageView) : Title() {
            override fun init(text: Text, icon: Icon) {
                val s = text.display(textView)
                textView.visibility = if (s.length == 0) View.GONE else View.VISIBLE
                displayIcon(image, icon)
            }
        }
    }
}