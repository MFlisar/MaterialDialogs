package com.michaelflisar.dialogs.classes

sealed class MaterialDialogAction {
    object Cancelled: MaterialDialogAction() {
        override fun toString() = "MaterialDialogAction::Cancelled"
    }
    class Menu(val menuId: Int): MaterialDialogAction() {
        override fun toString() = "MaterialDialogAction::Menu {menuId=$menuId}"
    }
}