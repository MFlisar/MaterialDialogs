package com.michaelflisar.dialogs

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.content.res.TypedArray
import android.graphics.Color
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.view.inputmethod.InputMethodManager
import androidx.core.widget.NestedScrollView

object MaterialDialogUtil {

    private const val KEY_VIEW_STATE = "MaterialDialogFragment|VIEWSTATE"

    fun <State : Parcelable> getViewState(savedInstanceState: Bundle?): State? {
        return savedInstanceState?.getParcelable(KEY_VIEW_STATE)
    }

    fun <State : Parcelable> saveViewState(outState: Bundle, state: State) {
        outState.putParcelable(KEY_VIEW_STATE, state)
    }

    fun getThemeColorAttr(context: Context, attr: Int): Int {
        val typedValue = TypedValue()
        val a: TypedArray = context.obtainStyledAttributes(typedValue.data, intArrayOf(attr))
        val color = a.getColor(0, 0)
        a.recycle()
        return color
    }

    fun getThemeReference(context: Context, attribute: Int): Int {
        val typeValue = TypedValue()
        context.theme.resolveAttribute(attribute, typeValue, false)
        return if (typeValue.type == TypedValue.TYPE_REFERENCE) {
            typeValue.data
        } else {
            -1
        }
    }

    fun pxToDp(px: Int): Int = (px / Resources.getSystem().displayMetrics.density).toInt()
    fun dpToPx(dp: Int): Int = (dp * Resources.getSystem().displayMetrics.density).toInt()

    fun isCurrentThemeDark(context: Context): Boolean {
        val color = resolve(context, android.R.attr.colorBackground)
        val darkness =
            1 - (0.299 * Color.red(color) + 0.587 * Color.green(color) + 0.114 * Color.blue(color)) / 255
        return darkness > 0.5
    }

    fun resolve(context: Context, attrId: Int): Int {
        val typedValue = TypedValue()
        context.theme.resolveAttribute(attrId, typedValue, true)
        return typedValue.data
    }

    fun isDark(context: Context): Boolean {
        return context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
    }

    fun createContentView(setup: MaterialDialogSetup<*, *, *>, content: View): View {
        if (setup.viewManager.wrapInScrollContainer) {
            val scrollView = NestedScrollView(content.context)
            scrollView.addView(
                content,
                ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
            )
            return scrollView
        } else return content
    }

    fun ensureKeyboardCloses(view: View) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            view.windowInsetsController?.hide(WindowInsets.Type.ime())
        } else {
            val imm =
                view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    fun getBoundsOnScreen(view: View): Rect {
        val pos = IntArray(2)
        view.getLocationOnScreen(pos)
        return Rect(pos[0], pos[1], pos[0] +  view.width, pos[1] +  view.height)
    }
}