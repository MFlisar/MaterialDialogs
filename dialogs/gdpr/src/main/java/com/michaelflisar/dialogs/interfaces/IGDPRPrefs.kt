package com.michaelflisar.dialogs.interfaces

import android.content.Context
import android.os.Parcelable
import com.michaelflisar.dialogs.classes.GDPRConsent
import com.michaelflisar.dialogs.classes.GDPRConsentState
import com.michaelflisar.dialogs.classes.GDPRLocation

interface IGDPRPrefs: Parcelable {
    fun getState(context: Context): GDPRConsentState
    fun saveState(context: Context, state: GDPRConsentState): Boolean
}