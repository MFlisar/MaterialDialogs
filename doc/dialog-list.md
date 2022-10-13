# Dialog List Module
| Single Selection | Multi Selection | Multi Click | Custom Data + Filter |
| :---: | :---: | :---: | :---: |
| ![Dialog](../images/dialog_list_singleselect.jpg?raw=true "Dialog") | ![Dialog](../images/dialog_list_multiselect.jpg?raw=true "Dialog") | ![Dialog](../images/dialog_list_multiclick.jpg?raw=true "Dialog") | ![Dialog](../images/dialog_list_custom.jpg?raw=true "Dialog") |

This module is placed inside the `dialogs-list` artifact and the main definition looks like following:
https://github.com/MFlisar/MaterialDialogs/blob/c36e09de3dadc6b5982ca6751997254f1365876f/dialogs/list/src/main/java/com/michaelflisar/dialogs/DialogList.kt#L16-L38

This dialog will emit events of the sealed class type `DialogList.Event` that looks like following:

https://github.com/MFlisar/MaterialDialogs/blob/c36e09de3dadc6b5982ca6751997254f1365876f/dialogs/list/src/main/java/com/michaelflisar/dialogs/DialogList.kt#L98-L112

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

https://github.com/MFlisar/MaterialDialogs/blob/fbc51fb4ef940e784bedfb198beb63e43704c53a/dialogs/list/src/main/java/com/michaelflisar/dialogs/interfaces/IListItemsLoader.kt#L7-L9

https://github.com/MFlisar/MaterialDialogs/blob/fbc51fb4ef940e784bedfb198beb63e43704c53a/dialogs/list/src/main/java/com/michaelflisar/dialogs/interfaces/IListItem.kt#L7-L13

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
