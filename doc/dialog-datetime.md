# Dialog DateTime Module

| Date Dialog | Time Dialog |
| :---: | :---: |
| ![Dialog](../images/dialog_date.jpg?raw=true "Dialog") | ![Dialog](../images/dialog_time.jpg?raw=true "Dialog") |

This module is placed inside the `dialogs-datetime` artifact and the main definition looks like following:

https://github.com/MFlisar/MaterialDialogs/blob/117349fcfd30a26a6b8afdf943a06e4270639775/dialogs/datetime/src/main/java/com/michaelflisar/dialogs/DialogDateTime.kt#L16-L39

This dialog will emit events of the the sealed class type `DialogDateTime.Event` that looks like following:

https://github.com/MFlisar/MaterialDialogs/blob/117349fcfd30a26a6b8afdf943a06e4270639775/dialogs/datetime/src/main/java/com/michaelflisar/dialogs/DialogDateTime.kt#L67-L81

# ATTENTION

When observing the events of a datetime dialog, you can use `DialogDateTime.Event` but you should use the concreate events instead because the `DialogDateTime.Event` is just there for simplicity.

The concrete class are made because otherwise we could not observe datetime events by type because of type erasion for generics.

Depending on the provided type of your datetime dialog you can observe following concrete events:

* `EventDateTime`
* `EventDate`
* `EventTime`
