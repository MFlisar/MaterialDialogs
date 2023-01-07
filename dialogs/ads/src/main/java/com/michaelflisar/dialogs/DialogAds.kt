package com.michaelflisar.dialogs

import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import com.google.ads.mediation.admob.AdMobAdapter
import com.google.android.gms.ads.AdRequest
import com.michaelflisar.dialogs.ads.databinding.MdfContentAdsBinding
import com.michaelflisar.dialogs.classes.DefaultAdPrefs
import com.michaelflisar.dialogs.classes.Icon
import com.michaelflisar.dialogs.classes.MaterialDialogAction
import com.michaelflisar.dialogs.interfaces.IAdsPrefs
import com.michaelflisar.dialogs.interfaces.IMaterialDialogEvent
import com.michaelflisar.dialogs.interfaces.IMaterialEventManager
import com.michaelflisar.dialogs.interfaces.IMaterialViewManager
import com.michaelflisar.text.Text
import com.michaelflisar.text.asText
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
class DialogAds(
    // Key
    override val id: Int? = null,
    // Header
    override val title: Text = Text.Empty,
    override val icon: Icon = Icon.None,
    override val menu: Int? = null,
    // specific fields
    val info: Text,
    val setup: AdSetup,
    // Buttons
    override val buttonPositive: Text = MaterialDialog.defaults.buttonPositive,
    override val buttonNegative: Text = MaterialDialog.defaults.buttonNegative,
    override val buttonNeutral: Text = MaterialDialog.defaults.buttonNeutral,
    // Style

    // Attached Data
    override val extra: Parcelable? = null
) : MaterialDialogSetup<DialogAds>() {

    @IgnoredOnParcel
    override val cancelable: Boolean = false

    @IgnoredOnParcel
    override val viewManager: IMaterialViewManager<DialogAds, MdfContentAdsBinding> =
        AdsViewManager(this)

    @IgnoredOnParcel
    override val eventManager: IMaterialEventManager<DialogAds> =
        AdsEventManager(this)

    // -----------
    // Result Events
    // -----------

    sealed class Event : IMaterialDialogEvent {

        data class Result(
            override val id: Int?,
            override val extra: Parcelable?,
            //val button: MaterialDialogButton,
            val data: Data
        ) : Event() {
            sealed class Data {
                data class BannerShown(val error: Exception?) : Data()
                data class RewardShown(val error: Exception?, val type: String, val amount: Int) : Data()
                data class InterstitialShown(val successfullyLoaded: Boolean) : Data()
                data class ClosedByUser(val error: Exception?) : Data()
            }
        }

        data class Action(
            override val id: Int?,
            override val extra: Parcelable?,
            override val data: MaterialDialogAction
        ) : Event(), IMaterialDialogEvent.Action
    }

    // -----------
    // Enums/Classes
    // -----------

    companion object {
        /*
        * default is the test id from https://developers.google.com/admob/android/banner
        */
        const val TEST_ID_BANNER = "ca-app-pub-3940256099942544/6300978111"

        /*
         * default is the test id from https://developers.google.com/admob/android/banner
         */
        const val TEST_ID_INTERSTITIAL = "ca-app-pub-3940256099942544/1033173712"

        /*
         * default is the test id from https://developers.google.com/admob/android/banner
         */
        const val TEST_ID_REWARDED_VIDEO = "ca-app-pub-3940256099942544/5224354917"

        var DEFAULT_TIME_TO_SHOW_DIALOG_AFTER_ERROR = 10
    }


    sealed class AdSetup : Parcelable {

        abstract val personalised: Boolean
        abstract val adId: Text
        abstract val testDeviceIds: List<Text>
        abstract val timeToShowDialogAfterError: Int

        fun createAdRequest(): AdRequest {
            val adRequest = AdRequest.Builder()
            if (!personalised) {
                val extras = Bundle().apply {
                    putString("npa", "1")
                }
                adRequest.addNetworkExtrasBundle(AdMobAdapter::class.java, extras)
            }
            return adRequest.build()
        }

        sealed interface BigAd {
            val button: Text
        }

        @Parcelize
        class Banner(
            override val personalised: Boolean,
            override val adId: Text = TEST_ID_BANNER.asText(),
            override val testDeviceIds: List<Text> = emptyList(),
            override val timeToShowDialogAfterError: Int = DEFAULT_TIME_TO_SHOW_DIALOG_AFTER_ERROR
        ) : AdSetup()

        @Parcelize
        class Interstitial(
            override val personalised: Boolean,
            override val adId: Text = TEST_ID_INTERSTITIAL.asText(),
            override val button: Text,
            override val testDeviceIds: List<Text> = emptyList(),
            override val timeToShowDialogAfterError: Int = DEFAULT_TIME_TO_SHOW_DIALOG_AFTER_ERROR
        ) : AdSetup(), BigAd

        @Parcelize
        class Reward(
            override val personalised: Boolean,
            override val adId: Text = TEST_ID_REWARDED_VIDEO.asText(),
            override val button: Text,
            override val testDeviceIds: List<Text> = emptyList(),
            override val timeToShowDialogAfterError: Int = DEFAULT_TIME_TO_SHOW_DIALOG_AFTER_ERROR
        ) : AdSetup(), BigAd
    }

    sealed class ShowPolicy : Parcelable {

        abstract fun shouldShow(context: Context): Boolean

        @Parcelize
        object Always : ShowPolicy() {
            override fun shouldShow(context: Context): Boolean = true
        }

        @Parcelize
        class OnceDaily(val prefs: IAdsPrefs = DefaultAdPrefs()) : ShowPolicy() {

            private fun getLastShowPolicyDateAsCalendar(context: Context): Calendar? {
                val date = prefs.getLastShowPolicyDate(context)
                return if (date <= 0L) {
                    null
                } else {
                    Calendar.getInstance().apply {
                        timeInMillis = date
                    }
                }
            }

            override fun shouldShow(context: Context): Boolean {
                val now = Calendar.getInstance()
                val last = getLastShowPolicyDateAsCalendar(context)
                if (last == null) {
                    prefs.saveLastShowPolicyDate(context, now.timeInMillis)
                    return true
                } else {
                    if (last.get(Calendar.YEAR) < now.get(Calendar.YEAR) ||
                        last.get(Calendar.MONTH) < now.get(Calendar.MONTH) ||
                        last.get(Calendar.DAY_OF_MONTH) < now.get(Calendar.DAY_OF_MONTH)
                    ) {
                        prefs.saveLastShowPolicyDate(context, now.timeInMillis)
                        MaterialDialog.logger?.invoke(
                            Log.DEBUG,
                            "Showing ad dialog because of policy ${this::class.java.simpleName}!",
                            null
                        )
                        return true
                    } else {
                        MaterialDialog.logger?.invoke(
                            Log.DEBUG,
                            "SKIPPED showing ad dialog because of policy ${this::class.java.simpleName}!",
                            null
                        )
                        return false
                    }
                }
            }
        }

        @Parcelize
        class EveryXTime(val times: Int, val prefs: IAdsPrefs = DefaultAdPrefs()) : ShowPolicy() {
            override fun shouldShow(context: Context): Boolean {
                var currentTimes = prefs.getLastPolicyCounter(context)
                currentTimes++
                if (currentTimes < times) {
                    prefs.setLastPolicyCounter(context, currentTimes)
                    MaterialDialog.logger?.invoke(
                        Log.DEBUG,
                        "SKIPPED showing ad dialog because of policy ${this::class.java.simpleName} (currentTimes = $currentTimes, policy times = $times)!",
                        null
                    )
                    return false
                } else {
                    MaterialDialog.logger?.invoke(
                        Log.DEBUG,
                        "Showing ad dialog because of policy ${this::class.java.simpleName} (policy times = $times)!",
                        null
                    )
                    prefs.setLastPolicyCounter(context, 0)
                    return true
                }
            }
        }
    }
}