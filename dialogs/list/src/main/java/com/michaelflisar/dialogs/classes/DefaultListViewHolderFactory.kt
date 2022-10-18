package com.michaelflisar.dialogs.classes

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.children
import androidx.recyclerview.widget.RecyclerView
import com.michaelflisar.dialogs.DialogList
import com.michaelflisar.dialogs.interfaces.IListItem
import com.michaelflisar.dialogs.ListEventManager
import com.michaelflisar.dialogs.interfaces.IListviewHolderFactory
import com.michaelflisar.dialogs.interfaces.IMaterialDialogPresenter
import com.michaelflisar.dialogs.list.databinding.MdfDefaultListItemBinding
import kotlinx.parcelize.Parcelize

@Parcelize
internal object DefaultListViewHolderFactory : IListviewHolderFactory {

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
        presenter: IMaterialDialogPresenter<*>,
        adapter: ListItemAdapter,
        item: IListItem,
        holder: RecyclerView.ViewHolder,
        position: Int
    ) {
        (holder as DefaultListItemViewHolder).bind(presenter, adapter, item, emptyList())
    }

    fun onItemClicked(
        presenter: IMaterialDialogPresenter<*>,
        view: View,
        index: Int,
        item: IListItem,
        adapter: ListItemAdapter
    ) {
        val eventManager = adapter.setup.eventManager as ListEventManager
        when (adapter.setup.selectionMode) {
            is DialogList.SelectionMode.SingleSelect -> {
                val selectedId = adapter.getCheckedIds().firstOrNull()
                if (selectedId != null && selectedId != item.listItemId) {
                    adapter.setItemChecked(selectedId, false)
                }
                val selected = adapter.toggleItemChecked(item)
                if (selected && adapter.setup.selectionMode.dismissOnSelection)  {
                    eventManager.sendEvent(presenter, item)
                    presenter.dismiss?.invoke()
                }
            }
            is DialogList.SelectionMode.MultiSelect -> {
                adapter.toggleItemChecked(item)
            }
            DialogList.SelectionMode.SingleClick -> {
                eventManager.sendEvent(presenter, item)
                presenter.dismiss?.invoke()
            }
            DialogList.SelectionMode.MultiClick -> {
                eventManager.sendEvent(presenter, item)
            }
        }
    }

    fun onItemLongClicked(
        presenter: IMaterialDialogPresenter<*>,
        view: View,
        index: Int,
        item: IListItem,
        adapter: ListItemAdapter
    ) {
        val eventManager = adapter.setup.eventManager as ListEventManager
        eventManager.sendEvent(presenter, item, true)
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
                this.height = setup.items.iconSize
                this.width = setup.items.iconSize
            }

            when (setup.selectionMode) {
                is DialogList.SelectionMode.SingleSelect -> {
                    binding.mdfCheckbox.visibility = View.GONE
                }
                is DialogList.SelectionMode.MultiSelect -> {
                    binding.mdfRadiobutton.visibility = View.GONE
                }
                DialogList.SelectionMode.SingleClick,
                DialogList.SelectionMode.MultiClick -> {
                    binding.mdfCheckboxContainerRight.visibility = View.GONE
                }
            }
        }

        fun bind(presenter: IMaterialDialogPresenter<*>, adapter: ListItemAdapter, item: IListItem, payload: List<Any>) {

            val onlyUpdateTexts =
                payload.isNotEmpty() && payload.contains(ListItemAdapter.PAYLOAD_FILTER)

            if (!onlyUpdateTexts) {
                // 1) icon
                val icon = item.displayIcon(binding.mdfIconLeft)
                binding.mdfIconLeft.visibility = if (icon) View.VISIBLE else View.GONE
                // 2) checked state
                binding.mdfCheckbox.isChecked =
                    adapter.state.selectedIds.contains(item.listItemId)
                binding.mdfRadiobutton.isChecked =
                    adapter.state.selectedIds.contains(item.listItemId)
                // 3) click listener
                binding.root.setOnClickListener {
                    viewFactory.onItemClicked(presenter, it, bindingAdapterPosition, item, adapter)
                }
                binding.root.setOnLongClickListener {
                    viewFactory.onItemLongClicked(
                        presenter,
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

                val enabled = setup.disabledIds.contains(item.listItemId)
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