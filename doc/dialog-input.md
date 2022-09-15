# Dialog Input Module

| Input Dialog  |
| :---: |
| ![Dialog](../images/dialog_input.jpg?raw=true "Dialog") |

This module is placed inside the `dialogs-input` artifact and the main definition looks like following:

https://github.com/MFlisar/MaterialDialogs/blob/fcd9f94675bef09c60d818c2674ece8b852ccf9f/dialogs/input/src/main/java/com/michaelflisar/dialogs/DialogInput.kt#L18-L38

This dialog will emit events of the sealed class type `DialogInput.Event` that looks like following:

https://github.com/MFlisar/MaterialDialogs/blob/fcd9f94675bef09c60d818c2674ece8b852ccf9f/dialogs/input/src/main/java/com/michaelflisar/dialogs/DialogInput.kt#L57-L65

# Tipps

### Input Type

This dialog allows to get any insert from the user. You can limit the input type by providing a custom `android.text.InputType` flag. E.g. like following:

```kotlin
 DialogInput(
    ...
    inputType = InputType.TYPE_CLASS_NUMBER // only allow numerical input
    ...
)
```

### Input Validator

You can find the interface [here](../dialogs/input/src/main/java/com/michaelflisar/dialogs/interfaces/IInputValidator.kt). If desired you can implement this interface in your custom class and provide whatever logic you want.

A simple default implementation is already added and you can create instances of it like following:

```kotlin
 DialogInput(
    ...
    validator = DialogInput.createSimpleValidator(minLength = 1, maxLength = 10) // force the length to be in the range [1, 10], both lengths are nullable to disable an enforcement on each side if desired
    ...
)
```
