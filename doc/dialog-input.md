# Dialog Input Module

| Input Dialog  |
| :---: |
| ![Dialog](../images/dialog_input.jpg?raw=true "Dialog") |

This module is placed inside the `dialogs-input` artifact and the main definition looks like following:

https://github.com/MFlisar/MaterialDialogs/blob/fcd9f94675bef09c60d818c2674ece8b852ccf9f/dialogs/input/src/main/java/com/michaelflisar/dialogs/DialogInput.kt#L18-L38

This dialog will emit events of the sealed class type `DialogColor.Event` that looks like following:

https://github.com/MFlisar/MaterialDialogs/blob/7b61dad709437ec49efac5d388450830b527c0f6/dialogs/color/src/main/java/com/michaelflisar/dialogs/DialogColor.kt#L48-L57
