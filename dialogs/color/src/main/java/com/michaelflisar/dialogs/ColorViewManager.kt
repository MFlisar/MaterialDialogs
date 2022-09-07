package com.michaelflisar.dialogs

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.*
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.michaelflisar.dialogs.adapter.ColorAdapter
import com.michaelflisar.dialogs.adapter.MainColorAdapter
import com.michaelflisar.dialogs.classes.ColorDefinitions
import com.michaelflisar.dialogs.classes.GroupedColor
import com.michaelflisar.dialogs.color.R
import com.michaelflisar.dialogs.color.databinding.MdfContentColorBinding
import com.michaelflisar.dialogs.interfaces.IMaterialViewManager
import com.michaelflisar.dialogs.utils.ColorUtil
import com.michaelflisar.dialogs.utils.RecyclerViewUtil
import kotlin.math.roundToInt

internal class ColorViewManager(
    private val setup: DialogColor
) : IMaterialViewManager<DialogColor, MdfContentColorBinding> {

    override val wrapInScrollContainer = false

    internal lateinit var mainColorAdapter: MainColorAdapter
    internal lateinit var colorAdapter: ColorAdapter

    private var selectedPage: Int = 0
    private var selectedColorPickerGroup: GroupedColor = ColorDefinitions.COLORS[0]
    private var selectedColorPickerColor: Int? = null
    private var selectedColorPickerTransparency: Int = 255
    private var selectedCustomColor: Int? = null

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
            selectedPage = savedInstanceState.getInt("selectedPage")
            selectedColorPickerGroup = ColorDefinitions.COLORS[savedInstanceState.getInt("selectedColorPickerGroup")]
            selectedColorPickerColor = "selectedColorPickerColor".let { if (savedInstanceState.containsKey(it)) savedInstanceState.getInt(it) else null }
            selectedColorPickerTransparency = savedInstanceState.getInt("selectedColorPickerTransparency")
            selectedCustomColor = "selectedCustomColor".let { if (savedInstanceState.containsKey(it)) savedInstanceState.getInt(it) else null }
        } else {
            selectedColorPickerGroup = ColorUtil.getNearestColorGroup(binding.root.context, setup.color)
            selectedColorPickerColor = selectedColorPickerGroup.findMatchingColor(binding.root.context, setup.color)
            selectedColorPickerTransparency = Color.alpha(setup.color)
            selectedCustomColor = setup.color
        }

        //binding.toolbar.title = setup.title.get(binding.root.context)
        //binding.toolbar.menu?.apply {
        //    add(R.string.color_dialog_toggle_view)
        //    getItem(0).apply {
        //        setIcon(R.drawable.ic_baseline_sync_alt_24)
        //        setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
        //        icon?.setColorFilter(if (DialogSetup.isUsingDarkTheme(view.context)) Color.WHITE else Color.BLACK, PorterDuff.Mode.SRC_ATOP)
        //    }
        //}
        //binding.toolbar.setOnMenuItemClickListener {
        //    binding.pager.currentItem = (binding.pager.currentItem + 1) % 2
        //    true
        //}

        // 1) init ViewPager
        initViewPager(binding)

        // 2) init adapter for picker page
        initPickerPage(binding)

        // 3) init custom color page
        initCustomPage(binding)

        // 4) update dependencies
        updateSelectedColorDependencies(binding)
    }

    override fun saveViewState(binding: MdfContentColorBinding, outState: Bundle) {
        super.saveViewState(binding, outState)
        outState.putInt("selectedColorPickerGroup", ColorDefinitions.COLORS.indexOf(selectedColorPickerGroup))
        outState.putInt("selectedPage", selectedPage)
        selectedColorPickerColor?.let { outState.putInt("selectedColorPickerColor", it) }
        outState.putInt("selectedColorPickerTransparency", selectedColorPickerTransparency)
        selectedCustomColor?.let { outState.putInt("selectedCustomColor", it) }
    }

    private fun updateSelectedColorDependencies(binding: MdfContentColorBinding, pickerAlphaChanged: Boolean = false) {
        val selectedColor = getSelectedColor()
            ?: selectedColorPickerGroup.getMainColor(binding.root.context)
        val textColor = ColorUtil.getBestTextColor(selectedColor)

//        binding.tabs.setBackgroundColor(selectedColor)
//        binding.tabs.tabTextColors = ColorStateList.valueOf(textColor)
//        binding.tabs.setSelectedTabIndicatorColor(textColor)

        binding.tvTransparancy.text = "${(100 * selectedColorPickerTransparency / 255f).roundToInt()}%"
        if (pickerAlphaChanged) {
            colorAdapter.setTransparency(selectedColorPickerTransparency)
        }
    }

    private fun initViewPager(binding: MdfContentColorBinding) {
        val adapter = ColorPageAdapter(
            binding.root.context,
            listOf(binding.page1, binding.page2),
            listOf(R.string.color_dialog_presets, R.string.color_dialog_custom)

        )
        binding.pager.adapter = adapter
        binding.pager.offscreenPageLimit = 5
//        binding.tabs.setupWithViewPager(binding.pager)
        binding.dots.attachViewPager(binding.pager)
        binding.pager.setCurrentItem(selectedPage, false)
        binding.pager.addOnPageChangeListener(object : ViewPager.SimpleOnPageChangeListener() {
            override fun onPageSelected(position: Int) {
                selectedPage = position
                updateSelectedColorDependencies(binding, false)
                if (selectedPage == 0) {
                    ensureMainColorIsVisible(binding)
                }
            }
        })

        binding.pager.wrapContent = !binding.root.context.isLandscape()
    }

    private fun initPickerPage(binding: MdfContentColorBinding) {

        val isLandscape = binding.root.context.isLandscape()

        // 1) RecyclerViews
        colorAdapter = ColorAdapter(isLandscape, selectedColorPickerGroup, selectedColorPickerTransparency, selectedColorPickerGroup.findMatchingColorIndex(binding.root.context, selectedColorPickerColor)) { adapter, item, color, pos ->
            selectedColorPickerColor = color
            colorAdapter.updateSelection(pos)
            updateSelectedColorDependencies(binding)
            if (setup.moveToCustomPageOnPickerSelection || setup.updateCustomColorOnPickerSelection) {
                selectedCustomColor = selectedColorPickerColor
                binding.colorPicker.setCurrentColor(selectedCustomColor!!)
                if (setup.moveToCustomPageOnPickerSelection) {
                    binding.pager.setCurrentItem(1, true)
                }
            }
        }
        mainColorAdapter = MainColorAdapter(ColorDefinitions.COLORS, getSelectedGroupIndex()) { adapter, item, color, pos ->
            // nicht hier speichern, das macht den Dialog etwas langsam weil das schreiben die UI blockiert
            // => nur wert updaten und später speichern
            if (color != selectedColorPickerGroup) {
                selectedColorPickerGroup = color
                selectedColorPickerColor = null
                mainColorAdapter.update(pos)
                colorAdapter.updateGroupColor(selectedColorPickerGroup, true)
                binding.rvMaterialColors.scrollToPosition(0)
                binding.tvGroupColorHeader.text = color.getHeaderDescription(binding.root.context)
                updateSelectedColorDependencies(binding)
            }
        }

        binding.tvGroupColorHeader.text = selectedColorPickerGroup.getHeaderDescription(binding.root.context)

        val columns = if (isLandscape) 7 else 4
        binding.rvMaterialColors.layoutManager = GridLayoutManager(binding.root.context, columns, RecyclerView.VERTICAL, false)
        binding.rvMaterialColors.adapter = colorAdapter
        binding.rvMaterialMainColors.layoutManager = LinearLayoutManager(binding.root.context, RecyclerView.VERTICAL, false)
        binding.rvMaterialMainColors.adapter = mainColorAdapter

        ensureMainColorIsVisible(binding)

        // 2) Slider
        if (!setup.alphaAllowed) {
            binding.tvTitleTransparancy.visibility = View.GONE
            binding.llTransparancy.visibility = View.GONE
        } else {
            binding.tvTitleTransparancy.visibility = View.VISIBLE
            binding.llTransparancy.visibility = View.VISIBLE
            binding.slTransparancy.value = selectedColorPickerTransparency / 255f
            binding.slTransparancy.setLabelFormatter {
                "${(100 * it).roundToInt()}%"
            }
            binding.slTransparancy.addOnChangeListener { slider, value, fromUser ->
                if (fromUser) {
                    selectedColorPickerTransparency = (255f * value).roundToInt()
                    selectedColorPickerColor?.let {
                        selectedColorPickerColor = ColorUtil.adjustAlpha(it, selectedColorPickerTransparency)
                    }
                    updateSelectedColorDependencies(binding, true)
                }
            }
        }

        // 3) Group Header
        binding.tvGroupColorHeader.visibility = if (binding.root.context.isLandscape()) View.GONE else View.VISIBLE
    }

    private fun initCustomPage(binding: MdfContentColorBinding) {
        binding.colorPicker.color = setup.color
        binding.colorPicker.showAlpha(setup.alphaAllowed)
        binding.colorPicker.addColorObserver { observableColor ->
            selectedCustomColor = observableColor.color
            updateSelectedColorDependencies(binding)
        }
    }

    private fun getSelectedGroupIndex() = ColorDefinitions.COLORS.indexOf(selectedColorPickerGroup)

    internal fun getSelectedColor(): Int? {
        return if (selectedPage == 0) {
            selectedColorPickerColor
        } else {
            selectedCustomColor
        }
    }

    private fun ensureMainColorIsVisible(binding: MdfContentColorBinding) {
        val scrollToMainColor = {
            if (!RecyclerViewUtil.isViewVisible(binding.rvMaterialMainColors, getSelectedGroupIndex()))
                binding.rvMaterialMainColors.scrollToPosition(getSelectedGroupIndex())
        }

        binding.rvMaterialColors.post {
            scrollToMainColor()
        }

//        if (binding.rvMaterialColors.isLaidOut) {
//            scrollToMainColor()
//        } else {
//            binding.rvMaterialColors.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
//                override fun onGlobalLayout() {
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
//                        binding.rvMaterialColors.viewTreeObserver.removeOnGlobalLayoutListener(this)
//                    else {
//                        @Suppress("DEPRECATION")
//                        binding.rvMaterialColors.viewTreeObserver.removeGlobalOnLayoutListener(this)
//                    }
//                    scrollToMainColor()
//                }
//            })
//        }
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
}