# Dialog Info Module

| GDPR Dialog  | |  |
| :---: | :---: | :---: |
| ![Dialog](../images/dialog_gdpr1.jpg?raw=true "Dialog") |  |  |

This module is placed inside the `dialogs-gdpr` artifact and the main definition looks like following:

https://github.com/MFlisar/MaterialDialogs/blob/117349fcfd30a26a6b8afdf943a06e4270639775/dialogs/info/src/main/java/com/michaelflisar/dialogs/DialogInfo.kt#L16-L33

This dialog will emit events of the sealed class type `DialogGDPR.Event` that looks like following:

https://github.com/MFlisar/MaterialDialogs/blob/117349fcfd30a26a6b8afdf943a06e4270639775/dialogs/info/src/main/java/com/michaelflisar/dialogs/DialogInfo.kt#L47-L60

**ATTENTION**

This module depends on my `KotBilling` library - check it out here: https://github.com/MFlisar/KotBilling
