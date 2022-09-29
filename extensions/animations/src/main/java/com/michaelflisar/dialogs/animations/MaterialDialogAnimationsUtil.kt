package com.michaelflisar.dialogs.animations

import android.graphics.Point
import android.graphics.PointF
import android.view.View
import com.michaelflisar.dialogs.MaterialDialogUtil

object MaterialDialogAnimationsUtil {

    fun calcRelativeOffsetFromAbsolutePoint(view: View, point: Point): PointF {

        val bounds = MaterialDialogUtil.getBoundsOnScreen(view)

        val viewTop = bounds.top
        val viewBottom = bounds.bottom
        val viewLeft = bounds.left
        val viewRight = bounds.right
        val viewWidth = bounds.width()
        val viewHeight = bounds.height()

        val pivotX = if (point.x < viewLeft) {
            0f
        } else if (point.x > viewRight) {
            viewWidth.toFloat()
        } else {
            point.x - viewLeft.toFloat()
        }

        val pivotY = if (point.y < viewTop) {
            0f
        } else if (point.y > viewBottom) {
            viewHeight.toFloat()
        } else {
            point.y - viewTop.toFloat()
        }

        return PointF(pivotX, pivotY)
    }
}