# Dialog Billing Module

| Single Item Billing Dialog | Multiple Item Banner Dialog |
| :---: | :---: |
| ![Dialog](../images/dialog_billing1.jpg?raw=true "Dialog") | ![Dialog](../images/dialog_billing2.jpg?raw=true "Dialog") |

This module is placed inside the `dialogs-billing` artifact and the main definition looks like following:

https://github.com/MFlisar/MaterialDialogs/blob/9e71297d53fd79646f9d444faefc3062c59c1629/dialogs/ads/src/main/java/com/michaelflisar/dialogs/DialogAds.kt#L24-L42

With following setup class:

https://github.com/MFlisar/MaterialDialogs/blob/9e71297d53fd79646f9d444faefc3062c59c1629/dialogs/ads/src/main/java/com/michaelflisar/dialogs/DialogAds.kt#L106-L153

This dialog will emit events of the sealed class type `DialogAds.Event` that looks like following:

https://github.com/MFlisar/MaterialDialogs/blob/9e71297d53fd79646f9d444faefc3062c59c1629/dialogs/ads/src/main/java/com/michaelflisar/dialogs/DialogAds.kt#L59-L80

