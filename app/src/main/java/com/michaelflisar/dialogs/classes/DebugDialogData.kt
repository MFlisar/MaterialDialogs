package com.michaelflisar.dialogs.classes

import android.content.Context
import android.widget.Toast

object DebugDialogData {

    private val PREFERENCE_FILE_NAME = "debug_settings"

    val BUTTON1 = DebugItem.Button("Debug button") { presenter ->
        Toast.makeText(
            presenter.requireContext(),
            "Debug button pressed",
            Toast.LENGTH_SHORT
        ).show()
    }
    val CHECKBOX_DEBUG_MODE = DebugItem.Checkbox("Enable debug mode", "debug_bool_1", false)
    val LIST_COLOR = DebugItem.List("Debug color", "debug_list_1", 0)
        .apply {
            addEntries(
                DebugItem.ListEntry("red", this, 0),
                DebugItem.ListEntry("blue", this, 1),
                DebugItem.ListEntry("green", this, 2)
            )
        }
    private val ITEMS = listOf(
        BUTTON1,
        CHECKBOX_DEBUG_MODE,
        LIST_COLOR,
        DebugItem.Reset("Reset all debug settings")
    )

    private var MANAGER: DebugDataManager? = null

    fun initDebugManager(context: Context) {
        if (MANAGER == null) {
            MANAGER = DebugDataManager(context, PREFERENCE_FILE_NAME, ITEMS)
        }
    }

    fun getDebugDataManager(): DebugDataManager {
        return MANAGER!!
    }


}