package com.michaelflisar.dialogs.interfaces

import android.content.Context
import android.os.Parcelable
import android.widget.TextView
import com.michaelflisar.dialogs.interfaces.IListItem

interface IListFilter : Parcelable {
    val unselectInvisibleItems: Boolean
    fun matches(context: Context, item: IListItem, filter: String): Boolean
    fun displayText(tv: TextView, item: IListItem, filter: String): CharSequence
    fun displaySubText(tv: TextView, item: IListItem, filter: String): CharSequence
}