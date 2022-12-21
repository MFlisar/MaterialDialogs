package com.michaelflisar.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import com.michaelflisar.dialogs.classes.BaseMaterialViewManager
import com.michaelflisar.dialogs.interfaces.IMaterialDialogPresenter
import com.michaelflisar.dialogs.template.databinding.MdfContentTemplateBinding

internal class TemplateViewManager(
    override val setup: DialogTemplate
) : BaseMaterialViewManager<DialogTemplate, MdfContentTemplateBinding>() {

    override val wrapInScrollContainer = true

    override fun onCreateContentViewBinding(
        layoutInflater: LayoutInflater,
        parent: ViewGroup?,
        attachToParent: Boolean
    ) = MdfContentTemplateBinding.inflate(layoutInflater, parent, attachToParent)

    override fun initBinding(savedInstanceState: Bundle?) {
        setup.text.display(binding.mdfText)
    }
}