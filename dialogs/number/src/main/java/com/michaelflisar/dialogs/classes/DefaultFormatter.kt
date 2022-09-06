package com.michaelflisar.dialogs.classes

import android.content.Context
import com.michaelflisar.dialogs.DialogNumber
import kotlinx.parcelize.Parcelize

@Parcelize
class DefaultFormatter<T>(
    val stringRes: Int
) : DialogNumber.Formatter<T> {

    override fun format(context: Context, value: T): String {
        return context.getString(stringRes, value)
    }
}