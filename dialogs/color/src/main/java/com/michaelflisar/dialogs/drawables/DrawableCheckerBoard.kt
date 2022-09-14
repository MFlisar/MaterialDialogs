package com.michaelflisar.dialogs.drawables

import android.graphics.*
import android.graphics.drawable.Drawable
import com.michaelflisar.dialogs.MaterialDialogUtil
import kotlin.math.min


class DrawableCheckerBoard : Drawable() {

    private val size = MaterialDialogUtil.dpToPx(8)
    private val p = createCheckerBoard(size)

    override fun draw(canvas: Canvas) {
        val w = bounds.width()
        val h = bounds.height()
        val minSize = min(w, h) / 2
        val paint = if (minSize < size) {
            createCheckerBoard(minSize)
        } else p
        canvas.drawPaint(paint)
    }

    override fun setAlpha(alpha: Int) {
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
    }

    override fun getOpacity(): Int {
        return PixelFormat.TRANSLUCENT
    }

    private fun createCheckerBoard(pixelSize: Int): Paint {

        val bitmap = Bitmap.createBitmap(pixelSize * 2, pixelSize * 2, Bitmap.Config.ARGB_8888)

        val p1 = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.FILL
            color = Color.parseColor("#FFC2C2C2")
        }
        val p2 = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.FILL
            color = Color.parseColor("#FFF3F3F3")
        }

        val canvas = Canvas(bitmap)
        val rect = Rect(0, 0, pixelSize, pixelSize)
        canvas.drawRect(rect, p1)
        rect.offset(pixelSize, 0)
        canvas.drawRect(rect, p2)
        rect.offset(-pixelSize, pixelSize)
        canvas.drawRect(rect, p2)
        rect.offset(pixelSize, 0)
        canvas.drawRect(rect, p1)

        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.shader =
            BitmapShader(bitmap, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT)

        return paint
    }
}