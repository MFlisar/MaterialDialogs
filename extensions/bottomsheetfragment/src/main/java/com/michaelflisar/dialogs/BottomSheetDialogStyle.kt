package com.michaelflisar.dialogs

import android.content.Context
import android.content.res.Configuration
import android.os.Parcelable
import com.michaelflisar.dialogs.classes.MaterialDialogTitleStyle
import com.michaelflisar.dialogs.interfaces.IMaterialDialogAnimation
import kotlinx.parcelize.Parcelize

@Parcelize
class BottomSheetDialogStyle(
    val title: MaterialDialogTitleStyle = DEFAULT_TITLE,
    val expand: Expand = DEFAULT_EXPAND,
    val peekHeight: Int? = DEFAULT_PEEK_HEIGHT
) : Parcelable {

    companion object {
        val DEFAULT_TITLE = MaterialDialogTitleStyle.LargeTextWithIconCentered
        val DEFAULT_EXPAND = Expand(false, false)
        val DEFAULT_PEEK_HEIGHT: Int? = null
    }

    @Parcelize
    class Expand(
        val landscape: Boolean = false,
        val portrait: Boolean = false
    ) : Parcelable {
        fun expand(context: Context): Boolean {
            val isLandscape = context.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
            return if (isLandscape) landscape else portrait
        }
    }
}