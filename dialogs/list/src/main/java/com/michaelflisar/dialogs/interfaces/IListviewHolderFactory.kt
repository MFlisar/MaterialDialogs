package com.michaelflisar.dialogs.interfaces

import android.os.Parcelable
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.michaelflisar.dialogs.DialogList
import com.michaelflisar.dialogs.classes.ListItemAdapter
import com.michaelflisar.dialogs.classes.MaterialDialogParent

interface IListviewHolderFactory : Parcelable {
    fun getItemViewType(position: Int): Int
    fun createViewHolder(
        setup: DialogList,
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder

    fun bindViewHolder(
        presenter: IMaterialDialogPresenter<*>,
        adapter: ListItemAdapter,
        item: IListItem,
        holder: RecyclerView.ViewHolder,
        position: Int
    )
}