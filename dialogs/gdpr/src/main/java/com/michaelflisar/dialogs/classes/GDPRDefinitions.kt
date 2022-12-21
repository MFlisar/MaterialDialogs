package com.michaelflisar.dialogs.classes

import com.michaelflisar.dialogs.gdpr.R
import com.michaelflisar.text.asText

object GDPRDefinitions {

    // -------------------
    // Ad Networks
    // -------------------

    val ADMOB = GDPRNetwork(
        name = "AdMob",
        link = "https://policies.google.com/privacy",
        type = R.string.gdpr_type_ads.asText(),
        isAdNetwork = true,
        intermediator = GDPRNetwork.Intermediator("https://support.google.com/admob/answer/9012903")
    )

    val AERSERV = GDPRNetwork(
        name = "AerServ",
        link = "https://www.aerserv.com/privacy-policy",
        type = R.string.gdpr_type_ads.asText(),
        isAdNetwork = true
    )

    val INMOBI = GDPRNetwork(
        name = "InMobi",
        link = "https://www.inmobi.com/privacy-policy-for-eea",
        type = R.string.gdpr_type_ads.asText(),
        isAdNetwork = true
    )

    val MOPUB = GDPRNetwork(
        name = "MoPub",
        link = "https://www.mopub.com/legal/privacy",
        type = R.string.gdpr_type_ads.asText(),
        isAdNetwork = true,
        intermediator = GDPRNetwork.Intermediator("https://www.mopub.com/legal/partners/")
    )

    val VUNGLE = GDPRNetwork(
        name = "Vungle",
        link = "https://vungle.com/privacy",
        type = R.string.gdpr_type_ads.asText(),
        isAdNetwork = true
    )

    val ADCOLONY = GDPRNetwork(
        name = "AdColony",
        link = "https://www.adcolony.com/privacy-policy",
        type = R.string.gdpr_type_ads.asText(),
        isAdNetwork = true
    )

    val UNITY = GDPRNetwork(
        name = "Unity",
        link = "https://unity3d.com/legal/privacy-policy",
        type = R.string.gdpr_type_ads.asText(),
        isAdNetwork = true
    )

    val APPLOVIN = GDPRNetwork(
        name = "AppLovin",
        link = "https://www.applovin.com/privacy",
        type = R.string.gdpr_type_ads.asText(),
        isAdNetwork = true
    )

    val FAN = GDPRNetwork(
        name = "Facebook",
        link = "https://www.facebook.com/privacy/explanation",
        type = R.string.gdpr_type_ads.asText(),
        isAdNetwork = true
    )

    val APPNEXT = GDPRNetwork(
        name = "AppNext",
        link = "https://www.appnext.com/policy.html#",
        type = R.string.gdpr_type_ads.asText(),
        isAdNetwork = true
    )

    val MOBVISTA = GDPRNetwork(
        name = "MobVista",
        link = "https://www.mobvista.com/en/privacy/",
        type = R.string.gdpr_type_ads.asText(),
        isAdNetwork = true
    )

    val FLURRY_ADS = GDPRNetwork(
        name = "Flurry Ads",
        link = "https://policies.oath.com/us/en/oath/privacy/index.html",
        type = R.string.gdpr_type_ads.asText(),
        isAdNetwork = true
    )

    val GLISPA = GDPRNetwork(
        name = "Glispa",
        link = "https://www.glispa.com/privacy-policy/",
        type = R.string.gdpr_type_ads.asText(),
        isAdNetwork = true
    )

    val TAPJOY = GDPRNetwork(
        name = "Tapjoy",
        link = "https://dev.tapjoy.com/faq/tapjoy-privacy-policy/",
        type = R.string.gdpr_type_ads.asText(),
        isAdNetwork = true
    )

    val APPBRAIN = GDPRNetwork(
        name = "AppBrain",
        link = "https://www.appbrain.com/info/help/privacy/index.html",
        type = R.string.gdpr_type_ads.asText(),
        isAdNetwork = true
    )

    val STARTAPP = GDPRNetwork(
        name = "StartApp",
        link = "https://www.startapp.com/policy/privacy-policy/",
        type = R.string.gdpr_type_ads.asText(),
        isAdNetwork = true
    )

    val IRONSOURCE = GDPRNetwork(
        name = "ironSource",
        link = "https://developers.ironsrc.com/ironsource-mobile/air/ironsource-mobile-privacy-policy/",
        type = R.string.gdpr_type_ads.asText(),
        isAdNetwork = true
    )

    val APPODEAL = GDPRNetwork(
        name = "Appodeal",
        link = "https://www.appodeal.com/privacy-policy",
        type = R.string.gdpr_type_ads.asText(),
        isAdNetwork = true,
        intermediator = GDPRNetwork.Intermediator("https://www.appodeal.com/home/partners-privacy-policies/")
    )

    val MOBFOX = GDPRNetwork(
        name = "Mobfox",
        link = "https://www.mobfox.com/privacy-policy/",
        type = R.string.gdpr_type_ads.asText(),
        isAdNetwork = true
    )

    val MINTEGRAL = GDPRNetwork(
        name = "Mintegral",
        link = "https://www.mintegral.com/en/privacy",
        type = R.string.gdpr_type_ads.asText(),
        isAdNetwork = true
    )

    // -------------------
    // Others
    // -------------------

    private const val firebase = "Firebase"
    private const val firebaseUrl = "https://firebase.google.com/support/privacy"

    val FIREBASE_DATABASE = GDPRNetwork(
        name = firebase,
        link = firebaseUrl,
        type = R.string.gdpr_type_cloud_database.asText(),
        isAdNetwork = false
    )

    val FIREBASE_CRASH = GDPRNetwork(
        name = firebase,
        link = firebaseUrl,
        type = R.string.gdpr_type_crash.asText(),
        isAdNetwork = false
    )

    val FIREBASE_ANALYTICS = GDPRNetwork(
        name = firebase,
        link = firebaseUrl,
        type = R.string.gdpr_type_analytics.asText(),
        isAdNetwork = false
    )

    val FIREBASE_CLOUD_MESSAGING = GDPRNetwork(
        name = firebase,
        link = firebaseUrl,
        type = R.string.gdpr_type_notifications.asText(),
        isAdNetwork = false
    )

    private const val flurry = "Flurry"
    private const val flurryUrl = "https://policies.oath.com/us/en/oath/privacy/index.html"

    val FLURRY_ANALYTICS = GDPRNetwork(
        name = flurry,
        link = flurryUrl,
        type = R.string.gdpr_type_analytics.asText(),
        isAdNetwork = false
    )

    val FLURRY_CRASH = GDPRNetwork(
        name = flurry,
        link = flurryUrl,
        type = R.string.gdpr_type_crash.asText(),
        isAdNetwork = false
    )

    private const val fabric = "Fabric"
    private const val fabricUrl = "https://fabric.io/terms"

    val FABRIC_CRASHLYTICS = GDPRNetwork(
        name = fabric,
        link = fabricUrl,
        type = R.string.gdpr_type_crash.asText(),
        isAdNetwork = false
    )

    val FABRIC_ANSWERS = GDPRNetwork(
        name = fabric,
        link = fabricUrl,
        type = R.string.gdpr_type_analytics.asText(),
        isAdNetwork = false
    )

    private const val localytics = "Localytics"
    private const val localyticsUrl = "https://www.localytics.com/privacy-policy/"

    val LOCALYTICS_ANALYTICS = GDPRNetwork(
        name = localytics,
        link = localyticsUrl,
        type = R.string.gdpr_type_analytics.asText(),
        isAdNetwork = false
    )

    val LOCALYTICS_MESSAGING = GDPRNetwork(
        name = localytics,
        link = localyticsUrl,
        type = R.string.gdpr_type_notifications.asText(),
        isAdNetwork = false
    )

    private const val adobe = "Adobe"
    private const val adobeUrl = "https://www.adobe.com/privacy/policy.html"

    val ADOBE_ID = GDPRNetwork(
        name = adobe,
        link = adobeUrl,
        type = R.string.gdpr_type_authorization.asText(),
        isAdNetwork = false
    )

    val ONESIGNAL = GDPRNetwork(
        name = "OneSignal",
        link = "https://onesignal.com/privacy_policy",
        type = R.string.gdpr_type_notifications.asText(),
        isAdNetwork = false
    )
}