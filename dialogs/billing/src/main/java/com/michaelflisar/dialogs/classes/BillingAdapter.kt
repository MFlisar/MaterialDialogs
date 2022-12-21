package com.michaelflisar.dialogs.classes

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.michaelflisar.dialogs.DialogBilling
import com.michaelflisar.dialogs.billing.R
import com.michaelflisar.dialogs.billing.databinding.MdfBillingProductItemBinding

class BillingAdapter(
    val onClickListener: (item: Item, pos: Int) -> Unit
) : RecyclerView.Adapter<BillingAdapter.ViewHolder>() {

    var items: List<Item> = emptyList()
        private set

    fun update(items: List<Item>) {
        this.items = items
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            MdfBillingProductItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(this, binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    class ViewHolder(
        val adapter: BillingAdapter,
        val binding: MdfBillingProductItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                adapter.onClickListener(adapter.items[adapterPosition], adapterPosition)
            }
        }

        fun bind(item: Item, ) {
            binding.mdfInfo.text = item.label
            if (item.owned == true) {
                binding.mdfInfo2.visibility = View.VISIBLE
                binding.mdfInfo2.setText(R.string.mdf_billing_item_already_owned)
            } else {
                binding.mdfInfo2.visibility = View.GONE
            }
            binding.mdfPrice.text = item.price
            item.product.icon.display(binding.mdfIcon)
        }
    }

    class Item(
        val product: DialogBilling.BillingProduct,
        val label: String,
        val price: String?,
        val owned: Boolean?
    )
}
