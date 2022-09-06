package com.michaelflisar.dialogs.animations

import android.graphics.Point
import android.os.Build
import android.view.View
import android.view.ViewAnimationUtils
import androidx.core.animation.doOnCancel
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import com.michaelflisar.dialogs.interfaces.IMaterialDialogAnimation
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import kotlin.math.max
import kotlin.math.sqrt

@Parcelize
class MaterialDialogRevealAnimation(
    val duration: Long
) : IMaterialDialogAnimation {

    @IgnoredOnParcel
    private var cx: Int = 0
    @IgnoredOnParcel
    private var cy: Int = 0
    @IgnoredOnParcel
    private var w: Int = 0
    @IgnoredOnParcel
    private var h: Int = 0
    @IgnoredOnParcel
    private var finalRadius: Int = 0

    override fun prepare(view: View) {
        // -
    }

    override fun show(view: View, onShown: (() -> Unit)?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            cx = (view.left + view.right) / 2
            cy = (view.top + view.bottom) / 2
            w = view.width
            h = view.height
            finalRadius = max(w, h)
            ViewAnimationUtils.createCircularReveal(view, cx, cy, 0f, finalRadius.toFloat()).apply {
                duration = this@MaterialDialogRevealAnimation.duration
                doOnStart {
                    view.visibility = View.VISIBLE
                    onShown?.invoke()
                }
            }.start()
        } else {
            view.visibility = View.VISIBLE
        }
    }

    override fun hide(view: View, onHidden: (() -> Unit)?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ViewAnimationUtils.createCircularReveal(view, cx, cy, finalRadius.toFloat(), 0f).apply {
                duration = this@MaterialDialogRevealAnimation.duration
                doOnEnd {
                    view.visibility = View.INVISIBLE
                    onHidden?.invoke()
                }
                doOnCancel {
                    view.visibility = View.INVISIBLE
                    onHidden?.invoke()
                }
            }.start()
        } else {
            view.visibility = View.VISIBLE
        }
    }

}