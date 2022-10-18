package com.michaelflisar.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import com.michaelflisar.dialogs.classes.BaseMaterialViewManager
import com.michaelflisar.dialogs.info.databinding.MdfContentInfoBinding
import com.michaelflisar.dialogs.interfaces.IMaterialDialogPresenter
import com.michaelflisar.dialogs.interfaces.IMaterialViewManager

internal class InfoViewManager(
    private val setup: DialogInfo
) : BaseMaterialViewManager<DialogInfo, MdfContentInfoBinding>() {

    override val wrapInScrollContainer = true

    override fun onCreateContentViewBinding(
        layoutInflater: LayoutInflater,
        parent: ViewGroup?,
        attachToParent: Boolean
    ) = MdfContentInfoBinding.inflate(layoutInflater, parent, attachToParent)

    override fun initBinding(
        presenter: IMaterialDialogPresenter<*, *, *>,
        savedInstanceState: Bundle?
    ) {
        setup.text.display(binding.mdfText)
    }
}