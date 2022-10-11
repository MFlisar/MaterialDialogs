package com.michaelflisar.dialogs.interfaces

import android.os.Parcelable
import android.widget.ImageView
import com.michaelflisar.text.Text

interface IListItem : Parcelable {

    // instead of "val id: Long" => avoids problems with other interfaces that may use int or long ids
    // so are more special name is better here...
    val listItemId: Long

    val text: Text
    val subText: Text
    fun displayIcon(imageView: ImageView): Boolean
    override fun equals(other: Any?): Boolean
}