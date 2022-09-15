package com.michaelflisar.dialogs.interfaces

import android.content.Context
import android.os.Parcelable

interface INumberFormatter<T> : Parcelable {
    fun format(context: Context, value: T): String
}