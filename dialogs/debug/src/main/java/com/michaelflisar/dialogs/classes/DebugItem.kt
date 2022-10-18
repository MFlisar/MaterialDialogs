package com.michaelflisar.dialogs.classes

import android.os.Parcelable
import com.michaelflisar.dialogs.DialogDebug
import com.michaelflisar.dialogs.interfaces.IMaterialDialogPresenter
import kotlinx.parcelize.Parcelize

sealed class DebugItem<T> : Parcelable {

    abstract val name: String
    private var visibleInRelease = true

    @Parcelize
    class Group(
        override val name: String,
        override var subEntries: ArrayList<DebugItem<*>> = arrayListOf()
    ) : DebugItem<Unit>(), SubEntryHolder<DebugItem<*>, Group>

    @Parcelize
    class Button(
        override val name: String,
        val function: (presenter: IMaterialDialogPresenter<*, *, *>) -> Unit
    ) : DebugItem<Unit>() {
        override fun onClick(
            manager: DebugDataManager,
            presenter: IMaterialDialogPresenter<*, *, *>,
            setup: DialogDebug
        ): Array<ClickResult> {
            function(presenter)
            return emptyArray()
        }
    }

    @Parcelize
    class Reset(
        override val name: String
    ) : DebugItem<Unit>() {
        override fun onClick(
            manager: DebugDataManager,
            presenter: IMaterialDialogPresenter<*, *, *>,
            setup: DialogDebug
        ): Array<ClickResult> {
            manager.reset(presenter, setup)
            DialogDebug.Event.NotifyDataSetChanged(setup.id, setup.extra).send(presenter)
            return emptyArray()
        }
    }

    @Parcelize
    class Checkbox(
        override val name: String,
        override val prefName: String,
        override val defaultValue: Boolean,
        val sideEffect: ((value: Boolean) -> Unit)? = null
    ) : DebugItem<Boolean>(), EntryWithPref<Boolean> {
        override fun reset(manager: DebugDataManager) {
            manager.setBool(this, defaultValue)
        }

        override fun onClick(
            manager: DebugDataManager,
            dialog: IMaterialDialogPresenter<*, *, *>,
            setup: DialogDebug
        ): Array<ClickResult> {
            val newValue = !manager.getBool(this)
            manager.setBool(this, newValue)
            sideEffect?.invoke(newValue)
            return arrayOf(ClickResult.Notify)
        }
    }

    @Parcelize
    class List(
        override val name: String,
        override val prefName: String,
        override val defaultValue: Int,
        override var subEntries: ArrayList<ListEntry> = arrayListOf(),
        val sideEffect: ((value: Int) -> Unit)? = null
    ) : DebugItem<Int>(), EntryWithPref<Int>, SubEntryHolder<ListEntry, List> {
        override fun reset(manager: DebugDataManager) {
            manager.setInt(this, defaultValue)
        }

        fun getEntryByValue(value: Int) = subEntries.find { it.value == value }!!

        fun addEntries(vararg items: ListEntry) {
            subEntries.addAll(items)
        }
    }

    @Parcelize
    class ListEntry(override val name: String, val parent: List, val value: Int) :
        DebugItem<Int>() {
        override fun onClick(
            manager: DebugDataManager,
            dialog: IMaterialDialogPresenter<*, *, *>,
            setup: DialogDebug
        ): Array<ClickResult> {
            manager.setInt(parent, value)
            parent.sideEffect?.invoke(value)
            return arrayOf(ClickResult.GoUp)
        }
    }

    open fun onClick(
        manager: DebugDataManager,
        dialog: IMaterialDialogPresenter<*, *, *>,
        setup: DialogDebug
    ): Array<ClickResult> = emptyArray()

    fun withVisibleInRelease(visible: Boolean): DebugItem<T> {
        this.visibleInRelease = visible
        return this
    }

    // --------------------
    // Enums and Interfaces
    // --------------------

    enum class ClickResult {
        Notify,
        GoUp
    }


    interface SubEntryHolder<S : DebugItem<*>, Parent : DebugItem<*>> {
        var subEntries: ArrayList<S>

        fun subEntries(block: (Parent) -> ArrayList<S>): Parent {
            @Suppress("UNCHECKED_CAST")
            subEntries = block(this as Parent)
            return this
        }
    }

    interface EntryWithPref<T> {
        val prefName: String
        val defaultValue: T

        fun reset(manager: DebugDataManager)
    }
}