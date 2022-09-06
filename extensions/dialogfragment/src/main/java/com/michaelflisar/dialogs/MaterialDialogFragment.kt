package com.michaelflisar.dialogs

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.viewbinding.ViewBinding
import com.michaelflisar.dialogs.interfaces.IMaterialDialogAnimation
import com.michaelflisar.dialogs.interfaces.IMaterialDialogEvent

internal class MaterialDialogFragment<S : MaterialDialogSetup<S, B, E>, B : ViewBinding, E: IMaterialDialogEvent> : AppCompatDialogFragment() {

    companion object {

        const val KEY_ANIMATION_STATE = "MaterialDialogFragment|ANIMATIONSTATE"
        const val ARG_SETUP = "MaterialDialogFragment|SETUP"
        const val ARG_ANIMATION = "MaterialDialogFragment|ANIMATION"

        fun <S : MaterialDialogSetup<S, B, E>, B : ViewBinding, E: IMaterialDialogEvent> create(
            setup: S,
            animation: IMaterialDialogAnimation?
        ): MaterialDialogFragment<S, B, E> {
            return MaterialDialogFragment<S, B, E>().apply {
                val args = Bundle()
                args.putParcelable(ARG_SETUP, setup)
                args.putParcelable(ARG_ANIMATION, animation)
                arguments = args
            }
        }
    }

    private lateinit var presenter: DialogFragmentPresenter<S, B, E>
    private var animationDone: Boolean = false

    // ------------------
    // Fragment
    // ------------------

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        presenter = DialogFragmentPresenter(requireArguments().getParcelable(ARG_SETUP)!!, this)
        val animation = requireArguments().getParcelable<IMaterialDialogAnimation?>(ARG_ANIMATION)
        presenter.onCreate(savedInstanceState, animation)
        animationDone = savedInstanceState?.getBoolean(KEY_ANIMATION_STATE) ?: false
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return presenter.onCreateDialog(requireContext(), savedInstanceState)
    }

/*

    override fun onStart() {
        super.onStart()
        dialogManager.onStart()
        initAnimation()
    }

    private fun initAnimation() {

        if (animationDone)
            return

        val rootDialog = dialog?.window?.decorView
        val root = rootDialog ?: view

        val style = setup.style
        val dimBackground = when (style) {
            is DialogStyle.BottomSheet -> style.dimBackground
            is DialogStyle.Dialog -> style.dimBackground
            is DialogStyle.FullScreen -> null
        }
        if (dimBackground == false) {
            dialog?.window?.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        }

        Log.d("BOUNDS", "rootDialog = $rootDialog | rootView = $view")

        val animation = setup.style.animation
        when (animation) {
            DialogAnimation.Default -> {
                // nothing to do...
            }
            is DialogAnimation.Reveal -> {
                // TODO...
            }
            is DialogAnimation.FadeScale -> {
                root?.post {
                    val anim = root.animate()
                    val bounds = root.getBoundsOnScreen()

                    anim.cancel()
                    animation.point?.let {

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
                        root.pivotX = pivotX
                        root.pivotY = pivotY

                        Log.d("BOUNDS", "point = $it")
                        Log.d("BOUNDS", "bounds = $bounds | ${bounds.left} - ${bounds.right}")
                        Log.d("BOUNDS", "pivotX / pivotY = $pivotX / $pivotY")
                        //Log.d("BOUNDS", "pViewCenter = $pViewCenter")
                        //Log.d("BOUNDS", "xOffset = $xOffset, yOffset = $yOffset, pX = $pX, pY = $pY")
                    }
                    root.scaleX = animation.scaleFromX
                    root.scaleY = animation.scaleFromY
                    root.alpha = animation.alphaFrom
                    anim.scaleX(1f)
                    anim.scaleY(1f)
                    anim.alpha(1f)
                    anim.duration = animation.duration
                    anim.start()
                    animationDone = true
                }
            }
        }

    }

    private fun hideAnimated(onAnimationDone: () -> Unit) {
        val rootDialog = dialog?.window?.decorView
        val root = rootDialog ?: view

        Log.d("BOUNDS", "rootDialog = $rootDialog | rootView = $view")

        val animation = setup.style.animation
        when (animation) {
            DialogAnimation.Default -> {
                // nothing to do...
                onAnimationDone()
            }
            is DialogAnimation.Reveal -> {
                // TODO...
                onAnimationDone()
            }
            is DialogAnimation.FadeScale -> {
                if (root != null) {
                    val anim = root.animate()
                    anim.cancel()
                    anim.scaleX(0f)
                    anim.scaleY(0f)
                    anim.alpha(0f)
                    anim.duration = animation.duration
                    anim.withEndAction {
                        onAnimationDone()
                    }
                    anim.start()
                } else {
                    onAnimationDone()
                }
            }
        }
    }
*/

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        presenter.saveViewState(outState)
        outState.putBoolean(KEY_ANIMATION_STATE, animationDone)
    }

    override fun onDestroy() {
        presenter.onDestroy()
        super.onDestroy()
    }

    override fun dismiss() {
        if (presenter.onBeforeDismiss(false))
            super.dismiss()
    }

    override fun dismissAllowingStateLoss() {
        if (presenter.onBeforeDismiss(true))
            super.dismiss()
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        presenter.onCancelled()
    }

    // ------------------
    // internal calls to super functions
    // ------------------
/*
    internal fun superDismiss() {
        super.dismiss()
    }

    internal fun superDismissAllowingStateLoss() {
        super.dismissAllowingStateLoss()
    }*/
}