# Dialog List Module
| Single Selection | Multi Selection | Multi Click | Custom Data + Filter |
| :---: | :---: | :---: | :---: |
| ![Dialog](images/dialog_list_singleselect.jpg?raw=true "Dialog") | ![Dialog](images/dialog_list_multiselect.jpg?raw=true "Dialog") | ![Dialog](images/dialog_list_multiclick.jpg?raw=true "Dialog") | ![Dialog](images/dialog_list_custom.jpg?raw=true "Dialog") |

This module is placed inside the `dialogs-color` artifact and the main definition looks like following:
https://github.com/MFlisar/MaterialDialogs/blob/c36e09de3dadc6b5982ca6751997254f1365876f/dialogs/list/src/main/java/com/michaelflisar/dialogs/DialogList.kt#L16-L38

This dialog will emit events of the sealed class type `DialogList.Event` that looks like following:

https://github.com/MFlisar/MaterialDialogs/blob/c36e09de3dadc6b5982ca6751997254f1365876f/dialogs/list/src/main/java/com/michaelflisar/dialogs/DialogList.kt#L98-L112
