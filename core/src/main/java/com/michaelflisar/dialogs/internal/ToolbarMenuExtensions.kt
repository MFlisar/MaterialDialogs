package com.michaelflisar.dialogs.internal

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.graphics.ColorFilter
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.view.menu.ActionMenuItemView
import androidx.appcompat.view.menu.MenuBuilder
import androidx.core.view.forEach
import com.google.android.material.appbar.MaterialToolbar
import com.michaelflisar.dialogs.MaterialDialogUtil
import com.michaelflisar.dialogs.core.R

internal fun MaterialToolbar.tintAndShowIcons() {
    val colorOnToolbar = MaterialDialogUtil.getThemeColorAttr(this.context, R.attr.colorOnSurface)
    val colorInOverflow = colorOnToolbar
    tintAndShowIcons(colorOnToolbar, colorInOverflow)
}

@SuppressLint("RestrictedApi")
internal fun MaterialToolbar.tintAndShowIcons(
    colorOnToolbar: Int,
    colorInOverflow: Int
) {
    (menu as? MenuBuilder)?.setOptionalIconsVisible(true)
    val c1 = ColorStateList.valueOf(colorOnToolbar)
    val c2 = PorterDuffColorFilter(colorInOverflow, PorterDuff.Mode.SRC_IN)
    val idsShowing = ArrayList<Int>()
    getAllChildrenRecursively().forEach {
        // Icon in Toolbar
        (it as? ActionMenuItemView)?.let {
            idsShowing.add(it.id)
        }
        // Overflow Icon
        (it as? ImageView)?.imageTintList = c1
    }
    menu.forEach {
        checkOverflowMenuItem(it, c2, idsShowing)
    }
}

private fun checkOverflowMenuItem(
    menuItem: MenuItem,
    iconColor: ColorFilter,
    idsShowing: ArrayList<Int>
) {
    // Only change Icons inside the overflow
    if (!idsShowing.contains(menuItem.itemId)) {
        menuItem.icon?.let {
            val icon = it.mutate()
            icon.colorFilter = iconColor
            menuItem.icon = icon
        }
    }
    menuItem.subMenu?.forEach {
        checkOverflowMenuItem(it, iconColor, idsShowing)
    }
}

private fun View.getAllChildrenRecursively(): List<View> {
    val result = ArrayList<View>()
    if (this !is ViewGroup) {
        result.add(this)
    } else {
        for (index in 0 until this.childCount) {
            val child = this.getChildAt(index)
            result.addAll(child.getAllChildrenRecursively())
        }
    }
    return result
}