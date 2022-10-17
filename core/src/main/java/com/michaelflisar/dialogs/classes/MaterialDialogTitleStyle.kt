package com.michaelflisar.dialogs.classes

import android.view.Gravity

enum class MaterialDialogTitleStyle {
    SmallTextWithIconCentered,
    LargeTextWithIconCentered,
    SmallTextWithIconInlined,
    LargeTextWithIconInlined
    ;

    fun hasSmallText() = when (this) {
        SmallTextWithIconCentered,
        SmallTextWithIconInlined -> true
        LargeTextWithIconCentered,
        LargeTextWithIconInlined -> false
    }

    fun hasInlinedIcon() = when (this) {
        SmallTextWithIconInlined,
        LargeTextWithIconInlined -> true
        SmallTextWithIconCentered,
        LargeTextWithIconCentered -> false
    }

    fun getTextGravity(): Int = when(this) {
        SmallTextWithIconCentered,
        LargeTextWithIconCentered -> Gravity.CENTER
        SmallTextWithIconInlined ,
        LargeTextWithIconInlined -> Gravity.START or Gravity.CENTER_VERTICAL
    }

}