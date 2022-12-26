package com.michaelflisar.dialogs.interfaces

import android.os.Parcelable
import android.view.View

interface IMaterialDialogAnimation : Parcelable {
    fun prepareShow(view: View)
    fun prepareHide(view: View)
    fun show(view: View, onShown: (() -> Unit)? = null)
    fun hide(view: View, onHidden: (() -> Unit)? = null)
}