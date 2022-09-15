package com.michaelflisar.dialogs.classes

import android.os.Parcelable
import com.michaelflisar.dialogs.MaterialDialogUtil
import com.michaelflisar.dialogs.interfaces.IListItem
import com.michaelflisar.dialogs.interfaces.IListItemsLoader
import kotlinx.parcelize.Parcelize
import java.util.ArrayList

sealed class ItemProvider : Parcelable {

    abstract val iconSize: Int

    @Parcelize
    class List constructor(
        val items: ArrayList<IListItem>,
        override val iconSize: Int = MaterialDialogUtil.dpToPx(40)
    ) : ItemProvider()

    @Parcelize
    class ItemLoader(
        val loader: IListItemsLoader,
        override val iconSize: Int = MaterialDialogUtil.dpToPx(40)
    ) : ItemProvider()
}