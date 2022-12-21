package com.michaelflisar.dialogs.classes

import android.os.Parcelable
import com.michaelflisar.dialogs.gdpr.R
import com.michaelflisar.text.Text
import com.michaelflisar.text.asText
import kotlinx.parcelize.Parcelize

@Parcelize
data class GDPRCustomTexts(
    val question: Text? = null,
    val mainMsg: Text? = null,
    val topMsg: Text? = null,
    val ageMsg: Text? = null
) : Parcelable
