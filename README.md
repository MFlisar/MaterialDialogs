# MaterialDialogs  [![Release](https://jitpack.io/v/MFlisar/materialdialogs.svg)](https://jitpack.io/#MFlisar/MaterialDialogs)

This library helps to show a `Dialog` and takes care of sending events to the parent `Activity`/`Fragment` without leaking it. It's made for the `Theme.Material3` theme and tries to follow styling that's described here:

[M3 Material Dialogs](https://m3.material.io/components/dialogs/implementation/android)

It supports following 3 styling "types" and changing between styles is as simple as defining a flag to inidcate which style to use:

* **AlertDialog**
* **DialogFragment**
* **BottomSheetDialogFragment**
* **FullscreenDialog**

All the `Fragment` modes support restoring intermdediate view states and automatic restoration of the event emission logic to the correct parent without leaking it.

# Introduction

It works as simply as following: From within an `Activity`/`Fragment` create a dialog like following:

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
  
  // ID is optional, you can also listen to all events of a special type if desired
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

### Dialog Input

```gradle
dependencies {
  implementation "com.github.MFlisar.MaterialDialogs:dialogs-input:<LATEST-VERSION>"
}
```

### Dialog List

```gradle
dependencies {
  implementation "com.github.MFlisar.MaterialDialogs:dialogs-list:<LATEST-VERSION>"
}
```

### Dialog Number

```gradle
dependencies {
  implementation "com.github.MFlisar.MaterialDialogs:dialogs-number:<LATEST-VERSION>"
}
```

### Dialog DateTime

```gradle
dependencies {
  implementation "com.github.MFlisar.MaterialDialogs:dialogs-datetime:<LATEST-VERSION>"
}
```

### Dialog Color

```gradle
dependencies {
  implementation "com.github.MFlisar.MaterialDialogs:dialogs-color:<LATEST-VERSION>"
}
```

## Extensions

### Animations

```gradle
dependencies {
  implementation "com.github.MFlisar.MaterialDialogs:extensions-animations:<LATEST-VERSION>"
}
```

### DialogFragment

```gradle
dependencies {
  implementation "com.github.MFlisar.MaterialDialogs:extensions-fragment-dialog:<LATEST-VERSION>"
}
```

### BottomSheetFragment

```gradle
dependencies {
  implementation "com.github.MFlisar.MaterialDialogs:extensions-fragment-bottomsheet:<LATEST-VERSION>"
}
```

### FullscreenFragment

```gradle
dependencies {
  implementation "com.github.MFlisar.MaterialDialogs:extensions-fragment-fullscreen:<LATEST-VERSION>"
}
```

# DEMO APP

Check the [demo app](app/src/main/java/com/michaelflisar/dialogs/MainActivity.kt) for more informations.

# TODO

- [ ] sticky footer in bottom sheet - a footer that stays visible in all "extension" state - so that buttons are always visible
- [ ] custom buttons view to support stacking in bottomsheet + fullscreen
- [ ] Swipe Dismiss Feature
- [ ] List
	- [ ] List Info: "x items"?
	- [ ] Filter Info (# items, showing X/Y items)

- [ ] General
	- [x] AlertDialog Style
	- [x] Dialog
	- [ ] BottomSheet
		
	- [x] Fullscreen
- [ ] Features
	- 
	- [ ] BottomSheet - flag to support "expand to fullscreen style" (pos button in toolbar is enabled, pos button in footer is removed, toolbar replaces the title)
- [ ] Dialogss
	- [x] Info
	- [x] Input
	- [x] List
	- [x] NumberPicker
		=> better UI?
	- [x] DateTime
		=> Custom Impl with material design?
	- [x] Color
	- [ ] Ads
	- [ ] Frequency
- [ ] Optional Features
	- [x] List - Filtering
	
	- [ ] Better default value handling? e.g. ListDialog default icon size? would need to be some extensible solution so that each dialog can register its defaults in there...
