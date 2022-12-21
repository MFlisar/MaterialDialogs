package com.michaelflisar.dialogs.classes

import android.content.Context
import android.os.Parcelable
import com.michaelflisar.dialogs.gdpr.R
import com.michaelflisar.dialogs.utils.GDPRUtils
import com.michaelflisar.text.Text
import kotlinx.parcelize.Parcelize

@Parcelize
data class GDPRNetwork(
    val name: String,
    val link: String?,
    val type: Text,
    var isAdNetwork: Boolean = false,
    var subNetworks: List<GDPRSubNetwork> = emptyList(),
    var intermediator: Intermediator? = null
) : Parcelable {

    fun getHtmlLink(
        context: Context,
        withIntermediatorLink: Boolean,
        withSubNetworks: Boolean
    ): String {
        var link = "<a href=\"$link\">$name</a>"
        if (withIntermediatorLink && intermediator?.subNetworksLink != null) {
            link += " (<a href=\"" + intermediator?.subNetworksLink + "\">" + context.getString(R.string.gdpr_show_me_partners) + "</a>)"
        }
        if (withSubNetworks && subNetworks.size > 0) {
            link += " ("
            val values: MutableList<String> = ArrayList()
            for (subNetwork in subNetworks) {
                values.add(subNetwork.htmlLink)
            }
            link += GDPRUtils.getCommaSeperatedString(context, values)
            link += ")"
        }
        return link
    }

    @Parcelize
    data class Intermediator(
        var subNetworksLink: String? = null
    ) : Parcelable
}