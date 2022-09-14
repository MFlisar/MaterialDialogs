package com.michaelflisar.dialogs.drawables

import android.graphics.*
import android.graphics.Color
import android.graphics.drawable.Drawable

class DrawableBlackWhite : Drawable() {

    private var paintBlack: Paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        this.color = Color.BLACK
    }

    private var paintWhite: Paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        this.color = Color.WHITE
    }

    override fun draw(canvas: Canvas) {
        val w = bounds.width().toFloat()
        val h = bounds.height().toFloat()
        canvas.save()
        canvas.clipRect(0f, 0f, w / 2f, h)
        canvas.drawPaint(paintBlack)
        canvas.restore()
        canvas.save()
        canvas.clipRect(w / 2f, 0f, w, h)
        canvas.drawPaint(paintWhite)
        canvas.restore()
    }

    override fun setAlpha(alpha: Int) {
        paintWhite.alpha = alpha
        paintBlack.alpha = alpha
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        paintWhite.colorFilter = colorFilter
        paintBlack.colorFilter = colorFilter
    }

    override fun getOpacity(): Int {
        return PixelFormat.TRANSLUCENT
    }
}