package com.michaelflisar.dialogs.classes

import android.content.Context
import android.content.SharedPreferences
import com.michaelflisar.dialogs.interfaces.IGDPRPrefs
import com.michaelflisar.text.Text
import com.michaelflisar.text.asText
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Parcelize
class DefaultGDPRPrefs(
    private val prefName: Text = "gdpr".asText()
) : IGDPRPrefs {

    private var CACHE: GDPRConsentState? = null

    @IgnoredOnParcel
    private val KEY_CONSENT = "gdpr_consent" // gdpr_preference

    @IgnoredOnParcel
    private val KEY_LOCATION =
        "gdpr_preference_is_in_eea_or_unknown" // gdpr_preference_is_in_eea_or_unknown

    @IgnoredOnParcel
    private val KEY_DATE = "gdpr_preference_date" // gdpr_preference_date

    @IgnoredOnParcel
    private val KEY_APP_VERSION = "gdpr_preference_app_version" // gdpr_preference_app_version

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(prefName.getString(context), Context.MODE_PRIVATE)
    }

    override fun getState(context: Context): GDPRConsentState {
        return CACHE ?: run {
            val consent = getConsent(context)
            val location = getLocation(context)
            val date = getDate(context)
            val version = getVersion(context)
            val state = GDPRConsentState(consent, location, date, version)
            CACHE = state
            state
        }
    }

    override fun saveState(context: Context, state: GDPRConsentState): Boolean {
        val success = saveConsent(context, state.consent) &&
                saveLocation(context, state.location) &&
                saveDate(context, state.date) &&
                saveVersion(context, state.version)
        if (success) {
            CACHE = state
        }
        return success
    }

    // ----------------
    // helper functions
    // ----------------

    private fun getConsent(context: Context): GDPRConsent {
        return GDPRConsent.values()[getPrefs(context).getInt(KEY_CONSENT, 0)]
    }

    private fun saveConsent(context: Context, consent: GDPRConsent): Boolean {
        return getPrefs(context).edit().putInt(KEY_CONSENT, consent.ordinal).commit()
    }

    private fun getLocation(context: Context): GDPRLocation {
        return GDPRLocation.values()[getPrefs(context).getInt(KEY_LOCATION, 0)]
    }

    private fun saveLocation(context: Context, location: GDPRLocation): Boolean {
        return getPrefs(context).edit().putInt(KEY_LOCATION, location.ordinal).commit()
    }

    private fun getDate(context: Context): Long {
        return getPrefs(context).getLong(KEY_DATE, 0L)
    }

    private fun saveDate(context: Context, date: Long): Boolean {
        return getPrefs(context).edit().putLong(KEY_DATE, date).commit()
    }

    private fun getVersion(context: Context): Int {
        return getPrefs(context).getInt(KEY_APP_VERSION, 0)
    }

    private fun saveVersion(context: Context, version: Int): Boolean {
        return getPrefs(context).edit().putInt(KEY_APP_VERSION, version).commit()
    }
}