package com.michaelflisar.dialogs.classes

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.michaelflisar.dialogs.MaterialDialogUtil
import com.michaelflisar.dialogs.debug.R
import com.michaelflisar.dialogs.debug.databinding.MdfRowDebugItemBinding

internal class DebugAdapter(
    private val manager: DebugDataManager,
    private val items: List<DebugItem<*>>,
    private val context: Context,
    private val withNumbering: Boolean,
    val onItemClicked: (item: DebugItem<*>, index: Int) -> Unit
) : RecyclerView.Adapter<DebugAdapter.ViewHolder>() {

    private var level = 0
    private var selectedIndices = arrayListOf<Int>()

    private val multiCheckMark by lazy {
        val typedValueAttr = TypedValue()
        context.theme.resolveAttribute(
            android.R.attr.listChoiceIndicatorMultiple,
            typedValueAttr,
            true
        )
        typedValueAttr.resourceId
    }

    override fun getItemCount(): Int {
        return getItems(selectedIndices).size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.mdf_row_debug_item, parent, false),
            this
        )
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(selectedIndices, position)
        val text = when (item) {
            is DebugItem.List -> item.name + " [" + item.getEntryByValue(manager.getInt(item)).name + "]"
            else -> item.name
        }
        val number = if (withNumbering) getNumber(position) else ""
        holder.binding.text.text = "$number$text"

        val checked = when (item) {
            is DebugItem.Checkbox -> manager.getBool(item)
            is DebugItem.ListEntry -> manager.getInt(item.parentPrefName, item.parentDefaultValue) == item.value
            else -> false
        }

        val iconTint =
            if (MaterialDialogUtil.isCurrentThemeDark(holder.binding.root.context)) Color.WHITE else Color.BLACK

        if (item is DebugItem.Group || item is DebugItem.List) {
            holder.binding.text.setCheckMarkDrawable(R.drawable.ic_baseline_arrow_forward_24)
            holder.binding.text.checkMarkTintList = ColorStateList.valueOf(iconTint)
        } else if (item is DebugItem.Checkbox) {
            holder.binding.text.setCheckMarkDrawable(multiCheckMark)
        } else {
            if (checked) {
                holder.binding.text.setCheckMarkDrawable(R.drawable.ic_baseline_check_24)
                holder.binding.text.checkMarkTintList = ColorStateList.valueOf(iconTint)
            } else {
                holder.binding.text.checkMarkDrawable = null
            }
        }

        holder.binding.text.isChecked = checked
    }

    private fun getItems(selectedTopIndices: List<Int>): List<DebugItem<*>> {
        var entries: List<DebugItem<*>> = items
        for (i in selectedTopIndices) {
            entries = (entries[i] as DebugItem.SubEntryHolder<*>).subEntries
        }
        return entries
    }

    private fun getItem(selectedTopIndices: List<Int>, index: Int): DebugItem<*> {
        return getItems(selectedTopIndices)[index]
    }

    private fun getParentItem(selectedTopIndices: List<Int>): DebugItem<*>? {
        if (selectedTopIndices.isEmpty()) {
            return null
        }
        var entries: List<DebugItem<*>> = items
        for (i in 0 until selectedTopIndices.size - 1) {
            entries = (entries[selectedTopIndices[i]] as DebugItem.SubEntryHolder<*>).subEntries
        }

        return entries[selectedTopIndices[selectedTopIndices.size - 1]]
    }

    @SuppressLint("NotifyDataSetChanged")
    internal fun goLevelUp(updateSubTitle: (parentEntry: DebugItem<*>?, number: String?) -> Unit): Boolean {
        return if (level > 0) {
            level--
            selectedIndices.removeAt(level)
            notifyDataSetChanged()
            updateSubTitle(
                getParentItem(selectedIndices),
                if (withNumbering) getNumber(-1) else null
            )
            true
        } else false
    }

    @SuppressLint("NotifyDataSetChanged")
    internal fun goLevelDown(
        index: Int,
        updateSubTitle: (parentEntry: DebugItem<*>?, number: String?) -> Unit
    ) {
        level++
        selectedIndices.add(index)
        notifyDataSetChanged()
        updateSubTitle(
            getParentItem(selectedIndices),
            if (withNumbering) getNumber(-1) else null
        )
    }

    private fun getNumber(pos: Int): String {
        var n = selectedIndices.joinToString(separator = ".") { "${it + 1}" }
        if (pos != -1)
            n += (if (selectedIndices.size > 0) "." else "") + "${pos + 1} "
        return n
    }

    // --------------------
    // ViewHolder
    // --------------------

    class ViewHolder(
        view: View,
        private val adapter: DebugAdapter
    ) : RecyclerView.ViewHolder(view), View.OnClickListener {

        val binding: MdfRowDebugItemBinding = MdfRowDebugItemBinding.bind(view).apply {
            root.setOnClickListener(this@ViewHolder)
        }

        override fun onClick(v: View?) {
            val item = adapter.getItem(adapter.selectedIndices, adapterPosition)
            adapter.onItemClicked(item, adapterPosition)
        }
    }
}