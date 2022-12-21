# Dialog GDPR Module

| GDPR Dialog  |
| :---: |
| ![Dialog](../images/dialog_gdpr1.jpg?raw=true "Dialog") |

This module is placed inside the `dialogs-gdpr` artifact and the main definition looks like following:

https://github.com/MFlisar/MaterialDialogs/blob/9e71297d53fd79646f9d444faefc3062c59c1629/dialogs/gdpr/src/main/java/com/michaelflisar/dialogs/DialogGDPR.kt#L17-L30

With following setup class:

https://github.com/MFlisar/MaterialDialogs/blob/9e71297d53fd79646f9d444faefc3062c59c1629/dialogs/gdpr/src/main/java/com/michaelflisar/dialogs/classes/GDPRSetup.kt#L10-L27

This dialog will emit events of the sealed class type `DialogGDPR.Event` that looks like following:

https://github.com/MFlisar/MaterialDialogs/blob/9e71297d53fd79646f9d444faefc3062c59c1629/dialogs/gdpr/src/main/java/com/michaelflisar/dialogs/DialogGDPR.kt#L56-L69

**ATTENTION**

This module depends on my `KotBilling` library - check it out here: https://github.com/MFlisar/KotBilling
