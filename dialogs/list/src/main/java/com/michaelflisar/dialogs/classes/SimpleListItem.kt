package com.michaelflisar.dialogs.classes

import android.widget.ImageView
import com.michaelflisar.dialogs.interfaces.IListItem
import com.michaelflisar.text.Text
import kotlinx.parcelize.Parcelize

@Parcelize
data class SimpleListItem(
    override val listItemId: Long,
    override val text: Text,
    override val subText: Text = Text.Empty,
    val resIcon: Int? = null
) : IListItem {
    override fun displayIcon(imageView: ImageView): Boolean {
        resIcon?.let { imageView.setImageResource(it) }
        return resIcon != null
    }
}