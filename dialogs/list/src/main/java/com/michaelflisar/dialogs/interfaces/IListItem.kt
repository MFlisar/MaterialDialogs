package com.michaelflisar.dialogs.interfaces

import android.os.Parcelable
import android.widget.ImageView
import com.michaelflisar.text.Text

interface IListItem : Parcelable {
    val id: Long
    val text: Text
    val subText: Text
    fun displayIcon(imageView: ImageView): Boolean
    override fun equals(other: Any?): Boolean
}