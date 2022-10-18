package com.michaelflisar.dialogs.classes

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.michaelflisar.dialogs.DialogList
import com.michaelflisar.dialogs.interfaces.IListItem
import com.michaelflisar.dialogs.interfaces.IMaterialDialogPresenter
import java.util.*

class ListItemAdapter(
    val presenter: IMaterialDialogPresenter<*, *>,
    val context: Context,
    val setup: DialogList,
    initialSelection: SortedSet<Long>,
    initialFilter: String,
    val onCheckedStateChanged: () -> Unit
) : ListAdapter<ListItemAdapter.ItemWrapper, RecyclerView.ViewHolder>(DiffCallback) {

    val state = State(initialSelection, initialFilter)

    private var unfilteredItems: List<IListItem> = emptyList()
    private var filteredItems: List<IListItem> = emptyList()

    val itemCountUnfiltered: Int
        get() = unfilteredItems.size

    companion object {
        const val PAYLOAD_FILTER = "PAYLOAD_FILTER"

        object DiffCallback : DiffUtil.ItemCallback<ItemWrapper>() {
            override fun areItemsTheSame(
                oldItem: ItemWrapper,
                newItem: ItemWrapper
            ): Boolean {
                return oldItem.item.listItemId == newItem.item.listItemId
            }

            override fun areContentsTheSame(
                oldItem: ItemWrapper,
                newItem: ItemWrapper
            ): Boolean {
                return oldItem == newItem
            }

            override fun getChangePayload(oldItem: ItemWrapper, newItem: ItemWrapper): Any? {
                if (oldItem.item == newItem.item && oldItem.filter != newItem.filter)
                    return PAYLOAD_FILTER
                return null
            }
        }
    }

    override fun getItemViewType(position: Int) = setup.viewFactory.getItemViewType(position)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        setup.viewFactory.createViewHolder(setup, parent, viewType)

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) =
        setup.viewFactory.bindViewHolder(presenter, this, getItem(position).item, holder, position)

    fun updateItems(items: List<IListItem>, callback: (() -> Unit)? = null) {
        unfilteredItems = items
        submitFilteredList(callback)
    }

    fun updateFilter(filter: String, callback: (() -> Unit)? = null) {
        state.filter = filter
        submitFilteredList(callback)
    }

    private fun submitFilteredList(callback: (() -> Unit)?) {
        // 1) calculate filtered items
        filteredItems = if (setup.filter == null) {
            unfilteredItems
        } else {
            unfilteredItems.filter { setup.filter.matches(context, it, state.filter) }
        }
        // 2) remove all selected not visible items... its not obvious to the user that those are selected if they are not visible!
        // TODO: events could only emit "visible selection" alternatively...
        if (setup.filter?.unselectInvisibleItems == true) {
            val invalidSelectedIds = unfilteredItems
                .map { it.listItemId }
                .toMutableSet()
                .let {
                    it.removeAll(filteredItems.map { it.listItemId }.toSet())
                    it
                }
            state.selectedIds.removeAll(invalidSelectedIds)
        }
        // 3) update adapter
        submitList(filteredItems.map { ItemWrapper(it, state.filter) }) {
            callback?.invoke()
        }
    }

    fun setItemChecked(item: IListItem, checked: Boolean) {
        setItemChecked(item.listItemId, checked)
    }

    fun setItemChecked(id: Long, checked: Boolean) {
        if (checked)
            state.selectedIds.add(id)
        else {
            state.selectedIds.remove(id)
        }
        val index = filteredItems.indexOfFirst { it.listItemId == id }
        notifyItemChanged(index)
        onCheckedStateChanged()
    }

    fun getCheckedIds(): SortedSet<Long> = state.selectedIds

    fun getCheckedItemsForResult(): List<IListItem> {
        val items = if (setup.filter?.unselectInvisibleItems == false) {
            // in this case there may be more items selected than visible => we base the result on the visible items only here!
            filteredItems
        } else unfilteredItems

        return state.selectedIds
            .map { id -> items.find { it.listItemId == id } }
            .filterNotNull()
            .toList()
    }

    fun toggleItemChecked(item: IListItem): Boolean {
        val isChecked = state.selectedIds.contains(item.listItemId)
        setItemChecked(item, !isChecked)
        return !isChecked
    }

    data class ItemWrapper(
        val item: IListItem,
        val filter: String
    )

    class State(
        val selectedIds: SortedSet<Long>,
        var filter: String
    )
}