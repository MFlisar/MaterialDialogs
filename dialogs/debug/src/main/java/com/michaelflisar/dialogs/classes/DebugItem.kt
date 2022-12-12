package com.michaelflisar.dialogs.classes

import android.os.Parcelable
import com.michaelflisar.dialogs.DialogDebug
import com.michaelflisar.dialogs.interfaces.IMaterialDialogPresenter
import kotlinx.parcelize.Parcelize

sealed class DebugItem<T> : Parcelable {

    abstract val name: String
    var visibleInRelease: Boolean = true

    @Parcelize
    class Group(
        override val name: String,
        override var subEntries: kotlin.collections.List<DebugItem<*>> = emptyList()
    ) : DebugItem<Unit>(), SubEntryHolder<DebugItem<*>>

    @Parcelize
    class Button(
        override val name: String
    ) : DebugItem<Unit>() {
        override fun onClick(
            manager: DebugDataManager,
            presenter: IMaterialDialogPresenter<*>,
            setup: DialogDebug
        ): Array<ClickResult> {
            return emptyArray()
        }
    }

    @Parcelize
    class Reset(
        override val name: String
    ) : DebugItem<Unit>() {
        override fun onClick(
            manager: DebugDataManager,
            presenter: IMaterialDialogPresenter<*>,
            setup: DialogDebug
        ): Array<ClickResult> {
            manager.resetAll()
            DialogDebug.Event.NotifyDataSetChanged(setup.id, setup.extra).send(presenter)
            return emptyArray()
        }
    }

    @Parcelize
    class Checkbox(
        override val name: String,
        override val prefName: String,
        override val defaultValue: Boolean
    ) : DebugItem<Boolean>(), EntryWithPref<Boolean> {

        override fun reset(manager: DebugDataManager) {
            manager.setBool(this, defaultValue)
        }

        override fun onClick(
            manager: DebugDataManager,
            presenter: IMaterialDialogPresenter<*>,
            setup: DialogDebug
        ): Array<ClickResult> {
            val newValue = !manager.getBool(this)
            manager.setBool(this, newValue)
            return arrayOf(ClickResult.Notify)
        }

        fun getValue(manager: DebugDataManager) = manager.getBool(this)
    }

    @Parcelize
    class List(
        override val name: String,
        override val prefName: String,
        override val defaultValue: Int,
        override var subEntries: kotlin.collections.List<ListEntry> = emptyList()
    ) : DebugItem<Int>(), EntryWithPref<Int>, SubEntryHolder<ListEntry> {

        override fun reset(manager: DebugDataManager) {
            manager.setInt(this, defaultValue)
        }

        fun getEntryByValue(value: Int) = subEntries.find { it.value == value }!!

        fun getValue(manager: DebugDataManager) = getEntryByValue(manager.getInt(this))
    }

    @Parcelize
    class ListEntry private constructor(
        override val name: String,
        val parentPrefName: String,
        val parentDefaultValue: Int,
        val value: Int
    ) : DebugItem<Int>() {

        constructor(parent: List, name: String, value: Int) : this(name, parent.prefName, parent.defaultValue, value)
        override fun onClick(
            manager: DebugDataManager,
            presenter: IMaterialDialogPresenter<*>,
            setup: DialogDebug
        ): Array<ClickResult> {
            manager.setInt(parentPrefName, value)
            return arrayOf(ClickResult.GoUp)
        }
    }

    open fun onClick(
        manager: DebugDataManager,
        presenter: IMaterialDialogPresenter<*>,
        setup: DialogDebug
    ): Array<ClickResult> = emptyArray()

    // --------------------
    // Enums and Interfaces
    // --------------------

    enum class ClickResult {
        Notify,
        GoUp
    }

    interface SubEntryHolder<S : DebugItem<*>> {
        var subEntries: kotlin.collections.List<S>
    }

    interface EntryWithPref<T> {
        val prefName: String
        val defaultValue: T
        fun reset(manager: DebugDataManager)
    }
}