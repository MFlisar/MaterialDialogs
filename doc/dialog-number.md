# Dialog Number Module

| Number Dialog | Number Dialog - user tried to manually insert a value out of range |
| :---: | :---: |
| ![Dialog](../images/dialog_number1.jpg?raw=true "Dialog") | ![Dialog](../images/dialog_number2.jpg?raw=true "Dialog") |

This module is placed inside the `dialogs-number` artifact and the main definition looks like following:

https://github.com/MFlisar/MaterialDialogs/blob/b188ed2ce4fd46d728c7dae3d5002d5f871044d3/dialogs/number/src/main/java/com/michaelflisar/dialogs/DialogNumber.kt#L16-L35

This dialog will emit events of the sealed class type `DialogNumber.Event<T : Number>` that looks like following:

https://github.com/MFlisar/MaterialDialogs/blob/b188ed2ce4fd46d728c7dae3d5002d5f871044d3/dialogs/number/src/main/java/com/michaelflisar/dialogs/DialogNumber.kt#L79-L91

# ATTENTION

When observing the events of a number dialog, you can use `DialogNumber.Event` but you should use the concreate events instead because the `DialogNumber.Event` is just there for simplicity.

The concrete class are made because otherwise we could not observe number events by types because of type erasion for generics.

Depending on the provided `value` of your number dialog and the derived type you can observe following concrete events:

* `EventInt`
* `EventLong`
* `EventFloat`
* `EventDouble`

# Tipps

### Setup

You can provide a custom setup to limit the min/max values and to define step sizes for the buttons like following:

```kotlin
 DialogInput(
    ...
     // default setup
    setup = DialogNumber.createDefaultSetup(value)
    // custom setup
    setup = NumberSetup<T>(min, max, step, formatter)
    ...
)
```

By default the numbers min/max values are used for the limits and step size is 1.

**Formatter**

If desired, you can also provide a custom formatter to format how the number is displayed:

```kotlin
 DialogInput(
    ...
    setup = NumberSetup(min, max, step, DefaultFormatter>(R.string.custom_int_formatter))
    ...
)
```

Of course you can use a full custom formatter as well, by simple implementing the [INumberFormatter](../dialogs/number/src/main/java/com/michaelflisar/dialogs/interfaces/INumberFormatter.kt) yourself.

https://github.com/MFlisar/MaterialDialogs/blob/b188ed2ce4fd46d728c7dae3d5002d5f871044d3/dialogs/number/src/main/java/com/michaelflisar/dialogs/interfaces/INumberFormatter.kt#L6-L8

