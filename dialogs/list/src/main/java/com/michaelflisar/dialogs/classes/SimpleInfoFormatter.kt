package com.michaelflisar.dialogs.classes

import com.michaelflisar.dialogs.interfaces.IListInfoFormatter
import kotlinx.parcelize.Parcelize

@Parcelize
class SimpleInfoFormatter(
    val labelSelected: String
) : IListInfoFormatter {
    override fun formatInfo(itemsTotal: Int, itemsFiltered: Int, itemsSelected: Int): String {
        return (if (itemsFiltered == itemsTotal) {
            itemsFiltered.toString()
        } else "$itemsFiltered / $itemsTotal") + if (itemsSelected > 0) {
            " ($labelSelected: $itemsSelected)"
        } else ""
    }
}