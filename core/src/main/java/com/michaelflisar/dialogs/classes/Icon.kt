package com.michaelflisar.dialogs.classes

import android.os.Parcelable
import android.widget.ImageView
import com.michaelflisar.dialogs.MaterialDialog
import com.michaelflisar.dialogs.interfaces.IMaterialDialogImageLoader
import kotlinx.parcelize.Parcelize

fun Int.asMaterialDialogIcon(tintInTextColor: Boolean = true) = Icon.Resource(this, tintInTextColor)
fun Parcelable.asMaterialDialogIcon(tintInTextColor: Boolean = true) = Icon.Data(this, tintInTextColor)
fun String.asMaterialDialogIcon(tintInTextColor: Boolean = true) = Icon.String(this, tintInTextColor)

sealed class Icon : Parcelable {

    abstract val tintInTextColor: Boolean
    abstract fun display(imageView: ImageView): Boolean

    protected fun ensureLoader(): IMaterialDialogImageLoader {
        return MaterialDialog.imageLoader
            ?: throw RuntimeException("To use custom image data you need to provide your own loader and initialise the 'MaterialDialog.imageLoader' variable!")
    }

    @Parcelize
    class Resource(val res: Int, override val tintInTextColor: Boolean) : Icon() {
        override fun display(imageView: ImageView): Boolean {
            MaterialDialog.imageLoader?.display(imageView, this) ?: imageView.setImageResource(res)
            return true
        }
    }

    @Parcelize
    class String(val data: kotlin.String, override val tintInTextColor: Boolean) : Icon() {
        override fun display(imageView: ImageView) = ensureLoader().display(imageView, this)
    }

    @Parcelize
    class Data(val data: Parcelable, override val tintInTextColor: Boolean) : Icon() {
        override fun display(imageView: ImageView) = ensureLoader().display(imageView, this)
    }

    @Parcelize
    object None : Icon() {
        override val tintInTextColor = false
        override fun display(imageView: ImageView) = false
    }
}