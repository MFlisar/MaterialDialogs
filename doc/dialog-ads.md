# Dialog Ads Module

| Ad Banner Dialog | Ad Banner Dialog |
| :---: | :---: |
| ![Dialog](../images/dialog_ads1.jpg?raw=true "Dialog") | ![Dialog](../images/dialog_ads2.jpg?raw=true "Dialog") |

This module is placed inside the `dialogs-ads` artifact and the main definition looks like following:

https://github.com/MFlisar/MaterialDialogs/blob/9e71297d53fd79646f9d444faefc3062c59c1629/dialogs/ads/src/main/java/com/michaelflisar/dialogs/DialogAds.kt#L24-L42

With following setup class:

https://github.com/MFlisar/MaterialDialogs/blob/9e71297d53fd79646f9d444faefc3062c59c1629/dialogs/ads/src/main/java/com/michaelflisar/dialogs/DialogAds.kt#L106-L153

This dialog will emit events of the sealed class type `DialogAds.Event` that looks like following:

https://github.com/MFlisar/MaterialDialogs/blob/9e71297d53fd79646f9d444faefc3062c59c1629/dialogs/ads/src/main/java/com/michaelflisar/dialogs/DialogAds.kt#L59-L80

**ATTENTION**

Currently only the banner dialog works, because I could not solve the issue that I can't listen to the dismiss event of full screen ads yet... (as written here: https://stackoverflow.com/questions/74864756/fullscreencontentcallback-onaddismissedfullscreencontent-never-called)