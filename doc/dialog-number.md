# Dialog Number Module

| Number Dialog | Number Dialog - user tried to manually insert a value out of range |
| :---: | :---: |
| ![Dialog](../images/dialog_number1.jpg?raw=true "Dialog") | ![Dialog](../images/dialog_number2.jpg?raw=true "Dialog") |

This module is placed inside the `dialogs-number` artifact and the main definition looks like following:

https://github.com/MFlisar/MaterialDialogs/blob/117349fcfd30a26a6b8afdf943a06e4270639775/dialogs/number/src/main/java/com/michaelflisar/dialogs/DialogNumber.kt#L18-L36

This dialog will emit events of the sealed class type `DialogNumber.Event<T : Number>` that looks like following:

https://github.com/MFlisar/MaterialDialogs/blob/117349fcfd30a26a6b8afdf943a06e4270639775/dialogs/number/src/main/java/com/michaelflisar/dialogs/DialogNumber.kt#L107-L120

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
 DialogNumber(
    ...
     // minimal setup
	input = DialogNumber.Input.Single(value = 100)
    // or with custom limits and step sizes and formatter
    input = DialogNumber.Input.Single(value, min, max, step, formatter)
    ...
)
```

By default the numbers min/max values are used for the limits and step size is 1.

### Multiple Inputs

This dialog allows you to display multiple inputs as well like following:

```kotlin
 DialogNumber(
    ...
	input = DialogNumber.Input.Multi(
		listOf(
			DialogNumber.Input.Single(value = 10),
			DialogNumber.Input.Single(value = 20),
			DialogNumber.Input.Single(value = 30)
		)
    )
    ...
)

### Formatter

If desired, you can also provide a custom formatter to format how the number is displayed:

```kotlin
 DialogNumber(
    ...
	input = DialogNumber.Input.Single(
	    value = 100
		formatter = DialogNumber.Formatter(R.string.custom_int_formatter)
	)
    ...
)
```

Of course you can use a full custom formatter as well, by simple implementing the [INumberFormatter](../dialogs/number/src/main/java/com/michaelflisar/dialogs/interfaces/INumberFormatter.kt) yourself.

https://github.com/MFlisar/MaterialDialogs/blob/b188ed2ce4fd46d728c7dae3d5002d5f871044d3/dialogs/number/src/main/java/com/michaelflisar/dialogs/interfaces/INumberFormatter.kt#L6-L8

