package com.michaelflisar.dialogs.interfaces

import android.os.Parcelable
import android.view.View

interface IMaterialDialogAnimation : Parcelable {
    fun prepare(view: View)
    fun show(view: View, onShown: (() -> Unit)? = null)
    fun hide(view: View, onHidden: (() -> Unit)? = null)
}