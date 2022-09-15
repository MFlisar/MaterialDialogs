# Extension Module

### Animations

This module is placed inside the `extensions-animations` artifact and provides following:

* [MaterialDialogFadeScaleAnimation](../extensions/animations/src/main/java/com/michaelflisar/dialogs/animations/MaterialDialogFadeScaleAnimation.kt)
* [MaterialDialogRevealAnimation](../extensions/animations/src/main/java/com/michaelflisar/dialogs/animations/MaterialDialogRevealAnimation.kt)

Simply create instances of those animations and provide it to the `Dialog.show...` method like following:

```kotlin
val animation = MaterialDialogRevealAnimation(250L)
val animation = MaterialDialogFadeScaleAnimation(250L, ...)
val dialog = ...
dialog.showAlertDialog(activity, animation)
dialog.showDialogFragment(activity, animation)
```

### DialogFragment / Bottomsheet / Fullscreen

Those extensions allow you to show dialogs as fragments and in different styles. They are placed in the `extensions-fragment-dialog`, `extensions-fragment-bottomsheet` and `extensions-fragment-fullscreen` artifacts.

They provide extension functions that can be used like following:

```kotlin
val dialog = ...
dialog.showAlertDialog(activity)
dialog.showDialogFragment(activity)
dialog.showBottomSheetDialogFragment(activity)
dialog.showFullscreenFragment(activity)
```

| Bottomsheet Style | Fullscreen Style | Fullscreen Style |
| :---: | :---: | :---: |
| ![Dialog](../images/style_bottomsheet.jpg?raw=true "Dialog") | ![Dialog](../images/style_fullscreen1.jpg?raw=true "Dialog") | ![Dialog](../images/style_fullscreen2.jpg?raw=true "Dialog") |
