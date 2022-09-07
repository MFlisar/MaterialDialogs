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

    var wrapContent: Boolean = true
        set(value) {
            field = value
            requestLayout()
        }

    @Suppress("NAME_SHADOWING")
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var heightMeasureSpec = heightMeasureSpec
        if (wrapContent) {
            val child: View? = getChildAt(currentItem)
            if (child != null) {
                child.measure(
                    widthMeasureSpec,
                    MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
                )
                val h: Int = child.measuredHeight
                heightMeasureSpec = MeasureSpec.makeMeasureSpec(h, MeasureSpec.EXACTLY)
            }
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }
}