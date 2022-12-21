package com.michaelflisar.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import com.michaelflisar.dialogs.classes.BaseMaterialViewManager
import com.michaelflisar.dialogs.info.databinding.MdfContentInfoBinding
import com.michaelflisar.dialogs.interfaces.IMaterialDialogPresenter

internal class InfoViewManager(
    override val setup: DialogInfo
) : BaseMaterialViewManager<DialogInfo, MdfContentInfoBinding>() {

    override val wrapInScrollContainer = true

    override fun onCreateContentViewBinding(
        layoutInflater: LayoutInflater,
        parent: ViewGroup?,
        attachToParent: Boolean
    ) = MdfContentInfoBinding.inflate(layoutInflater, parent, attachToParent)

    override fun initBinding(savedInstanceState: Bundle?) {
        setup.text.display(binding.mdfText)
    }
}