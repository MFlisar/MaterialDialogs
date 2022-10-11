package com.michaelflisar.dialogs.classes

import android.widget.ImageView
import com.michaelflisar.dialogs.interfaces.IListItem
import com.michaelflisar.text.Text
import kotlinx.parcelize.Parcelize

@Parcelize
data class SimpleListItem(
    val id: Long,
    override val text: Text,
    override val subText: Text = Text.Empty,
    val resIcon: Int? = null
) : IListItem {
    override fun getListIdentifier() = id
    override fun displayIcon(imageView: ImageView): Boolean {
        resIcon?.let { imageView.setImageResource(it) }
        return resIcon != null
    }
}