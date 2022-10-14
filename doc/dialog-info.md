# Dialog Info Module

| Simple Info Dialog  | Info Dialog - with spannable text and icon | Info Dialog - with long content |
| :---: | :---: | :---: |
| ![Dialog](../images/dialog_info1.jpg?raw=true "Dialog") | ![Dialog](../images/dialog_info2.jpg?raw=true "Dialog") | ![Dialog](../images/dialog_info3.jpg?raw=true "Dialog") |

This module is placed inside the `dialogs-info` artifact and the main definition looks like following:

https://github.com/MFlisar/MaterialDialogs/blob/117349fcfd30a26a6b8afdf943a06e4270639775/dialogs/info/src/main/java/com/michaelflisar/dialogs/DialogInfo.kt#L16-L33

This dialog will emit events of the sealed class type `DialogInfo.Event` that looks like following:

https://github.com/MFlisar/MaterialDialogs/blob/117349fcfd30a26a6b8afdf943a06e4270639775/dialogs/info/src/main/java/com/michaelflisar/dialogs/DialogInfo.kt#L47-L60

