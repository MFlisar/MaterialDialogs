# Dialog Info Module

| Simple Info Dialog  | Info Dialog - with spannable text and icon | Info Dialog - with long content |
| :---: | :---: | :---: |
| ![Dialog](../images/dialog_info1.jpg?raw=true "Dialog") | ![Dialog](../images/dialog_info2.jpg?raw=true "Dialog") | ![Dialog](../images/dialog_info3.jpg?raw=true "Dialog") |

This module is placed inside the `dialogs-info` artifact and the main definition looks like following:

https://github.com/MFlisar/MaterialDialogs/blob/4b38b133d423a0313e557d617589e88fbaaed413/dialogs/info/src/main/java/com/michaelflisar/dialogs/DialogInfo.kt#L16-L32

This dialog will emit events of the sealed class type `DialogInfo.Event` that looks like following:

https://github.com/MFlisar/MaterialDialogs/blob/4b38b133d423a0313e557d617589e88fbaaed413/dialogs/info/src/main/java/com/michaelflisar/dialogs/DialogInfo.kt#L46-L54
