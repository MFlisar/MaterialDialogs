package com.michaelflisar.dialogs.classes

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

sealed class MaterialDialogKey : Parcelable {

    abstract val classEvent: Class<*>

    @Parcelize
    data class Simple(override val classEvent: Class<*>) : MaterialDialogKey()

    @Parcelize
    data class ID(override val classEvent: Class<*>, val id: Int) : MaterialDialogKey()
}
