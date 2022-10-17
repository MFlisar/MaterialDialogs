package com.michaelflisar.dialogs.classes

import android.os.Build
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.updateMargins
import androidx.core.view.updatePadding
import com.google.android.material.appbar.MaterialToolbar
import com.michaelflisar.dialogs.core.R
import com.michaelflisar.text.Text

object MaterialDialogTitleViewManager {

    fun applyStyle(
        text: Text,
        icon: Icon,
        root: View,
        toolbar: MaterialToolbar,
        title: TextView,
        smallIcon: ImageView,
        largeIcon: ImageView,
        style: MaterialDialogTitleStyle,
        smallTextRootTopPadding: Int?,
        smallTextIconBottomMargin: Int?
    ) {

        // 1) Title TextAppearance
        if (style.hasSmallText()) {
            val appearance = R.style.MaterialDialogs_Title_Small
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                title.setTextAppearance(appearance)
            } else {
                @Suppress("DEPRECATION")
                title.setTextAppearance(title.context, appearance)
            }
        }

        // 2) Icon Paddings/Margins for small text sizes
        if (style.hasSmallText()) {
            if (icon == Icon.None) {
                smallTextRootTopPadding?.let {
                    root.updatePadding(top = it)
                }

            } else {
                smallTextIconBottomMargin?.let {
                    (largeIcon.layoutParams as ViewGroup.MarginLayoutParams).updateMargins(
                        bottom = it
                    )
                }
                val toolbarIconSize =
                    root.context.resources.getDimensionPixelSize(R.dimen.mdf_hero_icon_toolbar_small_size)
                (smallIcon.layoutParams as ViewGroup.LayoutParams).apply {
                    width = toolbarIconSize
                    height = toolbarIconSize
                }
            }
        }

        val titleText = text.display(title)
        val hasTitle = titleText.isNotEmpty()

        val showTitle: Boolean
        val showLargeIcon: Boolean
        val showSmallIcon: Boolean
        if (style.hasInlinedIcon()) {
            val hasIcon = icon.display(smallIcon)
            showTitle = hasTitle || hasIcon
            showSmallIcon = hasIcon
            showLargeIcon = false
        } else {
            val hasIcon = icon.display(largeIcon)
            showTitle = hasTitle
            showLargeIcon = hasIcon
            showSmallIcon = false
        }

        title.gravity = style.getTextGravity()
        title.visibility = if (showTitle) View.VISIBLE else View.GONE
        toolbar.visibility = title.visibility
        largeIcon.visibility = if (showLargeIcon) View.VISIBLE else View.GONE
        smallIcon.visibility = if (showSmallIcon) View.VISIBLE else View.GONE
    }
}