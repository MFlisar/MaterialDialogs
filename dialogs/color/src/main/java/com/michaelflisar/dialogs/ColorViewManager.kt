package com.michaelflisar.dialogs

import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.ColorUtils
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.michaelflisar.dialogs.adapter.ColorAdapter
import com.michaelflisar.dialogs.classes.Color
import com.michaelflisar.dialogs.classes.ColorDefinitions
import com.michaelflisar.dialogs.classes.GroupedColor
import com.michaelflisar.dialogs.color.R
import com.michaelflisar.dialogs.color.databinding.MdfContentColorBinding
import com.michaelflisar.dialogs.interfaces.IMaterialViewManager
import com.michaelflisar.dialogs.utils.ColorUtil
import kotlinx.parcelize.Parcelize
import kotlin.math.roundToInt

internal class ColorViewManager(
    private val setup: DialogColor
) : IMaterialViewManager<DialogColor, MdfContentColorBinding> {

    override val wrapInScrollContainer = false

    private lateinit var colorAdapter: ColorAdapter
    private var state = State()

    override fun createContentViewBinding(
        layoutInflater: LayoutInflater,
        parent: ViewGroup?,
        attachToParent: Boolean
    ) = MdfContentColorBinding.inflate(layoutInflater, parent, attachToParent)

    override fun initBinding(
        lifecycleOwner: LifecycleOwner,
        binding: MdfContentColorBinding,
        savedInstanceState: Bundle?
    ) {
        if (savedInstanceState != null) {
            state = savedInstanceState.getParcelable("state")!!
        } else {
            state = State(
                0,
                0,
                ColorUtil.getNearestColorGroup(binding.root.context, setup.color),
                setup.color,
                android.graphics.Color.alpha(setup.color)
            )
        }

        // 1) init ViewPager
        initViewPager(binding)

        // 2) init adapter for picker page
        initPickerPage(binding)

        // 3) init custom color page
        initCustomPage(binding)

        // 4) update dependencies
        onSelectionChanged(binding)
    }

    override fun onBackPress(binding: MdfContentColorBinding): Boolean {
        if (state.selectedPage == 0 && state.selectedPagePresetsLevel == 1) {
            state.selectedPagePresetsLevel = 0
            colorAdapter.update(ColorDefinitions.COLORS, true)
            return true
        }
        return false
    }

    override fun saveViewState(binding: MdfContentColorBinding, outState: Bundle) {
        super.saveViewState(binding, outState)
        outState.putParcelable("state", state)
    }

    private fun onSelectionChanged(
        binding: MdfContentColorBinding
    ) {
        val solidColor = state.selectedSolidColor
        val alpha = state.selectedTransparency
        val alphaInPercent = alpha.toFloat() / 255f
        val color =
            ColorUtils.setAlphaComponent(state.selectedSolidColor, state.selectedTransparency)

        // 1) adjust selection in adapter on picker page
        if (state.selectedPagePresetsLevel == 1) {
            val index = colorAdapter.indexOfSolidColor(binding.root.context, solidColor)
            colorAdapter.updateSelection(index)
        }

        // 2) adjust slider on picker page + eventually alpha in adapter
        if (setup.alphaAllowed) {
            if (alphaInPercent != binding.slTransparancy.value) {
                binding.slTransparancy.value = alphaInPercent
            }
            binding.tvTransparancy.text =
                "${(100 * alphaInPercent).roundToInt()}%"

            colorAdapter.updateTransparency(alpha)
        }

        // 3) adjust custom color selector
        if (binding.colorPicker.color != color) {
            binding.colorPicker.color = color
        }
    }

    private fun initViewPager(binding: MdfContentColorBinding) {
        val adapter = ColorPageAdapter(
            binding.root.context,
            listOf(binding.page1, binding.page2),
            listOf(R.string.color_dialog_presets, R.string.color_dialog_custom)

        )
        binding.pager.adapter = adapter
        binding.pager.offscreenPageLimit = adapter.count
        binding.dots.attachViewPager(binding.pager)
        binding.pager.setCurrentItem(state.selectedPage, false)
        binding.pager.addOnPageChangeListener(object : ViewPager.SimpleOnPageChangeListener() {
            override fun onPageSelected(position: Int) {
                state.selectedPage = position
            }
        })

        binding.pager.wrapContent = !binding.root.context.isLandscape()
    }

    private fun initPickerPage(binding: MdfContentColorBinding) {

        val isLandscape = binding.root.context.isLandscape()

        val colors =
            if (state.selectedPagePresetsLevel == 0) ColorDefinitions.COLORS else state.selectedGroup.colors

        // 1) RecyclerViews
        colorAdapter = ColorAdapter(
            colors,
            state.selectedTransparency,
            state.selectedSolidColor
        ) { adapter, item, color, pos ->

            when (color) {
                is GroupedColor -> {
                    state.selectedPagePresetsLevel = 1
                    state.selectedGroup = color
                    state.selectedSolidColor = color.color

                    colorAdapter.update(color.colors)

                    onSelectionChanged(binding)
                }
                is Color -> {
                    state.selectedSolidColor = color.get(binding.root.context)
                    colorAdapter.updateSelection(pos)

                    onSelectionChanged(binding)

                    if (setup.moveToCustomPageOnPickerSelection) {
                        binding.pager.setCurrentItem(1, true)
                    }
                }
            }
        }

        val columns = setup.presetsGridSize.get(isLandscape)
        binding.rvColors.layoutManager =
            GridLayoutManager(binding.root.context, columns, RecyclerView.VERTICAL, false)
        binding.rvColors.adapter = colorAdapter

        // 2) Slider
        if (!setup.alphaAllowed) {
            binding.tvTitleTransparancy.visibility = View.GONE
            binding.llTransparancy.visibility = View.GONE
        } else {
            binding.tvTitleTransparancy.visibility = View.VISIBLE
            binding.llTransparancy.visibility = View.VISIBLE
            binding.slTransparancy.value = state.selectedTransparency / 255f
            binding.slTransparancy.setLabelFormatter {
                "${(100 * it).roundToInt()}%"
            }
            binding.slTransparancy.addOnChangeListener { slider, value, fromUser ->
                if (fromUser) {
                    state.selectedTransparency = (255f * value).roundToInt()
                    onSelectionChanged(binding)
                }
            }
        }
    }

    private fun initCustomPage(binding: MdfContentColorBinding) {
        binding.colorPicker.color = setup.color
        binding.colorPicker.showAlpha(setup.alphaAllowed)
        binding.colorPicker.addColorObserver { observableColor ->
            val alpha = android.graphics.Color.alpha(observableColor.color)
            val solid = ColorUtils.setAlphaComponent(observableColor.color, 255)
            state.selectedSolidColor = solid
            state.selectedTransparency = alpha
            onSelectionChanged(binding)
        }
    }

    internal fun getSelectedColor(): Int {
        return ColorUtils.setAlphaComponent(state.selectedSolidColor, state.selectedTransparency)
    }

    // -----------------
    // Pager Adapter
    // -----------------

    internal inner class ColorPageAdapter(
        val context: Context,
        private val views: List<View>,
        private val titles: List<Int>
    ) : PagerAdapter() {

        override fun instantiateItem(collection: View, position: Int): Any {
            return views[position]
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {}

        override fun getCount(): Int {
            return views.size
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return titles[position].takeIf { it != -1 }?.let { context.getString(it) }
        }

        override fun isViewFromObject(arg0: View, arg1: Any): Boolean {
            return arg0 === arg1 as View
        }
    }

    @Parcelize
    data class State(
        var selectedPage: Int = 0,
        var selectedPagePresetsLevel: Int = 0,
        var selectedGroup: GroupedColor = ColorDefinitions.COLORS_BW,
        var selectedSolidColor: Int = android.graphics.Color.BLACK,
        var selectedTransparency: Int = 255
    ) : Parcelable
}