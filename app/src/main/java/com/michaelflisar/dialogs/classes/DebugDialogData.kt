package com.michaelflisar.dialogs.classes

import android.content.Context
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import com.michaelflisar.dialogs.DialogDebug

object DebugDialogData {

    private val PREFERENCE_FILE_NAME = "debug_settings"

    fun onDebugEvent(activity: FragmentActivity, event: DialogDebug.Event) {
        if (event is DialogDebug.Event.Result) {
            if (event.item == BUTTON1) {
                Toast.makeText(
                    activity,
                    "Debug button1 pressed",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    val BUTTON1 = DebugItem.Button("Debug button")
    val CHECKBOX_DEBUG_MODE = DebugItem.Checkbox("Enable debug mode", "debug_bool_1", false)
    val LIST_COLOR = DebugItem.List(
        "Debug color", "debug_list_1", 0
    ).apply {
        subEntries = listOf(
            DebugItem.ListEntry(this, "red", 0),
            DebugItem.ListEntry(this, "blue", 1),
            DebugItem.ListEntry(this, "green", 2)
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