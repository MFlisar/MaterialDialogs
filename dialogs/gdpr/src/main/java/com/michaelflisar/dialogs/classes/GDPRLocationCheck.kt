package com.michaelflisar.dialogs.classes

enum class GDPRLocationCheck {
    /**
     * use this to check the location via the same url google's consent sdk uses
     */
    INTERNET,

    /**
     * use this to check the location via the TelephonyManager
     */
    TELEPHONY_MANAGER,

    /**
     * use this to check the location via the TimeZone
     */
    TIMEZONE,

    /**
     * use this to check the location via the locale
     */
    LOCALE;

    companion object {
        var DEFAULT = listOf(INTERNET)
        var DEFAULT_WITH_FALLBACKS = listOf(INTERNET, TELEPHONY_MANAGER, TIMEZONE, LOCALE)
    }
}