package com.michaelflisar.dialogs.animations

import android.graphics.Point
import android.graphics.Rect
import android.util.Log
import android.view.View
import android.view.ViewPropertyAnimator
import android.view.animation.AccelerateDecelerateInterpolator
import com.michaelflisar.dialogs.MaterialDialogUtil
import com.michaelflisar.dialogs.interfaces.IMaterialDialogAnimation
import kotlinx.parcelize.Parcelize

@Parcelize
class MaterialDialogFadeScaleAnimation(
    private val duration: Long,
    private val point: Point? = null,
    private val scaleFromX: Float = 0f,
    private val scaleFromY: Float = 0f,
    private val alphaFrom: Float = 0f
) : IMaterialDialogAnimation {

    companion object {

        fun fromCenter(
            view: View,
            duration: Long,
            scaleFromX: Float = 0f,
            scaleFromY: Float = 0f,
            alphaFrom: Float = 0f
        ): MaterialDialogFadeScaleAnimation {
            val bounds = MaterialDialogUtil.getBoundsOnScreen(view)
            val p = bounds.let {
                Point(
                    it.centerX(),
                    it.centerY()
                )
            }
            return MaterialDialogFadeScaleAnimation(duration, p, scaleFromX, scaleFromY, alphaFrom)
        }
    }

    override fun prepare(view: View) {

    }

    override fun show(view: View, onShown: (() -> Unit)?) {
        val anim = view.animate()
        anim.cancel()
        point?.let {
            val pivot = MaterialDialogAnimationsUtil.calcRelativeOffsetFromAbsolutePoint(view, it)
            view.pivotX = pivot.x
            view.pivotY = pivot.y
        }
        view.scaleX = scaleFromX
        view.scaleY = scaleFromY
        view.alpha = alphaFrom
        anim.scaleX(1f)
        anim.scaleY(1f)
        anim.alpha(1f)
        setDurationAndInterpolator(anim)
        anim.withEndAction {
            onShown?.invoke()
        }
        anim.start()
    }

    override fun hide(view: View, onHidden: (() -> Unit)?) {
        val anim = view.animate()
        anim.cancel()
        anim.scaleX(scaleFromX)
        anim.scaleY(scaleFromY)
        anim.alpha(alphaFrom)
        setDurationAndInterpolator(anim)
        anim.withEndAction {
            onHidden?.invoke()
        }
        anim.start()
    }

    private fun setDurationAndInterpolator(anim: ViewPropertyAnimator) {
        anim.duration = duration
        anim.interpolator = AccelerateDecelerateInterpolator()
    }
}