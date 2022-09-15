package com.michaelflisar.dialogs.classes

import android.content.Context
import com.michaelflisar.dialogs.interfaces.INumberFormatter
import kotlinx.parcelize.Parcelize

@Parcelize
class DefaultFormatter<T>(
    val stringRes: Int
) : INumberFormatter<T> {

    override fun format(context: Context, value: T): String {
        return context.getString(stringRes, value)
    }
}