package com.michaelflisar.dialogs

import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.michaelflisar.dialogs.classes.BaseMaterialViewManager
import com.michaelflisar.dialogs.classes.InstanceManager
import com.michaelflisar.dialogs.interfaces.IMaterialDialogPresenter
import com.michaelflisar.dialogs.progress.databinding.MdfContentProgressBinding
import com.michaelflisar.text.Text
import kotlinx.parcelize.Parcelize

internal class ProgressViewManager(
    private val setup: DialogProgress,
) : BaseMaterialViewManager<DialogProgress, MdfContentProgressBinding>() {

    override val wrapInScrollContainer = false
    private var state = State(setup.text)

    override fun onCreateContentViewBinding(
        layoutInflater: LayoutInflater,
        parent: ViewGroup?,
        attachToParent: Boolean
    ): MdfContentProgressBinding =
        MdfContentProgressBinding.inflate(layoutInflater, parent, attachToParent)

    override fun initBinding(
        presenter: IMaterialDialogPresenter<*, *, *>,
        savedInstanceState: Bundle?
    ) {
        if (savedInstanceState != null) {
            state = savedInstanceState.getParcelable("state")!!
        } else {
            state = State(setup.text)
        }
        binding.pbProgress.visibility = if (setup.horizontal) View.GONE else View.VISIBLE
        binding.pbProgressHorizontal.visibility = if (setup.horizontal) View.VISIBLE else View.GONE
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

    @Parcelize
    private data class State(
        val text: Text
    ) : Parcelable
}