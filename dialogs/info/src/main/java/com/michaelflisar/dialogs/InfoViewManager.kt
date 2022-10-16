package com.michaelflisar.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import com.michaelflisar.dialogs.classes.MaterialDialogParent
import com.michaelflisar.dialogs.info.databinding.MdfContentInfoBinding
import com.michaelflisar.dialogs.interfaces.IMaterialDialogPresenter
import com.michaelflisar.dialogs.interfaces.IMaterialViewManager

internal class InfoViewManager(
    private val setup: DialogInfo
) : IMaterialViewManager<DialogInfo, MdfContentInfoBinding> {

    override val wrapInScrollContainer = true

    override fun createContentViewBinding(
        layoutInflater: LayoutInflater,
        parent: ViewGroup?,
        attachToParent: Boolean
    ) = MdfContentInfoBinding.inflate(layoutInflater, parent, attachToParent)

    override fun initBinding(
        presenter: IMaterialDialogPresenter,
        binding: MdfContentInfoBinding,
        savedInstanceState: Bundle?
    ) {
        setup.text.display(binding.mdfText)
    }
}