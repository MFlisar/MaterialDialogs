package com.michaelflisar.dialogs.classes

import android.content.Context
import android.os.Parcelable
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Parcelize
data class GDPRSubNetwork(
    val name: String,
    val link: String
) : Parcelable {

    constructor(context: Context, name: Int, link: Int) : this(
        context.getString(name),
        context.getString(link)
    )

    @IgnoredOnParcel
    val htmlLink = "<a href=\"$link\">$name</a>"
}