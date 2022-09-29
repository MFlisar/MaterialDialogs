package com.michaelflisar.dialogs.items

import android.view.View
import com.michaelflisar.dialogs.app.R
import com.michaelflisar.dialogs.app.databinding.DemoItemBinding
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.items.AbstractItem

class DemoItem(
    val title: String,
    val desc: String,
    val function: ((view: View) -> Unit)
) : AbstractItem<DemoItem.ViewHolder>() {

    override val type = R.id.fast_adapter_demo_item
    override val layoutRes = R.layout.demo_item

    override fun getViewHolder(v: View): ViewHolder {
        return ViewHolder(v)
    }

    class ViewHolder(view: View) : FastAdapter.ViewHolder<DemoItem>(view) {

        private val binding: DemoItemBinding

        init {
            binding = DemoItemBinding.bind(view)
        }

        override fun bindView(item: DemoItem, payloads: List<Any>) {
            binding.title.text = item.title
            binding.desc.text = item.desc
        }

        override fun unbindView(item: DemoItem) {
            binding.title.text = null
            binding.desc.text = null
        }
    }
}