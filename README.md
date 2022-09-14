# MaterialDialogs  [![Release](https://jitpack.io/v/MFlisar/materialdialogs.svg)](https://jitpack.io/#MFlisar/MaterialDialogs)

This library helps to show a `Dialog` and takes care of sending events to the parent `Activity`/`Fragment` without leaking it even if the dialogs are shown as `DialogFragments`. It's made for the `Theme.Material3` theme and tries to follow styling that's described here:

[M3 Material Dialogs](https://m3.material.io/components/dialogs/implementation/android)

It supports following styling "types" and changing between styles is as simple as calling a different show function:

* **AlertDialog**
* **DialogFragment**
* **BottomSheetDialogFragment**
* **FullscreenDialog**

All the `Fragment` modes support restoring intermdediate view states and automatic restoration of the event emission logic to the correct parent without leaking it.

Additionally, the dialogs support `View` based animations as well like e.g. `CircularReveal`.

# Introduction

It works as simple as following: From within an `Activity`/`Fragment` create a dialog like following:

```kotlin
DialogInfo(
  id = 1,
  title = "Info Title".asText(), // Int, String and any CharSequence are supported (e.g. SpannableString)
  text = "Some info text...".asText()
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

# Modules

* [Core](#core)
* Dialogs
  * [Info](#dialog-info)
  * [Input](#dialog-input)
  * [List](#dialog-list)
  * [Number](#dialog-number)
  * [DateTime](#dialog-datetime)
  * [Color](#dialog-color)
* Extensions
  * [Animations](#animations)
  * [DialogFragment](#dialogfragment)
  * [BottomSheetFragment](#bottomsheetfragment)
  * [FullscreenFragment](#fullscreenfragment)


## Core

```gradle
dependencies {
  implementation "com.github.MFlisar.MaterialDialogs:Core:<LATEST-VERSION>"
}
```

## Dialogs

### Dialog Info

```gradle
dependencies {
  implementation "com.github.MFlisar.MaterialDialogs:dialogs-info:<LATEST-VERSION>"
}
```

| Simple info dialog  | Info dialog with spannable text and icon | Info dialog with long content |
| :---: | :---: | :---: |
| ![Dialog](images/dialog_info1.jpg?raw=true "Dialog") | ![Dialog](images/dialog_info2.jpg?raw=true "Dialog") | ![Dialog](images/dialog_info3.jpg?raw=true "Dialog") |

### Dialog Input

```gradle
dependencies {
  implementation "com.github.MFlisar.MaterialDialogs:dialogs-input:<LATEST-VERSION>"
}
```

| Input dialog  |
| :---: |
| ![Dialog](images/dialog_input.jpg?raw=true "Dialog") |

### Dialog List

```gradle
dependencies {
  implementation "com.github.MFlisar.MaterialDialogs:dialogs-list:<LATEST-VERSION>"
}
```

| Single select | Multi selection | Multi click | Custom Data + Filter |
| :---: | :---: | :---: | :---: |
| ![Dialog](images/dialog_list_singleselect.jpg?raw=true "Dialog") | ![Dialog](images/dialog_list_multiselect.jpg?raw=true "Dialog") | ![Dialog](images/dialog_list_multiclick.jpg?raw=true "Dialog") | ![Dialog](images/dialog_list_custom.jpg?raw=true "Dialog") |

### Dialog Number

```gradle
dependencies {
  implementation "com.github.MFlisar.MaterialDialogs:dialogs-number:<LATEST-VERSION>"
}
```

| Buttons and no error | User tried to manually insert a value out of range |
| :---: | :---: |
| ![Dialog](images/dialog_number1.jpg?raw=true "Dialog") | ![Dialog](images/dialog_number2.jpg?raw=true "Dialog") |

### Dialog DateTime

```gradle
dependencies {
  implementation "com.github.MFlisar.MaterialDialogs:dialogs-datetime:<LATEST-VERSION>"
}
```

| Date dialog | Time dialog |
| :---: | :---: |
| ![Dialog](images/dialog_date.jpg?raw=true "Dialog") | ![Dialog](images/dialog_time.jpg?raw=true "Dialog") |

### Dialog Color

```gradle
dependencies {
  implementation "com.github.MFlisar.MaterialDialogs:dialogs-color:<LATEST-VERSION>"
}
```

| Color dialog - Presets page | Color dialog - custom value |
| :---: | :---: |
| ![Dialog](images/dialog_color1.jpg?raw=true "Dialog") | ![Dialog](images/dialog_color2.jpg?raw=true "Dialog") |

## Extensions

### Animations

```gradle
dependencies {
  implementation "com.github.MFlisar.MaterialDialogs:extensions-animations:<LATEST-VERSION>"
}
```

This extension provides a Fade and Scale Animation and a CircularReveal animation implementation.

### DialogFragment

```gradle
dependencies {
  implementation "com.github.MFlisar.MaterialDialogs:extensions-fragment-dialog:<LATEST-VERSION>"
}
```

This allows you to use the dialogs as fragments - they will save/restore their state and intermediate state automatically.

### BottomSheetFragment

```gradle
dependencies {
  implementation "com.github.MFlisar.MaterialDialogs:extensions-fragment-bottomsheet:<LATEST-VERSION>"
}
```

| Bottomsheet Style |
| :---: |
| ![Dialog](images/style_bottomsheet.jpg?raw=true "Dialog") |

### FullscreenFragment

```gradle
dependencies {
  implementation "com.github.MFlisar.MaterialDialogs:extensions-fragment-fullscreen:<LATEST-VERSION>"
}
```

| Fullscreen Style | Fullscreen Style |
| :---: | :---: |
| ![Dialog](images/style_fullscreen1.jpg?raw=true "Dialog") | ![Dialog](images/style_fullscreen2.jpg?raw=true "Dialog") |

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
	- [x] DateTime
		=> Custom Impl with material design?
	- [x] Color
	- [ ] Ads
	- [ ] Frequency