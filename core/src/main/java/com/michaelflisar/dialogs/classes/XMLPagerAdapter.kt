package com.michaelflisar.dialogs.classes

import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter

class XMLPagerAdapter(
    private val views: List<View>
) : PagerAdapter() {

    override fun instantiateItem(
        container: ViewGroup,
        position: Int
    ): Any {
        return views.get(position)
    }

    override fun getCount(): Int = views.size

    override fun isViewFromObject(
        view: View,
        `object`: Any
    ): Boolean = view === `object` as View

    override fun destroyItem(
        container: ViewGroup,
        position: Int,
        `object`: Any
    ) = Unit
}