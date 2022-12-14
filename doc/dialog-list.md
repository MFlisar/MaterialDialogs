# Dialog List Module
| Single Selection | Multi Selection | Multi Click | Custom Data + Filter |
| :---: | :---: | :---: | :---: |
| ![Dialog](../images/dialog_list_singleselect.jpg?raw=true "Dialog") | ![Dialog](../images/dialog_list_multiselect.jpg?raw=true "Dialog") | ![Dialog](../images/dialog_list_multiclick.jpg?raw=true "Dialog") | ![Dialog](../images/dialog_list_custom.jpg?raw=true "Dialog") |

This module is placed inside the `dialogs-list` artifact and the main definition looks like following:

https://github.com/MFlisar/MaterialDialogs/blob/117349fcfd30a26a6b8afdf943a06e4270639775/dialogs/list/src/main/java/com/michaelflisar/dialogs/DialogList.kt#L26-L49

This dialog will emit events of the sealed class type `DialogList.Event` that looks like following:

https://github.com/MFlisar/MaterialDialogs/blob/117349fcfd30a26a6b8afdf943a06e4270639775/dialogs/list/src/main/java/com/michaelflisar/dialogs/DialogList.kt#L78-L98

# Tipps

### Items - Synchronous

A small an preknown list of elements can be shown like following:

```kotlin
 DialogInput(
    ...
    // simple string list provider, item it is index
    itemsProvider = DialogList.createList(List(50) { "Item ${it + 1}" }.map { it.asText() })
    // simple string resource list provider, item it is index
    itemsProvider = DialogList.createList(listOf(...<resource ids>...).map { it.asText() })
    // simple Text list provider, item it is index
    itemsProvider = DialogList.createList(listOf(...<text objects>...))
    // simply IListItem list provider, item it is index
   itemsProvider = DialogList.Items.List(listOf(...<IListItem objects>...))
    ...
)
```

### Items - Asynchronous

A large or dynamic list of elements can be shown like following:

* create an instance of [IListItemsLoader](../dialogs/list/src/main/java/com/michaelflisar/dialogs/interfaces/IListItemsLoader.kt)
* load your data on whatever thread you want, e.g. on `Dispatchers.IO`, but that's up to you
* return a list of items that implement [IListItem](../dialogs/list/src/main/java/com/michaelflisar/dialogs/interfaces/IListItem.kt)

Interfaces looks like following:

https://github.com/MFlisar/MaterialDialogs/blob/be16f347152082a2a8d50e74f7497b448699f34f/dialogs/list/src/main/java/com/michaelflisar/dialogs/interfaces/IListItemsLoader.kt#L6-L8

https://github.com/MFlisar/MaterialDialogs/blob/be16f347152082a2a8d50e74f7497b448699f34f/dialogs/list/src/main/java/com/michaelflisar/dialogs/interfaces/IListItem.kt#L7-L17

**Example**

To see a full example check out the demo app and following 2 classes:

* Item that implements `IListItem`: [AppListItem](../app/src/main/java/com/michaelflisar/dialogs/apps/AppListItem.kt)
* Item loader that implements `IListItemLoader`: [AppsManager](../app/src/main/java/com/michaelflisar/dialogs/apps/AppsManager.kt)

```kotlin
 DialogInput(
    ...
    // custom item provider - custom implementation insde AppsManager
    itemsProvider = ItemProvider.Items.Loader(AppsManager)
    ...
)
```
