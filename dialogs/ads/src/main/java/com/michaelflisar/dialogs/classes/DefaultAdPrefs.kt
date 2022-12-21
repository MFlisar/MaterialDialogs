package com.michaelflisar.dialogs.classes

import android.content.Context
import android.content.SharedPreferences
import com.michaelflisar.dialogs.interfaces.IAdsPrefs
import com.michaelflisar.text.Text
import com.michaelflisar.text.asText
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Parcelize
class DefaultAdPrefs(
    private val prefName: Text = "prefs_ads".asText()
) : IAdsPrefs {

    @IgnoredOnParcel
    private val KEY_LAST_SHOW_POLICY_DATE = "date"

    @IgnoredOnParcel
    private val KEY_LAST_SHOW_POLICY_COUNTER = "counter"

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(prefName.getString(context), Context.MODE_PRIVATE)
    }

    override fun getLastShowPolicyDate(context: Context): Long {
        return getPrefs(context).getLong(KEY_LAST_SHOW_POLICY_DATE, 0)
    }

    override fun saveLastShowPolicyDate(context: Context, date: Long): Boolean {
        return getPrefs(context).edit().putLong(KEY_LAST_SHOW_POLICY_DATE, date).commit()
    }

    override fun getLastPolicyCounter(context: Context): Int {
        return getPrefs(context).getInt(KEY_LAST_SHOW_POLICY_COUNTER, 0)
    }

    override fun setLastPolicyCounter(context: Context, counter: Int): Boolean {
        return getPrefs(context).edit().putInt(KEY_LAST_SHOW_POLICY_COUNTER, counter).commit()
    }
}