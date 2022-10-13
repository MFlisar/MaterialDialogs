package com.michaelflisar.dialogs.classes

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import com.michaelflisar.dialogs.DialogDateTime
import java.util.*

internal class DateEditTextMask(
    val input: EditText,
    val format: DialogDateTime.DateFormat,
    val onDateChanged: (date: Date?) -> Unit
) {
    private val maxLength = format.format1.length + format.format2.length + format.format3.length + 2 * format.sep.length

    fun listen() {
        input.addTextChangedListener(mDateEntryWatcher)
    }

    private val mDateEntryWatcher = object : TextWatcher {

        var edited = false

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            if (edited) {
                edited = false
                return
            }

            var working = getEditText()

            working = manageDateDivider(working, format.format1.length, start, before)
            working = manageDateDivider(working, format.format1.length + format.format2.length + format.sep.length, start, before)

            edited = true
            input.setText(working)
            input.setSelection(input.text.length)

            var date: Date? = null
            try {
                date = format.dateFormat.parse(working)
            } catch (e: Exception) {
            }

            onDateChanged.invoke(date)
        }

        private fun manageDateDivider(
            working: String,
            position: Int,
            start: Int,
            before: Int
        ): String {
            if (working.length == position) {
                return if (before <= position && start < position)
                    working + format.sep
                else
                    working.dropLast(1)
            }
            return working
        }

        private fun getEditText(): String {
            return if (input.text.length >= maxLength)
                input.text.toString().substring(0, maxLength)
            else
                input.text.toString()
        }

        override fun afterTextChanged(s: Editable) {}
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
    }
}