package com.michaelflisar.dialogs.classes

import android.content.Context
import android.os.Parcelable
import com.michaelflisar.dialogs.interfaces.IGDPRPrefs
import com.michaelflisar.dialogs.utils.GDPRUtils
import kotlinx.parcelize.Parcelize

@Parcelize
data class GDPRSetup(
    val networks: List<GDPRNetwork>,
    val policyLink: String? = null,
    val prefs: IGDPRPrefs = DefaultGDPRPrefs(),
    val requestLocationChecks: List<GDPRLocationCheck> = GDPRLocationCheck.DEFAULT_WITH_FALLBACKS,
    val hasPaidVersion: Boolean = false,
    val forceSelection: Boolean = false,
    val allowNoConsent: Boolean = false,
    val allowNonPersonalisedForPaidVersion: Boolean = false,
    val explicitNonPersonalisedConfirmation: Boolean = false,
    val explicitAgeConfirmation: Boolean = false,
    val showPaidOrFreeInfoText: Boolean = true,
    val shortQuestion: Boolean = false,
    val publisherIds: List<String> = emptyList(),
    val connectionReadTimeout: Int = 3000,
    val connectionConnectTimeout: Int = 5000,
    val customTexts: GDPRCustomTexts = GDPRCustomTexts()
) : Parcelable {

    fun getFullPolicyLink(): String? {
        if (policyLink == null)
            return null
        var link = policyLink
        if (!link.startsWith("https://") && !link.startsWith("http://")) {
            link = "http://$policyLink"
        }
        link = link.replace("http://", "https://")
        return link
    }

    fun getNetworksCommaSeperated(context: Context, withLinks: Boolean): String {
        return GDPRUtils.getNetworksString(networks, context, withLinks)
    }

    fun allowAnyNoConsent(): Boolean {
        return allowNoConsent || allowNonPersonalisedForPaidVersion
    }

    fun containsAdNetwork(): Boolean {
        return networks.find { it.isAdNetwork } != null
    }

    fun getNetworkTypes(context: Context): HashSet<String> {
        val uniqueTypes = HashSet<String>()
        uniqueTypes.addAll(networks.map { it.type.getString(context) })
        return uniqueTypes
    }

    fun getNetworkTypesCommaSeperated(context: Context): String {
        return GDPRUtils.getCommaSeperatedString(context, getNetworkTypes(context))
    }

    fun supportsLocationCheckViaInternet(): Boolean {
        for (i in requestLocationChecks.indices) {
            if (requestLocationChecks[i] == GDPRLocationCheck.INTERNET) {
                return true
            }
        }
        return false
    }
}