package com.michaelflisar.dialogs.classes

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.michaelflisar.dialogs.app.R


class LongToast (context: Context) : Toast(context) {

    companion object {
        fun makeText(context: Context, text: CharSequence?, duration: Int): Toast {
            val t = Toast.makeText(context, text, duration)
            val inflater =
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val layout: View = inflater.inflate(R.layout.long_toast, null)
            val textView = layout.findViewById(R.id.text) as TextView
            textView.text = text
            t.view = layout
            return t
        }
    }
}