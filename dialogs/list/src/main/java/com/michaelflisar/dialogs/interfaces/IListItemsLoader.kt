package com.michaelflisar.dialogs.interfaces

import android.content.Context
import android.os.Parcelable

interface IListItemsLoader : Parcelable {
    suspend fun load(context: Context): List<IListItem>
}