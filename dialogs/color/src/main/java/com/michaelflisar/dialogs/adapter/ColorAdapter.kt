package com.michaelflisar.dialogs.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.ColorUtils
import androidx.recyclerview.widget.RecyclerView
import com.michaelflisar.dialogs.classes.ColorDefinitions
import com.michaelflisar.dialogs.classes.GroupedColor
import com.michaelflisar.dialogs.classes.IColor
import com.michaelflisar.dialogs.classes.Payload
import com.michaelflisar.dialogs.color.R
import com.michaelflisar.dialogs.color.databinding.MdfRowColorBinding
import com.michaelflisar.dialogs.setCircleBackground
import com.michaelflisar.dialogs.tint
import com.michaelflisar.dialogs.utils.ColorUtil

internal class ColorAdapter(
    private var colors: List<IColor>,
    private var transparency: Int,
    private var selected: Int?,
    private val listener: ((adapter: ColorAdapter, view: ColorViewHolder, color: IColor, pos: Int) -> Unit)?
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    @SuppressLint("NotifyDataSetChanged")
    fun update(colors: List<IColor>, clearSelection: Boolean = false) {
        if (clearSelection)
            this.selected = null
        this.colors = colors
        notifyDataSetChanged()
    }

    fun indexOfSolidColor(context: Context, color: Int) = colors.indexOfFirst {
        it.get(context) == color
    }

    fun updateSelection(selected: Int) {
        val newSelection = selected.takeIf { it >= 0 }
        val oldSelection = this.selected
        if (oldSelection != newSelection) {
            this.selected = newSelection
            oldSelection?.let { notifyItemChanged(oldSelection, Payload.SelectionChanged) }
            newSelection?.let { notifyItemChanged(newSelection, Payload.SelectionChanged) }
        }
    }

    fun updateTransparency(transparency: Int) {
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
        val color = colors[position]

        val solidColor = color.get(holder.itemView.context)
        val colorWithAlpha = solidColor.let { ColorUtils.setAlphaComponent(it, transparency) }

        if (payload.isNotEmpty()) {
            if (payload.contains(Payload.TransparencyChanged))
                updateTransparency(vh, position, color, colorWithAlpha, transparency)
            if (payload.contains(Payload.SelectionChanged))
                updateSelection(vh, position)
            return
        }

        updateTransparency(vh, position, color, colorWithAlpha, transparency)
        updateSelection(vh, position)

        if (color is GroupedColor) {
            ColorDefinitions.displayAsCircleViewBackground(color, vh.binding.vColorForeground)
        } else {
            vh.binding.vColorForeground.setCircleBackground(solidColor, false)
        }
        vh.itemView.setOnClickListener { view: View? ->
            listener?.invoke(this@ColorAdapter, vh, color, vh.adapterPosition)
        }

        if (vh.oldPosition != position) {
            vh.binding.vSelectedBackground.setCircleBackground(Color.TRANSPARENT, true)
        }
    }

    private fun updateTransparency(
        vh: ColorViewHolder,
        position: Int,
        color: IColor,
        colorWithTransparancy: Int,
        transparency: Int
    ) {
        val colorNumber = color.label
        val fgColor = ColorUtil.getBestTextColor(colorWithTransparancy)
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
        return colors.size
    }

    class ColorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var binding: MdfRowColorBinding = MdfRowColorBinding.bind(itemView)
    }
}