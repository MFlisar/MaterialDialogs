package com.michaelflisar.dialogs.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.michaelflisar.dialogs.classes.GroupedColor
import com.michaelflisar.dialogs.classes.Payload
import com.michaelflisar.dialogs.color.R
import com.michaelflisar.dialogs.color.databinding.MdfRowMainColorBinding
import com.michaelflisar.dialogs.setCircleBackground

internal class MainColorAdapter(
    private val items: Array<GroupedColor>,
    private var selected: Int,
    private val listener: ((adapter: MainColorAdapter, view: ColorViewHolder, color: GroupedColor, pos: Int) -> Unit)?
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    fun update(selected: Int) {
        val oldSelection = this.selected
        if (oldSelection != selected) {
            this.selected = selected
            notifyItemChanged(oldSelection, Payload.SelectionChanged)
            notifyItemChanged(selected, Payload.SelectionChanged)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ColorViewHolder(inflater.inflate(R.layout.mdf_row_main_color, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        onBindViewHolder(holder, position, emptyList())
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
        payload: List<Any>
    ) {
        val vh = holder as ColorViewHolder
        vh.binding.vSelectedBackground.visibility =
            if (position == selected) View.VISIBLE else View.INVISIBLE
        if (payload.size == 1 && payload.first() == Payload.SelectionChanged) {
            return
        }
        if (vh.oldPosition != position) {
            val groupColor = items[position]
            vh.binding.vSelectedBackground.setCircleBackground(Color.TRANSPARENT, true)
            groupColor.displayAsCircleViewBackground(vh.binding.vColor)
            vh.itemView.setOnClickListener { v: View? ->
                listener?.invoke(
                    this@MainColorAdapter,
                    vh,
                    groupColor,
                    vh.adapterPosition
                )
            }
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    class ColorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var binding: MdfRowMainColorBinding = MdfRowMainColorBinding.bind(itemView)
    }
}