package com.michaelflisar.dialogs.animations

import android.animation.Animator
import android.graphics.Point
import android.os.Build
import android.view.View
import android.view.ViewAnimationUtils
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.core.animation.doOnCancel
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import com.michaelflisar.dialogs.MaterialDialogUtil
import com.michaelflisar.dialogs.interfaces.IMaterialDialogAnimation
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import kotlin.math.max

@Parcelize
class MaterialDialogRevealAnimation(
    private val duration: Long,
    private val point: Point? = null
) : IMaterialDialogAnimation {

    companion object {

        fun fromCenter(
            view: View,
            duration: Long
        ): MaterialDialogRevealAnimation {
            val bounds = MaterialDialogUtil.getBoundsOnScreen(view)
            val p = bounds.let {
                Point(
                    it.centerX(),
                    it.centerY()
                )
            }
            return MaterialDialogRevealAnimation(duration, p)
        }
    }

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

            if (point != null) {
                val pivot = MaterialDialogAnimationsUtil.calcRelativeOffsetFromAbsolutePoint(view, point)
                cx = view.left + pivot.x.toInt()
                cy = view.top + pivot.y.toInt()
            } else {
                cx = (view.left + view.right) / 2
                cy = (view.top + view.bottom) / 2
            }

            w = view.width
            h = view.height
            finalRadius = max(w, h)
            val anim = ViewAnimationUtils.createCircularReveal(view, cx, cy, 0f, finalRadius.toFloat()).apply {
                setDurationAndInterpolator(this)
                doOnStart {
                    view.visibility = View.VISIBLE
                    onShown?.invoke()
                }
            }

            anim.start()
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

    private fun setDurationAndInterpolator(anim: Animator) {
        anim.duration = duration
        anim.interpolator = MaterialDialogAnimationsUtil.getDefaultInterpolator()
    }

}