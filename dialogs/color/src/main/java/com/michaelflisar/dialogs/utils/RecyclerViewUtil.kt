package com.michaelflisar.dialogs.utils

import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

internal object RecyclerViewUtil {
    fun findFirstVisibleItemPosition(rv: RecyclerView): Int? {
        if (rv.layoutManager is GridLayoutManager) {
            return (rv.layoutManager as GridLayoutManager).findFirstVisibleItemPosition()
        } else if (rv.layoutManager is LinearLayoutManager) {
            return (rv.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
        }
        return null
    }

    fun findLastVisibleItemPosition(rv: RecyclerView): Int? {
        if (rv.layoutManager is GridLayoutManager) {
            return (rv.layoutManager as GridLayoutManager).findLastVisibleItemPosition()
        }
        return if (rv.layoutManager is LinearLayoutManager) {
            (rv.layoutManager as LinearLayoutManager).findLastVisibleItemPosition()
        } else null
    }

    fun findFirstCompletelyVisibleItemPosition(rv: RecyclerView): Int? {
        if (rv.layoutManager is GridLayoutManager) {
            return (rv.layoutManager as GridLayoutManager).findFirstCompletelyVisibleItemPosition()
        } else if (rv.layoutManager is LinearLayoutManager) {
            return (rv.layoutManager as LinearLayoutManager).findFirstCompletelyVisibleItemPosition()
        }
        return null
    }

    fun isViewVisible(rv: RecyclerView, index: Int): Boolean {
        val firstVisible = findFirstVisibleItemPosition(rv)!!
        val lastVisible = findLastVisibleItemPosition(rv)!!
        return index in firstVisible..lastVisible
    }
}