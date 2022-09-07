package com.michaelflisar.dialogs.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.michaelflisar.dialogs.classes.GroupedColor
import com.michaelflisar.dialogs.classes.Payload
import com.michaelflisar.dialogs.color.R
import com.michaelflisar.dialogs.color.databinding.MdfRowColorBinding
import com.michaelflisar.dialogs.setCircleBackground
import com.michaelflisar.dialogs.tint
import com.michaelflisar.dialogs.utils.ColorUtil

internal class ColorAdapter(
    private val isLandscape: Boolean,
    private var groupedColor: GroupedColor,
    private var transparency: Int,
    private var selected: Int,
    private val listener: ((adapter: ColorAdapter, view: ColorViewHolder, color: Int, pos: Int) -> Unit)?
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    fun updateGroupColor(groupedColor: GroupedColor, resetSelection: Boolean) {
        this.groupedColor = groupedColor
        if (resetSelection) {
            this.selected = -1
        }
        notifyDataSetChanged()
    }

    fun updateSelection(selected: Int) {
        val oldSelection = this.selected
        if (oldSelection != selected) {
            this.selected = selected
            notifyItemChanged(oldSelection, Payload.SelectionChanged)
            notifyItemChanged(selected, Payload.SelectionChanged)
        }
    }

    fun setTransparency(transparency: Int) {
        val oldTransparency = this.transparency
        if (oldTransparency != transparency) {
            this.transparency = transparency
            notifyItemRangeChanged(0, itemCount, Payload.TransparencyChanged)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ColorViewHolder(inflater.inflate(R.layout.mdf_row_color, parent, false))
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

        val solidColor =
            groupedColor.getAdapterColor(holder.itemView.context, isLandscape, position)
        val color = solidColor?.let { ColorUtil.adjustAlpha(it, transparency) }

        if (payload.isNotEmpty()) {
            if (payload.contains(Payload.TransparencyChanged) && color != null)
                updateTransparency(vh, position, color, transparency)
            if (payload.contains(Payload.SelectionChanged))
                updateSelection(vh, position)
            return
        }

        if (color == null) {
            vh.binding.root.visibility = View.INVISIBLE
            vh.itemView.setOnClickListener(null)
        } else {

            vh.binding.root.visibility = View.VISIBLE

            updateTransparency(vh, position, color, transparency)
            updateSelection(vh, position)

            vh.binding.vColorForeground.setCircleBackground(solidColor, false)
            vh.itemView.setOnClickListener { view: View? ->
                listener?.invoke(this@ColorAdapter, vh, color, vh.adapterPosition)
            }
        }

        if (vh.oldPosition != position) {
            vh.binding.vSelectedBackground.setCircleBackground(Color.TRANSPARENT, true)
        }
    }

    private fun updateTransparency(
        vh: ColorViewHolder,
        position: Int,
        color: Int,
        transparency: Int
    ) {
        val colorNumber =
            groupedColor.getAdapterColorName(vh.itemView.context, isLandscape, position)
        val fgColor = ColorUtil.getBestTextColor(color)
        vh.binding.tvColorNumber.text = colorNumber
        vh.binding.tvColorNumber.setTextColor(fgColor)
        vh.binding.ivSelected.tint(fgColor)
        vh.binding.vColorForeground.alpha = transparency.toFloat() / 255f
    }

    private fun updateSelection(vh: ColorViewHolder, position: Int) {
        vh.binding.vSelectedBackground.visibility =
            if (position == selected) View.VISIBLE else View.INVISIBLE
        vh.binding.ivSelected.visibility =
            if (position == selected) View.VISIBLE else View.INVISIBLE
        vh.binding.tvColorNumber.visibility =
            if (position == selected) View.INVISIBLE else View.VISIBLE
    }

    override fun getItemCount(): Int {
        return groupedColor.geAdapterItemCount(isLandscape)
    }

    class ColorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var binding: MdfRowColorBinding = MdfRowColorBinding.bind(itemView)
    }
}