package com.michaelflisar.dialogs.views

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.viewpager.widget.ViewPager

class AutoSizeViewPager(
    context: Context,
    attrs: AttributeSet? = null
) : ViewPager(context, attrs) {

    init {
        addOnPageChangeListener(object : SimpleOnPageChangeListener() {
            override fun onPageSelected(position: Int) {
                requestLayout()
            }
        })
    }

    var mode: Mode = Mode.WrapContent
        set(value) {
            field = value
            requestLayout()
        }

    @Suppress("NAME_SHADOWING")
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var heightMeasureSpec = heightMeasureSpec
        val mode = this.mode
        when (mode) {
            Mode.Disabled -> {
            }
            Mode.WrapContent -> {
                val child: View? = getChildAt(currentItem)
                heightMeasureSpec =
                    child?.let { measureHeight(widthMeasureSpec, it) } ?: heightMeasureSpec
            }
            is Mode.WrapPage -> {
                val child: View? = getChildAt(mode.page)
                heightMeasureSpec =
                    child?.let { measureHeight(widthMeasureSpec, it) } ?: heightMeasureSpec
            }
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    private fun measureHeight(widthMeasureSpec: Int, view: View): Int {
        view.measure(
            widthMeasureSpec,
            MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
        )
        val h: Int = view.measuredHeight
        return MeasureSpec.makeMeasureSpec(h, MeasureSpec.EXACTLY)
    }


    sealed class Mode {
        object WrapContent : Mode()
        object Disabled : Mode()
        class WrapPage(val page: Int) : Mode()
    }
}