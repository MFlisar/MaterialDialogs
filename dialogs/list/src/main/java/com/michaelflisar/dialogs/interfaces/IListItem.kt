package com.michaelflisar.dialogs.interfaces

import android.os.Parcelable
import android.widget.ImageView
import com.michaelflisar.text.Text

interface IListItem : Parcelable {
    val text: Text
    val subText: Text
    // instead of "val id: Long" => avoids problems with other interfaces that may use int or long ids
    fun getListIdentifier(): Long
    fun displayIcon(imageView: ImageView): Boolean
    override fun equals(other: Any?): Boolean
}