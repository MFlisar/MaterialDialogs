package com.michaelflisar.dialogs.classes

enum class GDPRConsent {
    /*
     * users consent is unknown, it needs to be requests
     */
    UNKNOWN,

    /*
     * user consent given: he does not accept any usage of personal data nor non personal data
     */
    NO_CONSENT,

    /*
     * user consent given: he accept non personal data only
     */
    NON_PERSONAL_CONSENT_ONLY,

    /*
     * user consent given: he accepts personal data usage
     */
    PERSONAL_CONSENT,

    /*
     * user consent automatically set because of request location was checked and location was outside of the EAA: personal data usage is possible
     */
    AUTOMATIC_PERSONAL_CONSENT;

    val isPersonalConsent: Boolean
        get() = this == PERSONAL_CONSENT || this == AUTOMATIC_PERSONAL_CONSENT
}