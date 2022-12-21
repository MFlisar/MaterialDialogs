package com.michaelflisar.dialogs

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.michaelflisar.dialogs.billing.R
import com.michaelflisar.dialogs.billing.databinding.MdfContentBillingBinding
import com.michaelflisar.dialogs.classes.BaseMaterialViewManager
import com.michaelflisar.dialogs.classes.BillingAdapter
import com.michaelflisar.dialogs.classes.MaterialDialogButton
import com.michaelflisar.kotbilling.KotBilling
import com.michaelflisar.kotbilling.classes.Product
import com.michaelflisar.kotbilling.classes.ProductType
import com.michaelflisar.kotbilling.results.IKBPurchaseQueryResult
import com.michaelflisar.kotbilling.results.KBError
import com.michaelflisar.kotbilling.results.KBProductDetailsList
import com.michaelflisar.kotbilling.results.KBPurchaseQuery
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

internal class BillingViewManager(
    override val setup: DialogBilling
) : BaseMaterialViewManager<DialogBilling, MdfContentBillingBinding>() {

    private val TESTING = false

    override val wrapInScrollContainer = false

    private val adapter = BillingAdapter(::onItemClicked)

    override fun onCreateContentViewBinding(
        layoutInflater: LayoutInflater,
        parent: ViewGroup?,
        attachToParent: Boolean
    ) = MdfContentBillingBinding.inflate(layoutInflater, parent, attachToParent)

    override fun initBinding(savedInstanceState: Bundle?) {

        setup.information.display(binding.mdfDescription)

        binding.mdfError.visibility = View.GONE
        binding.mdfRecyclerView.visibility = View.GONE

        binding.mdfRecyclerView.layoutManager =
            LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        binding.mdfRecyclerView.adapter = adapter

        // load data of products
        presenter.requireLifecycleOwner().lifecycleScope.launch(Dispatchers.IO) {
            val products = KotBilling.queryProducts(setup.products.map { it.product })
            val purchasesInApp = KotBilling.queryPurchases(ProductType.InApp)
            val purchasesSubscription = KotBilling.queryPurchases(ProductType.Subscription)
            MaterialDialog.logger?.invoke(Log.DEBUG, "products = $products", null)
            MaterialDialog.logger?.invoke(Log.DEBUG, "purchasesInApp = $purchasesInApp", null)
            MaterialDialog.logger?.invoke(
                Log.DEBUG,
                "purchasesSubscription = $purchasesSubscription",
                null
            )
            withContext(Dispatchers.Main) {
                when (products) {
                    is KBError -> setDataErrorUI()
                    is KBProductDetailsList -> {
                        // testing
                        if (TESTING) {
                            updateProductUI(
                                setup.products.mapIndexed { index, billingProduct ->
                                    BillingAdapter.Item(
                                        billingProduct,
                                        billingProduct.product.id,
                                        "${index + 1}.0€",
                                        index % 2 == 1
                                    )
                                }
                            )
                        } else {
                            updateProductUI(products.details.map { detail ->
                                val product =
                                    setup.products.find { it.product.id == detail.product.id }!!
                                BillingAdapter.Item(
                                    product,
                                    detail.details.name,
                                    detail.details.singlePrice,
                                    isOwned(detail.product, purchasesInApp, purchasesSubscription)
                                )
                            })
                        }
                    }
                }
                binding.mdfLoading.visibility = View.GONE
            }
        }
    }

    override fun onButtonsReady() {
        if (adapter.itemCount == 0)
            presenter.setButtonEnabled(MaterialDialogButton.Positive, false)
    }

    private fun onItemClicked(item: BillingAdapter.Item, pos: Int) {
        if (item.owned == true) {
            Toast.makeText(
                presenter.requireContext(),
                R.string.mdf_billing_item_already_owned,
                Toast.LENGTH_SHORT
            ).show()
        } else {
            val activity = MaterialDialogUtil.getActivity(presenter.requireContext())!!
            GlobalScope.launch {
                val result = KotBilling.purchase(activity, item.product.product, null)
                withContext(Dispatchers.Main) {
                    (setup.eventManager as BillingEventManager).sendEvent(presenter, result)
                    presenter.dismiss?.invoke()
                }
            }
        }
    }

    private fun updateProductUI(items: List<BillingAdapter.Item>) {
        val items = if (setup.showOwnedProducts) items else items.filter { it.owned != true }
        if (items.isEmpty()) {
            binding.mdfError.visibility = View.VISIBLE
            binding.mdfError.text = "No product found!"
        } else {
            binding.mdfRecyclerView.visibility = View.VISIBLE
            adapter.update(items)
            if (items.size == 1) {
                presenter.setButtonEnabled(MaterialDialogButton.Positive, true)
            }
        }
    }

    private fun setDataErrorUI() {
        binding.mdfRecyclerView.visibility = View.GONE
        binding.mdfLoading.visibility = View.GONE
    }

    private fun isOwned(
        product: Product,
        purchasesInApp: IKBPurchaseQueryResult,
        purchasesSubscription: IKBPurchaseQueryResult
    ): Boolean? {
        val resultToCheck = when (product.type) {
            ProductType.InApp -> purchasesInApp
            ProductType.Subscription -> purchasesSubscription
        }
        return when (resultToCheck) {
            is KBError -> null
            is KBPurchaseQuery -> resultToCheck.details.find { it.products.contains(product.id) }?.isOwned
        }
    }

    internal fun buyFirstProduct() {
        adapter.items.firstOrNull()?.let {
            onItemClicked(it, 0)
        }
    }
}