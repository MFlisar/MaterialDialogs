package com.michaelflisar.dialogs.classes

import android.os.Parcelable
import com.michaelflisar.dialogs.interfaces.INumberFormatter
import kotlinx.parcelize.Parcelize

@Parcelize
data class NumberSetup<T : Number>(
    val min: T,
    val max: T,
    val step: T,
    val formatter: INumberFormatter<T>? = null
) : Parcelable {
    fun isValid(value: T): Boolean {
        return when (min) {
            is Int -> value as Int >= min as Int && value as Int <= max as Int
            is Long -> value as Long >= min as Long && value as Long <= max as Long
            is Float -> value as Float >= min as Float && value as Float <= max as Float
            is Double -> value as Double >= min as Double && value as Double <= max as Double
            else -> throw RuntimeException()
        }
    }
}