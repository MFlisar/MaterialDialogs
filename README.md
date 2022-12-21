# Material Dialogs  [![Release](https://jitpack.io/v/MFlisar/materialdialogs.svg)](https://jitpack.io/#MFlisar/MaterialDialogs) ![License](https://img.shields.io/github/license/MFlisar/MaterialDialogs)

This library helps to show a `Dialog` and takes care of sending events to the parent `Activity`/`Fragment` without leaking it even if the dialogs are shown as `DialogFragments`. It's made for the `Theme.Material3` theme and tries to follow styling that's described here:

[M3 Material Dialogs](https://m3.material.io/components/dialogs/implementation/android)

It supports following styling "types" and changing between styles is as simple as calling a different show function:

* **AlertDialog**
* **DialogFragment**
* **BottomSheetDialogFragment**
* **FullscreenDialog**

All the `Fragment` modes support restoring intermdediate view states and automatic restoration of the event emission logic to the correct parent without leaking it.

Additionally, the dialogs support `View` based animations as well like e.g. `CircularReveal`.

# Gradle (via [JitPack.io](https://jitpack.io/))

1. add jitpack to your project's `build.gradle`:
```
repositories {
    maven { url "https://jitpack.io" }
}
```
2. add the compile statement to your module's `build.gradle`:
```
dependencies {
    // core module - contains the common logic and allows to display a dialog as "AlertDialog"
    implementation "com.github.MFlisar.MaterialDialogs:core:<LATEST-VERSION>"
    // dialog modules
    implementation "com.github.MFlisar.MaterialDialogs:dialogs-info:<LATEST-VERSION>"
    implementation "com.github.MFlisar.MaterialDialogs:dialogs-input:<LATEST-VERSION>"
    implementation "com.github.MFlisar.MaterialDialogs:dialogs-list:<LATEST-VERSION>"
    implementation "com.github.MFlisar.MaterialDialogs:dialogs-number:<LATEST-VERSION>"
    implementation "com.github.MFlisar.MaterialDialogs:dialogs-datetime:<LATEST-VERSION>"
    implementation "com.github.MFlisar.MaterialDialogs:dialogs-color:<LATEST-VERSION>"
    implementation "com.github.MFlisar.MaterialDialogs:dialogs-ads:<LATEST-VERSION>"
    implementation "com.github.MFlisar.MaterialDialogs:dialogs-gdpr:<LATEST-VERSION>"
    // extensions for additional "display modes" and for custom animations
    implementation "com.github.MFlisar.MaterialDialogs:extensions-animations:<LATEST-VERSION>"
    implementation "com.github.MFlisar.MaterialDialogs:extensions-fragment-dialog:<LATEST-VERSION>"
    implementation "com.github.MFlisar.MaterialDialogs:extensions-fragment-bottomsheet:<LATEST-VERSION>"
    implementation "com.github.MFlisar.MaterialDialogs:extensions-fragment-fullscreen:<LATEST-VERSION>"
}
```

# Example

It works as simple as following: From within an `Activity`/`Fragment` create a dialog like following:

```kotlin
DialogInfo(
  id = 1,
  title = "Info Title".asText(), // Int, String and any CharSequence are supported (e.g. SpannableString)
  text = "Some info text...".asText(),
  extra = <SOME OPTIONAL DATA> // attach any additional data (it must be parcelable) you may want to use when handling the result
)
  .showAlertDialog(context) // simple context is enough for the AlertDialog mode
  // OR following ( parent is a fragment or an activity)
  .showDialogFragment(parent)
  .showBottomSheetDialogFragment(parent)
  .showFullscreenFragment(parent)
```

From any lifecycle aware component (like e.g. an `Activity`/`Fragment`) you can do then following:

```kotlin
override fun onCreate(savedInstanceState: Bundle?) {
  super.onCreate(savedInstanceState)
  
  // - ID is optional, you can also listen to all events of a special type if desired
  // - onMaterialDialogEvent is an extension function on LifecycleOwner
  onMaterialDialogEvent<DialogInfo.Event>(id = 1) { event ->
    // dialog event received -> process it...
  }
}
```

Additionally, the `AlertDialog` mode does support providing a direct callback, simply like following:

```kotlin
DialogInfo(...)
  .showAlertDialog(parent) { event ->
    // event of Type "DialogInfo.Event" will be directly emitted here...
  }
```

That's it, the library will take care to unregister the listener if the `Activity`/`Fragment` is destroyed and will avoid leaks like this. Even though the `DialogFragments` are recreated automatically after restoration and screen rotation the parent will be able to receive all events without any further code requirements by the developer. 

# Screenshots

### Dialog Types

| | | | |
| :---: | :---: | :---: | :---: |
| ![Dialog](images/dialog_info1.jpg?raw=true "Dialog") | ![Dialog](images/dialog_info2.jpg?raw=true "Dialog") | ![Dialog](images/dialog_info3.jpg?raw=true "Dialog") | ![Dialog](images/dialog_input.jpg?raw=true "Dialog") |
| ![Dialog](images/dialog_list_singleselect.jpg?raw=true "Dialog") | ![Dialog](images/dialog_list_multiselect.jpg?raw=true "Dialog") | ![Dialog](images/dialog_list_multiclick.jpg?raw=true "Dialog") | ![Dialog](images/dialog_list_custom.jpg?raw=true "Dialog") |
| ![Dialog](images/dialog_number1.jpg?raw=true "Dialog") | ![Dialog](images/dialog_number2.jpg?raw=true "Dialog") | ![Dialog](images/dialog_date.jpg?raw=true "Dialog") | ![Dialog](images/dialog_time1.jpg?raw=true "Dialog") |
| ![Dialog](images/dialog_time2.jpg?raw=true "Dialog") | ![Dialog](images/dialog_color1.jpg?raw=true "Dialog") | ![Dialog](images/dialog_color2.jpg?raw=true "Dialog") | ![Dialog](images/dialog_ads1.jpg?raw=true "Dialog") | 
| ![Dialog](images/dialog_ads2.jpg?raw=true "Dialog") | ![Dialog](images/dialog_gdpr1.jpg?raw=true "Dialog") | | |

### Dialog Styles

| | | |
| :---: | :---: | :---: |
| ![Dialog](images/style_bottomsheet.jpg?raw=true "Dialog") | ![Dialog](images/style_fullscreen1.jpg?raw=true "Dialog") | ![Dialog](images/style_fullscreen2.jpg?raw=true "Dialog") |

# Modules

Readmes are split up for each module and can be found inside the `doc` folder, here are the links:

### Dialogs

* [Info](doc/dialog-info.md)
* [Input](doc/dialog-input.md)
* [List](doc/dialog-list.md)
* [Number](doc/dialog-number.md)
* [DateTime](doc/dialog-datetime.md)
* [Color](doc/dialog-color.md)
* [Ads](doc/dialog-ads.md)
* [GDPR](doc/dialog-gdpr.md)

### Others

* [Extensions (all)](doc/extensions.md)

# DEMO APP

Check the [demo app](app/src/main/java/com/michaelflisar/dialogs/MainActivity.kt) for more informations.

# TODO

- [ ] ButtonsView: support stacking
- [ ] Swipe Dismiss Feature
- [ ] Features 
    - [ ] BottomSheet - flag to support "expand to fullscreen style" (pos button in toolbar is enabled, pos button in footer is removed, toolbar replaces the title)
- [ ] Dialogss
    - [x] Info
    - [x] Input
    - [x] List
    - [x] NumberPicker
    - [x] NumberPicker
    - [x] DateTime (=> Custom Impl with material design?)
    - [x] Color
    - [x] Ads
    - [x] GDPR
    - [ ] Frequency
    - [ ] Billing
