package com.michaelflisar.dialogs

import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.ColorUtils
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.michaelflisar.dialogs.adapter.ColorAdapter
import com.michaelflisar.dialogs.classes.BaseMaterialViewManager
import com.michaelflisar.dialogs.classes.Color
import com.michaelflisar.dialogs.classes.ColorDefinitions
import com.michaelflisar.dialogs.classes.GroupedColor
import com.michaelflisar.dialogs.color.databinding.MdfContentColorBinding
import com.michaelflisar.dialogs.interfaces.IMaterialDialogPresenter
import com.michaelflisar.dialogs.utils.ColorUtil
import com.michaelflisar.dialogs.views.BaseCustomColorView
import com.michaelflisar.dialogs.views.BaseSlider
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

internal class ColorViewManager(
    override val setup: DialogColor
) : BaseMaterialViewManager<DialogColor, MdfContentColorBinding>() {

    override val wrapInScrollContainer = true

    private lateinit var colorAdapter: ColorAdapter
    private var state = State()

    override fun onCreateContentViewBinding(
        layoutInflater: LayoutInflater,
        parent: ViewGroup?,
        attachToParent: Boolean
    ) = MdfContentColorBinding.inflate(layoutInflater, parent, attachToParent)

    override fun initBinding(
        presenter: IMaterialDialogPresenter<*>,
        savedInstanceState: Bundle?
    ) {
        if (savedInstanceState != null) {
            state = savedInstanceState.getParcelable("state")!!
        } else {
            state = State(
                0,
                0,
                ColorUtil.getNearestColorGroup(context, setup.color),
                setup.color,
                android.graphics.Color.alpha(setup.color)
            )
        }

        // 1) init ViewPager
        initViewPager()

        // 2) init adapter for picker page
        initPickerPage()

        // 3) init custom color page
        initCustomPage()

        // 4) update dependencies
        onSelectionChanged()
    }

    override fun onBackPress(): Boolean {
        if (state.selectedPage == 0 && state.selectedPagePresetsLevel == 1) {
            state.selectedPagePresetsLevel = 0
            colorAdapter.update(ColorDefinitions.COLORS, true)
            return true
        }
        return false
    }

    override fun saveViewState(outState: Bundle) {
        super.saveViewState(outState)
        outState.putParcelable("state", state)
    }

    private fun onSelectionChanged() {
        val solidColor = state.selectedSolidColor
        val alpha = state.selectedTransparency
        val alphaInPercent = alpha.toFloat() / 255f
        val color =
            ColorUtils.setAlphaComponent(state.selectedSolidColor, state.selectedTransparency)

        // 1) adjust selection in adapter on picker page
        if (state.selectedPagePresetsLevel == 1) {
            val index = colorAdapter.indexOfSolidColor(context, solidColor)
            colorAdapter.updateSelection(index)
        }

        // 2) adjust slider on picker page + eventually alpha in adapter
        if (setup.alphaAllowed) {
            (binding.sliderAlpha as BaseSlider).value = alpha
            colorAdapter.updateTransparency(alpha)
        }

        // 3) adjust custom color selector
        val colorPicker = binding.colorPicker as BaseCustomColorView
        if (colorPicker.value != color) {
            colorPicker.value = color
        }
    }

    private fun initViewPager() {
        val adapter = ColorPageAdapter(
            context,
            listOf(binding.page1, binding.page2),
            //listOf(R.string.color_dialog_presets, R.string.color_dialog_custom)

        )
        binding.pager.adapter = adapter
        binding.pager.offscreenPageLimit = adapter.count
        binding.dots.attachViewPager(binding.pager)
        binding.dots.setOnClickListener {
            val page = (binding.pager.currentItem + 1) % adapter.count
            binding.pager.currentItem = page
        }
        binding.pager.setCurrentItem(state.selectedPage, false)
        binding.pager.addOnPageChangeListener(object : ViewPager.SimpleOnPageChangeListener() {
            override fun onPageSelected(position: Int) {
                state.selectedPage = position
            }
        })

        // ViewPager should measure itself based on the second page with the custom color selector
        // because measuring a scrolling view does not work well
        //binding.pager.mode = AutoSizeViewPager.Mode.WrapPage(1)
    }

    private fun initPickerPage() {

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

                    onSelectionChanged()
                }
                is Color -> {
                    state.selectedSolidColor = color.get(context)
                    colorAdapter.updateSelection(pos)

                    onSelectionChanged()

                    if (setup.moveToCustomPageOnPickerSelection) {
                        binding.pager.setCurrentItem(1, true)
                    }
                }
            }
        }

        val columns = if (context.isLandscape()) 6 else 4
        binding.rvColors.layoutManager =
            GridLayoutManager(context, columns, RecyclerView.VERTICAL, false)
        binding.rvColors.adapter = colorAdapter

        // 2) Slider
        if (!setup.alphaAllowed) {
            binding.tvTitleTransparancy.visibility = View.GONE
            binding.sliderAlpha.visibility = View.GONE
        } else {
            binding.tvTitleTransparancy.visibility = View.VISIBLE
            binding.sliderAlpha.visibility = View.VISIBLE
            val slider = binding.sliderAlpha as BaseSlider
            slider.value = state.selectedTransparency
            slider.onSelectionChanged = { value ->
                state.selectedTransparency = value
                onSelectionChanged()
            }
        }
    }

    private fun initCustomPage() {
        val colorPicker = binding.colorPicker as BaseCustomColorView
        colorPicker.value = setup.color
        colorPicker.supportsAlpha(setup.alphaAllowed)
        colorPicker.onValueChanged = { color ->
            val alpha = android.graphics.Color.alpha(color)
            val solid = ColorUtils.setAlphaComponent(color, 255)
            state.selectedSolidColor = solid
            state.selectedTransparency = alpha
            onSelectionChanged()
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
        //private val titles: List<Int>
    ) : PagerAdapter() {

        override fun instantiateItem(collection: View, position: Int): Any {
            return views[position]
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {}

        override fun getCount(): Int {
            return views.size
        }

        //override fun getPageTitle(position: Int): CharSequence? {
        //    return titles[position].takeIf { it != -1 }?.let { context.getString(it) }
        //}

        override fun isViewFromObject(arg0: View, arg1: Any): Boolean {
            return arg0 === arg1 as View
        }
    }

    // -----------
    // State
    // -----------

    @Parcelize
    private data class State(
        var selectedPage: Int = 0,
        var selectedPagePresetsLevel: Int = 0,
        var selectedGroupMainColor: Int = ColorDefinitions.COLORS_BW.color,
        //var selectedGroup: GroupedColor = ColorDefinitions.COLORS_BW,
        var selectedSolidColor: Int = android.graphics.Color.BLACK,
        var selectedTransparency: Int = 255
    ) : Parcelable {

        constructor(
            selectedPage: Int,
            selectedPagePresetsLevel: Int,
            selectedGroup: GroupedColor,
            selectedSolidColor: Int,
            selectedTransparency: Int
        ) : this(
            selectedPage,
            selectedPagePresetsLevel,
            selectedGroup.color,
            selectedSolidColor,
            selectedTransparency
        )

        @IgnoredOnParcel
        var selectedGroup: GroupedColor =
            ColorDefinitions.COLORS.find { it.color == selectedGroupMainColor }!!
            set(value) {
                selectedGroupMainColor = value.color
                field = value
            }
    }
}