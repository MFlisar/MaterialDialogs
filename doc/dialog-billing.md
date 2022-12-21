# Dialog Billing Module

| Single Item Billing Dialog | Multiple Item Banner Dialog |
| :---: | :---: |
| ![Dialog](../images/dialog_billing1.jpg?raw=true "Dialog") | ![Dialog](../images/dialog_billing2.jpg?raw=true "Dialog") |

This module is placed inside the `dialogs-billing` artifact and the main definition looks like following:

https://github.com/MFlisar/MaterialDialogs/blob/ceb3c91f23fa5ffefbcdb012ecad7c2a47e22baf/dialogs/billing/src/main/java/com/michaelflisar/dialogs/DialogBilling.kt#L20-L37

This dialog will emit events of the sealed class type `DialogBilling.Event` that looks like following:

https://github.com/MFlisar/MaterialDialogs/blob/ceb3c91f23fa5ffefbcdb012ecad7c2a47e22baf/dialogs/billing/src/main/java/com/michaelflisar/dialogs/DialogBilling.kt#L59-L73

