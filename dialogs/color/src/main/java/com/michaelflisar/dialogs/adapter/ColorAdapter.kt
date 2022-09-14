package com.michaelflisar.dialogs.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.ColorUtils
import androidx.recyclerview.widget.RecyclerView
import com.michaelflisar.dialogs.classes.IColor
import com.michaelflisar.dialogs.classes.Payload
import com.michaelflisar.dialogs.color.R
import com.michaelflisar.dialogs.color.databinding.MdfRowColorBinding
import com.michaelflisar.dialogs.drawables.DrawableCheckerBoard
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
                vh.updateTransparency(color, colorWithAlpha, transparency)
            if (payload.contains(Payload.SelectionChanged))
                vh.updateSelection(position == selected)
            return
        }

        vh.updateTransparency(color, colorWithAlpha, transparency)
        vh.updateSelection(position == selected)
        vh.updateColor(color, solidColor)

        vh.itemView.setOnClickListener { view: View? ->
            listener?.invoke(this@ColorAdapter, vh, color, vh.adapterPosition)
        }
    }


    override fun getItemCount(): Int {
        return colors.size
    }

    internal class ColorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var binding: MdfRowColorBinding = MdfRowColorBinding.bind(itemView)

        init {
            binding.vCheckerBackground.background = DrawableCheckerBoard()
        }

        fun updateColor(color: IColor, solidColor: Int) {
            color.getCustomDrawable()?.let {
                binding.vSelectedColor.background = it
            } ?: binding.vSelectedColor.setBackgroundColor(solidColor)
        }

        fun updateTransparency(
            color: IColor,
            colorWithTransparancy: Int,
            transparency: Int
        ) {
            val fgColor = ColorUtil.getBestTextColor(colorWithTransparancy)
            binding.tvSelectedColorLabel.text = color.label
            binding.tvSelectedColorLabel.setTextColor(fgColor)
            binding.ivCheckmark.tint(fgColor)
            binding.vSelectedColor.alpha = transparency.toFloat() / 255f
        }

        fun updateSelection(selected: Boolean) {
            //vh.binding.vSelectedBackground.visibility =
            //    if (position == selected) View.VISIBLE else View.INVISIBLE
            binding.ivCheckmark.visibility =
                if (selected) View.VISIBLE else View.INVISIBLE
            binding.tvSelectedColorLabel.visibility =
                if (selected) View.INVISIBLE else View.VISIBLE
        }
    }
}