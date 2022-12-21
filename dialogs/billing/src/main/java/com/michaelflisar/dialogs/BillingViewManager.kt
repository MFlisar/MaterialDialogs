package com.michaelflisar.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.michaelflisar.dialogs.billing.databinding.MdfContentBillingBinding
import com.michaelflisar.dialogs.classes.BaseMaterialViewManager
import com.michaelflisar.kotbilling.KotBilling
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

internal class BillingViewManager(
    override val setup: DialogBilling
) : BaseMaterialViewManager<DialogBilling, MdfContentBillingBinding>() {

    override val wrapInScrollContainer = true

    override fun onCreateContentViewBinding(
        layoutInflater: LayoutInflater,
        parent: ViewGroup?,
        attachToParent: Boolean
    ) = MdfContentBillingBinding.inflate(layoutInflater, parent, attachToParent)

    override fun initBinding(savedInstanceState: Bundle?) {

        // TODO...
        presenter.requireLifecycleOwner().lifecycleScope.launch(Dispatchers.IO) {
            val res = KotBilling.queryProducts(listOf(setup.product))

        }

        //setup.text.display(binding.mdfText)
    }
}