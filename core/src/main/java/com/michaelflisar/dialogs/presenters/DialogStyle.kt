package com.michaelflisar.dialogs.presenters

import android.os.Parcelable
import com.michaelflisar.dialogs.classes.MaterialDialogTitleStyle
import com.michaelflisar.dialogs.interfaces.IMaterialDialogAnimation
import kotlinx.parcelize.Parcelize

@Parcelize
class DialogStyle(
    val title: MaterialDialogTitleStyle = DEFAULT_TITLE,
    val animation: IMaterialDialogAnimation? = DEFAULT_ANIMATION
) : Parcelable {

    companion object {
        val DEFAULT_TITLE = MaterialDialogTitleStyle.LargeTextWithIconCentered
        val DEFAULT_ANIMATION: IMaterialDialogAnimation? = null
    }

}