package com.michaelflisar.dialogs.apps

import android.content.pm.ResolveInfo
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class App(
    val name: String,
    val resolveInfo: ResolveInfo
) : Parcelable