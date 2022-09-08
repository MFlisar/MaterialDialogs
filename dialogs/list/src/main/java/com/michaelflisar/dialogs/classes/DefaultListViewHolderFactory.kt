package com.michaelflisar.dialogs.classes

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.children
import androidx.recyclerview.widget.RecyclerView
import com.michaelflisar.dialogs.DialogList
import com.michaelflisar.dialogs.ListEventManager
import com.michaelflisar.dialogs.MaterialDialogUtil
import com.michaelflisar.dialogs.interfaces.IListviewHolderFactory
import com.michaelflisar.dialogs.list.databinding.MdfDefaultListItemBinding
import kotlinx.parcelize.Parcelize

@Parcelize
object DefaultListViewHolderFactory : IListviewHolderFactory {

    override fun getItemViewType(position: Int): Int {
        return 1
    }

    override fun createViewHolder(
        setup: DialogList,
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        val binding =
            MdfDefaultListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DefaultListItemViewHolder(this, setup, binding)
    }

    override fun bindViewHolder(
        adapter: ListItemAdapter,
        item: DialogList.ListItem,
        holder: RecyclerView.ViewHolder,
        position: Int
    ) {
        (holder as DefaultListItemViewHolder).bind(adapter, item, emptyList())
    }

    fun onItemClicked(
        view: View,
        index: Int,
        item: DialogList.ListItem,
        adapter: ListItemAdapter
    ) {
        val eventManager = adapter.setup.eventManager as ListEventManager
        when (adapter.setup.selectionMode) {
            is DialogList.SelectionMode.SingleSelect -> {
                val selectedId = adapter.getCheckedIds().firstOrNull()
                if (selectedId != null && selectedId != item.id) {
                    adapter.setItemChecked(selectedId, false)
                }
                adapter.toggleItemChecked(item)
            }
            is DialogList.SelectionMode.MultiSelect -> {
                adapter.toggleItemChecked(item)
            }
            DialogList.SelectionMode.SingleClick -> {
                eventManager.sendEvent(item)
                adapter.setup.dismiss?.invoke()
            }
            DialogList.SelectionMode.MultiClick -> {
                eventManager.sendEvent(item)
            }
        }
    }

    fun onItemLongClicked(
        view: View,
        index: Int,
        item: DialogList.ListItem,
        adapter: ListItemAdapter
    ) {
        val eventManager = adapter.setup.eventManager as ListEventManager
        eventManager.sendEvent(item, true)
    }

    // ------------
    // ViewHolder
    // ------------

    private class DefaultListItemViewHolder(
        private val viewFactory: DefaultListViewHolderFactory,
        private val setup: DialogList,
        private val binding: MdfDefaultListItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.mdfIconLeft.layoutParams.apply {
                this.height = setup.itemsProvider.iconSize
                this.width = setup.itemsProvider.iconSize
            }

            val icon = when (setup.selectionMode) {
                is DialogList.SelectionMode.SingleSelect -> MaterialDialogUtil.getThemeReference(
                    binding.root.context,
                    android.R.attr.listChoiceIndicatorSingle
                )
                is DialogList.SelectionMode.MultiSelect -> MaterialDialogUtil.getThemeReference(
                    binding.root.context,
                    android.R.attr.listChoiceIndicatorMultiple
                )
                DialogList.SelectionMode.SingleClick -> null
                DialogList.SelectionMode.MultiClick -> null
            }

            if (icon == null)
                binding.mdfCheckboxRight.visibility = View.GONE
            else
                binding.mdfCheckboxRight.setButtonDrawable(icon)
        }

        fun bind(adapter: ListItemAdapter, item: DialogList.ListItem, payload: List<Any>) {

            val onlyUpdateTexts =
                payload.isNotEmpty() && payload.contains(ListItemAdapter.Companion.PAYLOAD_FILTER)

            if (!onlyUpdateTexts) {
                // 1) icon
                val icon = item.displayIcon(binding.mdfIconLeft)
                binding.mdfIconLeft.visibility = if (icon) View.VISIBLE else View.GONE
                // 2) checked state
                binding.mdfCheckboxRight.isChecked =
                    adapter.state.selectedIds.contains(item.id)
                // 3) click listener
                binding.root.setOnClickListener {
                    viewFactory.onItemClicked(it, bindingAdapterPosition, item, adapter)
                }
                binding.root.setOnLongClickListener {
                    viewFactory.onItemLongClicked(
                        it,
                        bindingAdapterPosition,
                        item,
                        adapter
                    )
                    true
                }
            }

            if (setup.disabledIds.isNotEmpty()) {
                var getSubView: ((view: View) -> List<View>)? = null
                getSubView = { view: View ->
                    val list = ArrayList<View>()
                    if (view is ViewGroup) {
                        view.children.forEach {
                            list.addAll(getSubView!!(it))
                        }
                    } else
                        list.add(view)
                    list
                }
                val subViews = getSubView(binding.root)

                val enabled = setup.disabledIds.contains(item.id)
                binding.root.isEnabled = enabled
                subViews.forEach {
                    it.isEnabled = enabled
                }
            }

            // 4) main text
            if (setup.filter == null)
                item.text.display(binding.mdfText)
            else
                setup.filter.displayText(binding.mdfText, item, adapter.state.filter)

            // 5) optional sub text
            val subText = if (setup.filter == null)
                item.subText.display(binding.mdfSubText)
            else
                setup.filter.displaySubText(
                    binding.mdfSubText,
                    item,
                    adapter.state.filter
                )
            binding.mdfSubText.visibility = if (subText.isEmpty()) View.GONE else View.VISIBLE
        }
    }
}