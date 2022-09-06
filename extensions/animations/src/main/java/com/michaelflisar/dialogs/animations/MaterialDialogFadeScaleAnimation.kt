package com.michaelflisar.dialogs.animations

import android.graphics.Point
import android.util.Log
import android.view.View
import android.view.ViewPropertyAnimator
import android.view.animation.AccelerateDecelerateInterpolator
import com.michaelflisar.dialogs.MaterialDialogUtil
import com.michaelflisar.dialogs.interfaces.IMaterialDialogAnimation
import kotlinx.parcelize.Parcelize

@Parcelize
class MaterialDialogFadeScaleAnimation(
    val duration: Long,
    val point: Point? = null,
    val scaleFromX: Float = 0f,
    val scaleFromY: Float = 0f,
    val alphaFrom: Float = 0f
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

            val bounds = MaterialDialogUtil.getBoundsOnScreen(view)

            val viewTop = bounds.top
            val viewBottom = bounds.bottom
            val viewLeft = bounds.left
            val viewRight = bounds.right
            val viewWidth = bounds.width()
            val viewHeight = bounds.height()

            val pivotX = if (it.x < viewLeft) {
                0f
            } else if (it.x > viewRight) {
                viewWidth.toFloat()
            } else {
                it.x - viewLeft.toFloat()
            }

            val pivotY = if (it.y < viewTop) {
                0f
            } else if (it.y > viewBottom) {
                viewHeight.toFloat()
            } else {
                it.y - viewTop.toFloat()
            }
            view.pivotX = pivotX
            view.pivotY = pivotY

            Log.d("BOUNDS", "point = $it")
            Log.d("BOUNDS", "bounds = $bounds | ${bounds.left} - ${bounds.right}")
            Log.d("BOUNDS", "pivotX / pivotY = $pivotX / $pivotY")
            //Log.d("BOUNDS", "pViewCenter = $pViewCenter")
            //Log.d("BOUNDS", "xOffset = $xOffset, yOffset = $yOffset, pX = $pX, pY = $pY")
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