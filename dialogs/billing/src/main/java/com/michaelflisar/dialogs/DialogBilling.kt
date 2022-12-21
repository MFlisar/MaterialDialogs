package com.michaelflisar.dialogs

import android.os.Parcelable
import com.michaelflisar.dialogs.billing.R
import com.michaelflisar.dialogs.billing.databinding.MdfContentBillingBinding
import com.michaelflisar.dialogs.classes.Icon
import com.michaelflisar.dialogs.classes.MaterialDialogAction
import com.michaelflisar.dialogs.classes.MaterialDialogButton
import com.michaelflisar.dialogs.interfaces.IMaterialDialogEvent
import com.michaelflisar.dialogs.interfaces.IMaterialEventManager
import com.michaelflisar.dialogs.interfaces.IMaterialViewManager
import com.michaelflisar.kotbilling.classes.Product
import com.michaelflisar.kotbilling.results.IKBPurchaseResult
import com.michaelflisar.text.Text
import com.michaelflisar.text.asText
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Parcelize
class DialogBilling(
    // Key
    override val id: Int? = null,
    // Header
    override val title: Text = Text.Empty,
    override val icon: Icon = Icon.None,
    override val menu: Int? = null,
    // specific fields
    val products: List<BillingProduct>,
    val information: Text,
    val showOwnedProducts: Boolean = true,
    // Buttons
    override val buttonNeutral: Text = android.R.string.cancel.asText(),
    // Style
    override val cancelable: Boolean = MaterialDialog.defaults.cancelable,
    // Attached Data
    override val extra: Parcelable? = null
) : MaterialDialogSetup<DialogBilling>() {

    @IgnoredOnParcel
    override val buttonPositive: Text = if (products.size == 1) {
        R.string.mdf_billing_button_buy_product.asText()
    } else Text.Empty

    @IgnoredOnParcel
    override val buttonNegative: Text = Text.Empty

    @IgnoredOnParcel
    override val viewManager: IMaterialViewManager<DialogBilling, MdfContentBillingBinding> =
        BillingViewManager(this)

    @IgnoredOnParcel
    override val eventManager: IMaterialEventManager<DialogBilling> =
        BillingEventManager(this)

    // -----------
    // Result Events
    // -----------

    sealed class Event : IMaterialDialogEvent {

        data class Result(
            override val id: Int?,
            override val extra: Parcelable?,
            val button: MaterialDialogButton?,
            val purchase: IKBPurchaseResult?
        ) : Event()

        data class Action(
            override val id: Int?,
            override val extra: Parcelable?,
            override val data: MaterialDialogAction
        ) : Event(), IMaterialDialogEvent.Action
    }

    // -----------
    // Enums/Classes
    // -----------

    @Parcelize
    data class BillingProduct(
        val product: com.michaelflisar.kotbilling.classes.Product,
        val icon: Icon = Icon.None
    ) : Parcelable

    //sealed class PurchaseType : Parcelable {
//
    //    @Parcelize
    //    class SingleProduct(
    //        val information: Text,
    //        val product: Product
    //    ) : PurchaseType()
//
    //    class Select
    //}
}