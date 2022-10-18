package com.michaelflisar.dialogs.classes

import android.view.View
import android.widget.ImageView
import androidx.viewbinding.ViewBinding
import com.google.android.material.appbar.MaterialToolbar
import com.michaelflisar.dialogs.MaterialDialogSetup
import com.michaelflisar.text.Text

sealed class TitleViews {

    abstract fun init(text: Text, icon: Icon)

    fun <S : MaterialDialogSetup<S, B>, B: ViewBinding> init(setup: S) {
        init(setup.title, setup.icon)
    }

    protected fun displayIcon(image: ImageView, icon: Icon) {
        val hasIcon = icon.display(image)
        image.visibility = if (hasIcon) View.VISIBLE else View.GONE
    }

    class Toolbar(val toolbar: MaterialToolbar, val image: ImageView) : TitleViews() {
        override fun init(text: Text, icon: Icon) {
            text.display(toolbar) { view, text ->
                view.title = text
            }
            displayIcon(image, icon)
        }
    }

    class TextView(val textView: android.widget.TextView, val image: ImageView, val toolbar: MaterialToolbar? = null) : TitleViews() {
        override fun init(text: Text, icon: Icon) {
            val s = text.display(textView)
            textView.visibility = if (s.isEmpty()) View.GONE else View.VISIBLE
            toolbar?.visibility = textView.visibility
            displayIcon(image, icon)
        }
    }
}