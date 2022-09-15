package com.michaelflisar.dialogs.interfaces

import android.content.Context
import android.os.Parcelable

interface IInputValidator : Parcelable {
    fun isValid(input: String): Boolean
    fun getError(context: Context, input: String): String
}