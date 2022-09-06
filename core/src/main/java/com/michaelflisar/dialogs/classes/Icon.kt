package com.michaelflisar.dialogs.classes

import android.os.Parcelable
import android.widget.ImageView
import com.michaelflisar.dialogs.MaterialDialog
import com.michaelflisar.dialogs.interfaces.IMaterialDialogImageLoader
import kotlinx.parcelize.Parcelize

fun Int.asMaterialDialogIcon() = Icon.Resource(this)
fun Parcelable.asMaterialDialogIcon() = Icon.Data(this)
fun String.asMaterialDialogIcon() = Icon.String(this)

sealed class Icon : Parcelable {

    abstract fun display(imageView: ImageView): Boolean

    protected fun ensureLoader(): IMaterialDialogImageLoader {
        return MaterialDialog.imageLoader
            ?: throw RuntimeException("To use custom image data you need to provide your own loader and initialise the 'MaterialDialog.imageLoader' variable!")
    }

    @Parcelize
    class Resource(val res: Int, val tintInTextColor: Boolean? = null) : Icon() {
        override fun display(imageView: ImageView): Boolean {
            MaterialDialog.imageLoader?.display(imageView, this) ?: imageView.setImageResource(res)
            return true
        }
    }

    @Parcelize
    class String(val data: kotlin.String) : Icon() {
        override fun display(imageView: ImageView) = ensureLoader().display(imageView, this)
    }

    @Parcelize
    class Data(val data: Parcelable) : Icon() {
        override fun display(imageView: ImageView) = ensureLoader().display(imageView, this)
    }

    @Parcelize
    object None : Icon() {
        override fun display(imageView: ImageView) = false
    }
}