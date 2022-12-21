package com.michaelflisar.dialogs.utils

import android.content.Context
import android.content.pm.PackageManager
import android.telephony.TelephonyManager
import android.util.Log
import com.michaelflisar.dialogs.MaterialDialog
import com.michaelflisar.dialogs.classes.GDPRNetwork
import com.michaelflisar.dialogs.gdpr.R
import java.util.*

internal object GDPRUtils {
    /**
     * get the current app version code
     *
     * @param context any context that is used to get the app verion code
     * @return the app version or -1 if something went wrong
     */
    fun getAppVersion(context: Context): Int {
        try {
            val pInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            return pInfo.versionCode
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return -1
    }

    /**
     * checks the location via [TelephonyManager]
     *
     * @param context context used to get [TelephonyManager]
     * @return true, if location is within EAA, false if not and null in case of an error
     */
    fun isRequestInEAAOrUnknownViaTelephonyManagerCheck(context: Context): Boolean? {
        var error = false

        /* is eu sim */try {
            val tm = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            var simCountry = tm.simCountryIso
            if (simCountry != null && simCountry.length == 2) {
                simCountry = simCountry.uppercase(Locale.getDefault())
                if (EUCountry.contains(simCountry)) {
                    return true
                }
            }
        } catch (e: Exception) {
            error = true
            MaterialDialog.logger?.invoke(
                Log.ERROR,
                "Could not get location from telephony manager via SimCountry",
                e
            )
        }


        /* is eu network */try {
            val tm = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            if (tm.phoneType != TelephonyManager.PHONE_TYPE_CDMA && tm.phoneType != TelephonyManager.PHONE_TYPE_NONE) {
                var networkCountry = tm.networkCountryIso
                if (networkCountry != null && networkCountry.length == 2) {
                    networkCountry = networkCountry.uppercase(Locale.getDefault())
                    if (EUCountry.contains(networkCountry)) {
                        return true
                    }
                }
            }
        } catch (e: Exception) {
            error = true
            MaterialDialog.logger?.invoke(
                Log.ERROR,
                "Could not load location from network via NetworkCountry",
                e
            )
        }
        return if (error) null else false
    }/* is eu time zone id */

    /**
     * checks the location via [TimeZone]
     *
     * @return true, if location is within EAA, false if not and null in case of an error
     */
    val isRequestInEAAOrUnknownViaTimezoneCheck: Boolean?
        get() {
            var error = false

            /* is eu time zone id */try {
                val tz = TimeZone.getDefault().id.lowercase(Locale.getDefault())
                if (tz.length < 10) {
                    error = true
                } else if (tz.contains("euro")) {
                    return true
                }
            } catch (e: Exception) {
                error = true
                MaterialDialog.logger?.invoke(
                    Log.ERROR,
                    "Could not get location from TimeZone",
                    e
                )
            }
            return if (error) null else false
        }/* is eu locale id */

    /**
     * checks the location via [Locale]
     *
     * @return true, if location is within EAA, false if not and null in case of an error
     */
    val isRequestInEAAOrUnknownViaLocaleCheck: Boolean?
        get() {
            var error = false

            /* is eu locale id */
            try {
                val locale = Locale.getDefault()
                val localeCountry = locale.country
                if (EUCountry.contains(localeCountry)) {
                    return true
                }
            } catch (e: Exception) {
                error = true
                MaterialDialog.logger?.invoke(
                    Log.ERROR,
                    "Could not get location from Locale",
                    e
                )
            }
            return if (error) null else false
        }

    /**
     * returns a comma seperated string of all items passed in; additioanlly it uses the seperator and last seperator defined in the resources
     *
     * @param context context used to get seperators
     * @param values  a list of values that should be concatenated
     * @return the comma seperated string
     */
    fun getCommaSeperatedString(context: Context, values: Collection<String>): String {
        val innerSep = context.getString(R.string.gdpr_list_seperator)
        val lastSep = context.getString(R.string.gdpr_last_list_seperator)
        var sep: String
        var types = ""
        var i = 0
        for (value in values) {
            if (i == 0) {
                types = value
            } else {
                sep = if (i == values.size - 1) lastSep else innerSep
                types += sep + value
            }
            i++
        }
        return types
    }

    fun getNetworksString(
        networks: List<GDPRNetwork>,
        context: Context,
        withLinks: Boolean
    ): String {
        val sb = StringBuilder("")
        val uniqueNetworks = HashSet<String>()
        for (i in networks.indices) {
            val addIntermediatorLink = networks[i].subNetworks.isEmpty()
            val text = if (withLinks) networks[i].getHtmlLink(
                context,
                addIntermediatorLink,
                true
            ) else networks[i].name
            if (uniqueNetworks.add(text)) {
                if (sb.isNotEmpty()) {
                    sb.append("<br>")
                }
                sb
                    .append("&#8226;&nbsp;")
                    .append(
                        if (withLinks) networks[i].getHtmlLink(
                            context,
                            addIntermediatorLink,
                            false
                        ) else networks[i].name
                    )
                for (subNetwork in networks[i].subNetworks) {
                    sb
                        .append("<br>")
                        .append("&nbsp;&nbsp;&#9702;&nbsp;")
                        .append(if (withLinks) subNetwork.htmlLink else subNetwork.name)
                }
            }
        }
        return sb.toString()
    }

    private enum class EUCountry {
        AT, BE, BG, HR, CY, CZ, DK, EE, FI, FR, DE, GR, HU, IE, IT, LV, LT, LU, MT, NL, PL, PT, RO, SK, SI, ES, SE, GB,  //28 member states
        GF, PF, TF,  //French territories French Guiana,Polynesia,Southern Territories
        EL, UK,  //alternative EU names for GR and GB
        IS, LI, NO,  //not EU but in EAA
        CH,  //not in EU or EAA but in single market
        AL, BA, MK, XK, ME, RS, TR;

        companion object {
            //candidate countries
            operator fun contains(s: String?): Boolean {
                for (country in values()) {
                    if (country.name.equals(s, ignoreCase = true)) return true
                }
                return false
            }
        }
    }
}