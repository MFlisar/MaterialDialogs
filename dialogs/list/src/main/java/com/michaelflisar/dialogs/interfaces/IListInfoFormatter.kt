package com.michaelflisar.dialogs.interfaces

import android.os.Parcelable

interface IListInfoFormatter : Parcelable {
    fun formatInfo(itemsTotal: Int, itemsFiltered: Int, itemsSelected: Int): String
}