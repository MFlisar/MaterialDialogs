package com.michaelflisar.dialogs.classes

import android.content.Context
import com.michaelflisar.dialogs.utils.GDPRUtils
import java.util.*

data class GDPRConsentState(
    val consent: GDPRConsent = GDPRConsent.UNKNOWN,
    val location: GDPRLocation = GDPRLocation.UNDEFINED,
    val date: Long = -1L,
    val version: Int = -1
) {
    constructor(context: Context, consent: GDPRConsent, location: GDPRLocation) : this(
        consent,
        location,
        Date().time,
        GDPRUtils.getAppVersion(context),
    )
}
