package com.michaelflisar.dialogs.utils

import android.content.Context
import android.text.TextUtils
import android.util.Log
import com.michaelflisar.dialogs.MaterialDialog
import com.michaelflisar.dialogs.classes.*
import com.michaelflisar.dialogs.gdpr.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL


internal object DataUtil {

    enum class DataType {
        Location,
        SubNetworks,
        Both
    }

    private sealed class InternalData {
        class Success(
            val location: GDPRLocation = GDPRLocation.UNDEFINED,
            val subNetworks: List<GDPRSubNetwork> = emptyList()
        ) : InternalData()

        class Error(
            val error: Exception
        ) : InternalData()
    }

    suspend fun load(context: Context, setup: GDPRSetup, type: DataType): GDPRData {
        return withContext(Dispatchers.IO) {

            val loadLocation = when (type) {
                DataType.Location -> true
                DataType.SubNetworks -> false
                DataType.Both -> true
            }

            val loadSubNetworks = when (type) {
                DataType.Location -> false
                DataType.SubNetworks -> true
                DataType.Both -> true
            }

            val supportsLocationCheckViaInternet = setup.supportsLocationCheckViaInternet()

            // 1) eventually load the JSON data from the internet
            var data: InternalData? = null
            if (loadSubNetworks || (supportsLocationCheckViaInternet && loadLocation)) {
                data = load(
                    context,
                    setup.publisherIds,
                    setup.connectionReadTimeout,
                    setup.connectionConnectTimeout,
                    supportsLocationCheckViaInternet
                )
            }

            // 2) check location if necessary
            var location = GDPRLocation.UNDEFINED
            if (loadLocation)
                location = getLocation(context, setup, data)

            // 3) check sub ad networks if necessary
            var subNetworks: List<GDPRSubNetwork> = emptyList()
            if (loadSubNetworks)
                subNetworks = getSubNetworks(data)

            val result = GDPRData(location, subNetworks)
            MaterialDialog.logger?.invoke(Log.DEBUG, "result = $result", null)
            result
        }
    }

    private fun getLocation(context: Context, setup: GDPRSetup, data: InternalData?): GDPRLocation {
        for (check in setup.requestLocationChecks) {
            val location = getLocation(context, check, data)
            if (location != null)
                return location
        }
        return GDPRLocation.UNDEFINED
    }

    private fun getLocation(
        context: Context,
        check: GDPRLocationCheck,
        data: InternalData?
    ): GDPRLocation? {
        return when (check) {
            GDPRLocationCheck.INTERNET -> {
                if (data is InternalData.Success)
                    data.location
                else {
                    MaterialDialog.logger?.invoke(
                        Log.ERROR,
                        "Failed to detect location via internet - data = $data!",
                        null
                    )
                    null
                }
            }
            GDPRLocationCheck.TELEPHONY_MANAGER -> {
                val isInEAAOrUnknown =
                    GDPRUtils.isRequestInEAAOrUnknownViaTelephonyManagerCheck(context)
                if (isInEAAOrUnknown != null) {
                    if (isInEAAOrUnknown) GDPRLocation.IN_EAA_OR_UNKNOWN else GDPRLocation.NOT_IN_EAA
                } else {
                    MaterialDialog.logger?.invoke(
                        Log.ERROR,
                        "Failed to detect location via telephony manager!",
                        null
                    )
                    null
                }
            }
            GDPRLocationCheck.TIMEZONE -> {
                val isInEAAOrUnknown = GDPRUtils.isRequestInEAAOrUnknownViaTimezoneCheck
                if (isInEAAOrUnknown != null) {
                    if (isInEAAOrUnknown) GDPRLocation.IN_EAA_OR_UNKNOWN else GDPRLocation.NOT_IN_EAA
                } else {
                    MaterialDialog.logger?.invoke(
                        Log.ERROR,
                        "Failed to detect location via timezone!",
                        null
                    )
                    null
                }
            }
            GDPRLocationCheck.LOCALE -> {
                val isInEAAOrUnknown = GDPRUtils.isRequestInEAAOrUnknownViaLocaleCheck
                if (isInEAAOrUnknown != null) {
                    if (isInEAAOrUnknown) GDPRLocation.IN_EAA_OR_UNKNOWN else GDPRLocation.NOT_IN_EAA
                } else {
                    MaterialDialog.logger?.invoke(
                        Log.ERROR,
                        "Failed to detect location via locale!",
                        null
                    )
                    null
                }
            }
        }
    }

    private fun getSubNetworks(data: InternalData?): List<GDPRSubNetwork> {
        return when (data) {
            is InternalData.Error -> emptyList()
            is InternalData.Success -> data.subNetworks
            null -> emptyList()
        }
    }

    private fun load(
        context: Context,
        publisherIds: List<String>,
        readTimeout: Int,
        connectTimeout: Int,
        checkLocationViaInternet: Boolean
    ): InternalData {
        try {
            val jsonObject = loadJSON(context, publisherIds, readTimeout, connectTimeout)
            val fieldIsRequestInEaaOrUnknown =
                context.getString(R.string.gdpr_googles_check_json_field_is_request_in_eea_or_unknown)
            val fieldCompanies = context.getString(R.string.gdpr_googles_check_json_field_companies)
            val isInEAAOrUnknown = jsonObject.getBoolean(fieldIsRequestInEaaOrUnknown)
            val location = if (checkLocationViaInternet) {
                if (isInEAAOrUnknown) GDPRLocation.IN_EAA_OR_UNKNOWN else GDPRLocation.NOT_IN_EAA
            } else GDPRLocation.UNDEFINED
            val subNetworks = ArrayList<GDPRSubNetwork>()
            if (jsonObject.has(fieldCompanies)) {
                val fieldCompanyName =
                    context.getString(R.string.gdpr_googles_check_json_field_company_name)
                val fieldPolicyUrl =
                    context.getString(R.string.gdpr_googles_check_json_field_policy_url)
                val array = jsonObject.getJSONArray(fieldCompanies)
                for (i in 0 until array.length()) {
                    subNetworks.add(
                        GDPRSubNetwork(
                            array.getJSONObject(i).getString(fieldCompanyName),
                            array.getJSONObject(i).getString(fieldPolicyUrl)
                        )
                    )
                }
            }
            return InternalData.Success(location, subNetworks)
        } catch (e: Exception) {
            MaterialDialog.logger?.invoke(
                Log.ERROR,
                "Could not load location from network",
                e
            )
            return InternalData.Error(e)
        }
    }

    @Throws(IOException::class, JSONException::class)
    private fun loadJSON(
        context: Context,
        publisherIds: List<String>,
        readTimeout: Int,
        connectTimeout: Int
    ): JSONObject {

        val publisherIdsString = TextUtils.join(",", publisherIds)
        val url = URL(
            context.getString(
                R.string.gdpr_googles_check_is_eaa_request_url,
                publisherIdsString
            )
        )

        val urlConnection = (url.openConnection() as HttpURLConnection)
            .apply {
                requestMethod = "GET"
                this.readTimeout = readTimeout
                this.connectTimeout = connectTimeout
                doOutput = true
                connect()
            }

        val br = BufferedReader(InputStreamReader(urlConnection.inputStream))
        val sb = StringBuilder()
        var line: String
        while (br.readLine().also { line = it } != null) {
            sb.append(line.trimIndent())
        }
        br.close()
        return JSONObject(sb.toString())
    }
}