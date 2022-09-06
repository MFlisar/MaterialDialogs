package com.michaelflisar.dialogs.classes

import android.content.Context
import android.widget.ArrayAdapter
import android.widget.Filter

class NoFilterArrayAdapter<T>(
    context: Context,
    textViewResourceId: Int,
    objects: List<T>
) : ArrayAdapter<T>(context, textViewResourceId, objects) {

    private val filter: Filter = NoFilter()
    var items: List<T>

    init {
        items = objects
    }

    override fun getFilter(): Filter {
        return filter
    }

    private inner class NoFilter : Filter() {
        override fun performFiltering(arg0: CharSequence?): FilterResults {
            val result = FilterResults()
            result.values = items
            result.count = items.size
            return result
        }

        override fun publishResults(arg0: CharSequence?, arg1: FilterResults?) {
            notifyDataSetChanged()
        }
    }


}