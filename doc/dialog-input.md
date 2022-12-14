# Dialog Input Module

| Input Dialog  |
| :---: |
| ![Dialog](../images/dialog_input.jpg?raw=true "Dialog") |

This module is placed inside the `dialogs-input` artifact and the main definition looks like following:

https://github.com/MFlisar/MaterialDialogs/blob/117349fcfd30a26a6b8afdf943a06e4270639775/dialogs/input/src/main/java/com/michaelflisar/dialogs/DialogInput.kt#L20-L39

This dialog will emit events of the sealed class type `DialogInput.Event` that looks like following:

https://github.com/MFlisar/MaterialDialogs/blob/117349fcfd30a26a6b8afdf943a06e4270639775/dialogs/input/src/main/java/com/michaelflisar/dialogs/DialogInput.kt#L53-L71

# Tipps

### Input Type

This dialog allows to get any insert from the user. You can limit the input type by providing a custom `android.text.InputType` flag. E.g. like following:

```kotlin
 DialogInput(
    ...
    input = DialogInput.Input.Single(
        inputType = InputType.TYPE_CLASS_NUMBER // only allow numerical input
    )
    ...
)
```

### Multiple Inputs

This dialog allows you to display multiple inputs as well like following:

```kotlin
 DialogInput(
    ...
    input = DialogInput.Input.Single(
        input = DialogInput.Input.Multi(
            listOf(
                DialogInput.Input.Single(hint = "Value 1".asText()),
                DialogInput.Input.Single(hint = "Value 2".asText()),
                DialogInput.Input.Single(hint = "Value 3".asText()),
            )
        )
    )
    ...
)
```

### Input Validator

You can find the interface [here](../dialogs/input/src/main/java/com/michaelflisar/dialogs/interfaces/IInputValidator.kt). If desired you can implement this interface in your custom class and provide whatever logic you want.

A simple default implementation is already added and you can create instances of it like following:

```kotlin
 DialogInput(
    ...
    input = DialogInput.Input.Single(
        validator = DialogInput.TextValidator(minLength = 1, maxLength = 10) // force the length to be in the range [1, 10], both lengths are nullable to disable an enforcement on each side if desired
    )
    ...
)
```
