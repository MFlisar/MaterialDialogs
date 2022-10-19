package com.michaelflisar.dialogs.classes

import android.content.Context
import android.content.SharedPreferences
import android.os.Parcelable
import com.michaelflisar.dialogs.DialogDebug
import com.michaelflisar.dialogs.interfaces.IMaterialDialogPresenter
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Parcelize
class DebugDataManager private constructor(
    internal val items: List<DebugItem<*>>
) : Parcelable {

    @IgnoredOnParcel
    private lateinit var cache: SharedPrefsCache

    constructor(context: Context, preferenceName: String, items: List<DebugItem<*>>) : this(items) {
        cache = SharedPrefsCache(context.applicationContext.getSharedPreferences(preferenceName, Context.MODE_PRIVATE))
    }

    fun findEntry(prefName: String, items: List<DebugItem<*>>): DebugItem<*>? {

        for (e in items) {
            val isEntry = (e is DebugItem.EntryWithPref<*>) && e.prefName.equals(prefName)
            if (isEntry) {
                return e
            }
            if (e is DebugItem.SubEntryHolder<*>) {
                val entry = findEntry(prefName, e.subEntries)
                if (entry != null) {
                    return entry
                }
            }
        }

        return null
    }

    fun resetAll() {
        reset(items)
    }

    private fun reset(
        items: List<DebugItem<*>>
    ) {
        for (e in items) {
            if (e is DebugItem.EntryWithPref<*>) {
                e.reset(this)
            }
            if (e is DebugItem.SubEntryHolder<*>) {
                reset(e.subEntries)
            }
        }
    }

    fun deleteAll() = cache.deleteAll()

    fun delete(items: List<DebugItem<*>>) {
        val keys = getAllKeys(items)
        cache.delete(keys)
    }

    fun deleteDeprecated(itemsToKeep: List<DebugItem<*>>) {
        val keys = getAllKeys(itemsToKeep)
        cache.deleteDeprecated(keys)
    }

    private fun getAllKeys(items: List<DebugItem<*>>): ArrayList<String> {
        val keys = ArrayList<String>()
        for (e in items) {
            if (e is DebugItem.EntryWithPref<*>) {
                keys.add(e.prefName)
            }
            if (e is DebugItem.SubEntryHolder<*>) {
                keys.addAll(getAllKeys(e.subEntries))
            }
        }
        return keys
    }

    // --------------
    // SharedPrefsCache + Funktionen
    // --------------

    fun getBool(entry: DebugItem.Checkbox) = cache.getBoolean(entry.prefName, entry.defaultValue)
    fun setBool(entry: DebugItem.Checkbox, enabled: Boolean) =
        cache.putBoolean(entry.prefName, enabled)

    fun getInt(entry: DebugItem.List) = cache.getInt(entry.prefName, entry.defaultValue)
    fun getInt(prefName: String, defaultValue: Int) = cache.getInt(prefName, defaultValue)
    fun setInt(entry: DebugItem.List, value: Int) = cache.putInt(entry.prefName, value)
    fun setInt(prefName: String, value: Int) = cache.putInt(prefName, value)

    internal class SharedPrefsCache(private val sharedPreferences: SharedPreferences) {

        val map = hashMapOf<String, Any>()

        internal fun getBoolean(key: String, defaultValue: Boolean): Boolean {
            val cached = map.get(key)
            if (cached != null && cached is Boolean) {
                return cached
            }
            val value = sharedPreferences.getBoolean(key, defaultValue)
            map[key] = value
            return value
        }

        internal fun putBoolean(key: String, value: Boolean) {
            map[key] = value
            sharedPreferences.edit().putBoolean(key, value).apply()
        }

        internal fun getInt(key: String, defaultValue: Int): Int {
            val cached = map.get(key)
            if (cached != null && cached is Int) {
                return cached
            }
            val value = sharedPreferences.getInt(key, defaultValue)
            map[key] = value
            return value
        }

        internal fun putInt(key: String, value: Int) {
            map[key] = value
            sharedPreferences.edit().putInt(key, value).apply()
        }

        internal fun delete(keys: List<String>) {
            for (k in keys) {
                map.remove(k)
            }
            val editor = sharedPreferences.edit()
            for (k in keys) {
                editor.remove(k)
            }
            editor.apply()
        }

        internal fun deleteDeprecated(keysToKeep: List<String>) {
            val keysToDelete = ArrayList<String>()
            val allKeys = sharedPreferences.all.keys
            for (k in allKeys) {
                if (!keysToKeep.contains(k))
                    keysToDelete.add(k)
            }
            val editor = sharedPreferences.edit()
            for (k in keysToDelete) {
                editor.remove(k)
            }
            editor.apply()
        }

        internal fun deleteAll() {
            map.clear()
            sharedPreferences.edit().clear().apply()
        }
    }
}