package com.michaelflisar.dialogs.interfaces

import android.content.Context
import android.os.Parcelable

interface IAdsPrefs : Parcelable {
    fun getLastShowPolicyDate(context: Context): Long
    fun saveLastShowPolicyDate(context: Context, date: Long): Boolean
    fun getLastPolicyCounter(context: Context): Int
    fun setLastPolicyCounter(context: Context, counter: Int): Boolean
}