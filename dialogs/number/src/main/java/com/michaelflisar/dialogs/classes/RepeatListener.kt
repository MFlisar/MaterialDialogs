package com.michaelflisar.dialogs.classes

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Looper
import android.view.MotionEvent
import android.view.View


/**
 * @param initialInterval The interval after first click event
 * @param normalInterval The interval after second and subsequent click
 * events
 * @param clickListener The OnClickListener, that will be called
 * periodically
 */
class RepeatListener(
    val initialInterval: Long,
    val normalInterval: Long,
    val clickListener: View.OnClickListener
) : View.OnTouchListener {

    private val handler = Handler(Looper.getMainLooper())

    private val handlerRunnable = object : Runnable {
        override fun run() {
            handler.postDelayed(this, normalInterval)
            clickListener.onClick(downView)
        }
    }

    private var downView: View? = null


    init {
        if (initialInterval < 0 || normalInterval < 0)
            throw IllegalArgumentException("Negative intervals are not allowed!")
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(view: View, motionEvent: MotionEvent): Boolean {
        when (motionEvent.action) {
            MotionEvent.ACTION_DOWN -> {
                handler.removeCallbacks(handlerRunnable)
                handler.postDelayed(handlerRunnable, initialInterval)
                downView = view
                downView?.isPressed = true
                clickListener.onClick(view)
                return true
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                handler.removeCallbacks(handlerRunnable)
                downView?.isPressed = false
                downView = null
                return true
            }
        }

        return false
    }
}