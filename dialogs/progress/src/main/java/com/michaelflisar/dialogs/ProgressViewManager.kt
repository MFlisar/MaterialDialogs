package com.michaelflisar.dialogs

import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import com.michaelflisar.dialogs.classes.BaseMaterialViewManager
import com.michaelflisar.dialogs.classes.InstanceManager
import com.michaelflisar.dialogs.interfaces.IMaterialDialogPresenter
import com.michaelflisar.dialogs.progress.databinding.MdfContentProgressBinding
import com.michaelflisar.text.Text
import kotlinx.parcelize.Parcelize

internal class ProgressViewManager(
    override val setup: DialogProgress,
) : BaseMaterialViewManager<DialogProgress, MdfContentProgressBinding>() {

    override val wrapInScrollContainer = false
    private var state = State()

    private val progressBar: ProgressBar
        get() = if (setup.horizontal) binding.pbProgressHorizontal else binding.pbProgress

    override fun onCreateContentViewBinding(
        layoutInflater: LayoutInflater,
        parent: ViewGroup?,
        attachToParent: Boolean
    ): MdfContentProgressBinding =
        MdfContentProgressBinding.inflate(layoutInflater, parent, attachToParent)

    override fun initBinding(
        presenter: IMaterialDialogPresenter<*>,
        savedInstanceState: Bundle?
    ) {
        if (savedInstanceState != null) {
            state = savedInstanceState.getParcelable("state")!!
        } else {
            state = State(setup.text, 0)
        }

        (if (setup.horizontal) {
            binding.pbProgress
        } else binding.pbProgressHorizontal).visibility = View.GONE

        binding.tvText.gravity = setup.textGravity
        progressBar.isIndeterminate = setup.indeterminate
        progressBar.progress = state.progress

        state.text.display(binding.tvText)
        InstanceManager.register(setup.id, presenter)
    }

    override fun saveViewState(outState: Bundle) {
        super.saveViewState(outState)
        outState.putParcelable("state", state)
    }

    override fun onDestroy() {
        InstanceManager.unregister(setup.id)
        super.onDestroy()
    }

    internal fun updateText(text: Text) {
        text.display(binding.tvText)
    }

    internal fun updateProgress(progress: Int) {
        state = state.copy(progress = progress)
        progressBar.progress = progress
    }

    @Parcelize
    private data class State(
        val text: Text = Text.Empty,
        val progress: Int = 0
    ) : Parcelable
}