package com.michaelflisar.dialogs

import android.os.Parcelable
import com.michaelflisar.dialogs.interfaces.IMaterialDialogAnimation
import kotlinx.parcelize.Parcelize

@Parcelize
class FullscreenDialogStyle(
    val icon: Icon = DEFAULT_ICON
) : Parcelable {

    companion object {
        val DEFAULT_ICON = Icon.Center
    }

    enum class Icon {
        Center
    }
}