package com.michaelflisar.dialogs

import android.content.Context
import android.util.Log
import com.michaelflisar.dialogs.classes.*

object GDPR {

    /**
     * returns the current consent state, i.e. the last one given by the user or the default unknown consent state
     *
     * @param context the Context
     * @param setup the GDPRSetup
     *
     * @return the consent state
     */
    fun getCurrentConsentState(context: Context, setup: GDPRSetup): GDPRConsentState {
        return setup.prefs.getState(context)
    }

    /**
     * Checks if you must require consent from the user
     *
     *
     * it will call the callback [IGDPRCallback.onConsentNeedsToBeRequested] function if the
     * user should be asked for consent, otherwise it will directly call the [IGDPRCallback.onConsentInfoUpdate] function
     *
     * @param context the Context
     * @param setup the GDPRSetup
     *
     * @return true, if we need to ask the user for consent
     */
    fun shouldAskForConsent(context: Context, setup: GDPRSetup): Boolean {
        val consent = setup.prefs.getState(context)
        val checkConsent = when (consent.consent) {
            GDPRConsent.UNKNOWN -> true
            GDPRConsent.NO_CONSENT -> !setup.allowAnyNoConsent()
            GDPRConsent.NON_PERSONAL_CONSENT_ONLY, GDPRConsent.PERSONAL_CONSENT, GDPRConsent.AUTOMATIC_PERSONAL_CONSENT -> false
        }
        MaterialDialog.logger?.invoke(
            Log.DEBUG,
            "consent check needed: $checkConsent, current consent: $consent",
            null
        )
        return checkConsent
    }

    /**
     * return whether we can use personal informations or not
     *
     * @param context the Context
     * @param setup the GDPRSetup
     *
     * @return true, if we can collect personal informations, false otherwise
     */
    fun canCollectPersonalInformation(context: Context, setup: GDPRSetup): Boolean {
        // if user has given consent for personal data usage, we can collect personal information
        return setup.prefs.getState(context).consent.isPersonalConsent
    }

    /**
     * resets the current consent state to the undefined consent state
     *
     * @param context the Context
     * @param setup the GDPRSetup
     *
     * @return true, if consent state was saved successfully, false otherwise
     */
    fun resetConsent(context: Context, setup: GDPRSetup) {
        setup.prefs.saveState(context, GDPRConsentState())
    }

    /**
     * sets the current consent state and persists it
     *
     * @param context the Context
     * @param setup the GDPRSetup
     * @param state the consent state to save
     *
     * @return true, if consent state was saved successfully, false otherwise
     */
    fun setConsent(context: Context, setup: GDPRSetup, state: GDPRConsentState): Boolean {
        return setup.prefs.saveState(context, state)
    }
}