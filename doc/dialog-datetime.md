# Dialog DateTime Module

| Date Dialog | Time Dialog |
| :---: | :---: |
| ![Dialog](../images/dialog_date.jpg?raw=true "Dialog") | ![Dialog](../images/dialog_time.jpg?raw=true "Dialog") |

This module is placed inside the `dialogs-datetime` artifact and the main definition looks like following:

https://github.com/MFlisar/MaterialDialogs/blob/ee5ee5bb827f2995b380a4290ec11350243ba5f2/dialogs/datetime/src/main/java/com/michaelflisar/dialogs/DialogDateTime.kt#L15-L36

This dialog will emit events of the the sealed class type `DialogDateTime.Event` that looks like following:

https://github.com/MFlisar/MaterialDialogs/blob/ee5ee5bb827f2995b380a4290ec11350243ba5f2/dialogs/datetime/src/main/java/com/michaelflisar/dialogs/DialogDateTime.kt#L56-L68

ATTENTION

When observing the events of a datetime dialog, you can use `DialogDateTime.Event` but you should use the concreate events instead because the `DialogDateTime.Event` is just there for simplicity.

The concrete class are made because otherwise we could not observe datetune events by types because of type erasion for generics.

Depending on the provided type of your datetime dialog you can observe following concrete events:

* `EventDateTime `
* `EventDate `
* `EventTime `
