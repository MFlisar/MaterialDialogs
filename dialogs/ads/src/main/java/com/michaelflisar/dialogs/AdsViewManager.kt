package com.michaelflisar.dialogs

import android.os.Bundle
import android.os.Handler
import android.os.Parcelable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.michaelflisar.dialogs.ads.R
import com.michaelflisar.dialogs.ads.databinding.MdfContentAdsBinding
import com.michaelflisar.dialogs.classes.BaseMaterialViewManager
import com.michaelflisar.dialogs.classes.MaterialDialogButton
import kotlinx.parcelize.Parcelize

internal class AdsViewManager(
    override val setup: DialogAds
) : BaseMaterialViewManager<DialogAds, MdfContentAdsBinding>() {

    override val wrapInScrollContainer = false

    private lateinit var viewState: ViewState

    private var error: Exception? = null

    private val handlerTimer = Handler()
    private val runnable = Runnable { checkBannerTimeout() }

    private var interstitialAd: InterstitialAd? = null
    private var rewardedAd: RewardedAd? = null

    override fun onCreateContentViewBinding(
        layoutInflater: LayoutInflater,
        parent: ViewGroup?,
        attachToParent: Boolean
    ): MdfContentAdsBinding {
        MobileAds.initialize(layoutInflater.context) {
            //MaterialDialog.logger?.invoke(Log.DEBUG, "$it", null)
        }
        val configuration = RequestConfiguration.Builder()
            .setTestDeviceIds(setup.setup.testDeviceIds.map { it.getString(layoutInflater.context) })
            .build()
        MobileAds.setRequestConfiguration(configuration)

        return MdfContentAdsBinding.inflate(layoutInflater, parent, attachToParent)
    }

    override fun initBinding(savedInstanceState: Bundle?) {
        viewState = MaterialDialogUtil.getViewState(savedInstanceState)
            ?: run { ViewState(setup.setup.timeToShowDialogAfterError) }

        binding.tvInfo.text = setup.info.get(presenter.requireContext())
        binding.btShow.isEnabled = false

        val adSetup = setup.setup
        val adId = adSetup.adId.getString(presenter.requireContext())

        when (adSetup) {
            is DialogAds.AdSetup.Banner -> {
                binding.btShow.visibility = View.GONE
                val adView = AdView(presenter.requireContext()).apply {
                    setAdSize(AdSize.BANNER)
                    adUnitId = adId
                }
                replaceAdView(adView)
                adView.adListener = createAdListener()
                adView.loadAd(setup.setup.createAdRequest())
            }
            is DialogAds.AdSetup.Interstitial,
            is DialogAds.AdSetup.Reward -> {

                binding.adView.visibility = View.GONE

                adSetup as DialogAds.AdSetup.BigAd

                binding.btShow.text = adSetup.button.get(presenter.requireContext())
                binding.btShow.setOnClickListener {
                    showBigAd()
                }

                when (adSetup) {
                    is DialogAds.AdSetup.Interstitial -> {
                        InterstitialAd.load(
                            presenter.requireContext(),
                            adSetup.adId.getString(presenter.requireContext()),
                            setup.setup.createAdRequest(),
                            createInterstitialAdListener()
                        )
                    }
                    is DialogAds.AdSetup.Reward -> {
                        RewardedAd.load(
                            presenter.requireContext(),
                            adSetup.adId.getString(presenter.requireContext()),
                            setup.setup.createAdRequest(),
                            createRewardedVideoAdListener()
                        )
                    }
                }
            }
        }
    }

    override fun onButtonsReady() {
        enableButtons(false)
    }

    override fun saveViewState(outState: Bundle) {
        MaterialDialogUtil.saveViewState(
            outState,
            viewState
        )
    }

    fun getStateForButtonClick(): DialogAds.Event.Result.Data {
        return when (setup.setup) {
            is DialogAds.AdSetup.Banner -> DialogAds.Event.Result.Data.BannerShown(error)
            is DialogAds.AdSetup.Interstitial,
            is DialogAds.AdSetup.Reward -> {
                // this type of ad dialog is automatically closed on success and only allows to cancel it the ad was not watched
                DialogAds.Event.Result.Data.ClosedByUser(error)
            }
        }
    }

    override fun onDestroy() {
        interstitialAd = null
        rewardedAd = null
        handlerTimer.removeCallbacksAndMessages(null)
        super.onDestroy()
    }

    // -----------
    // helper functions
    // -----------

    private fun replaceAdView(adView: View) {
        val parent = binding.adView.parent as ViewGroup
        val index = parent.indexOfChild(binding.adView)
        parent.removeView(binding.adView)
        parent.addView(adView, index)
    }

    private fun createAdListener(type: String = "BANNER"): AdListener {
        return object : AdListener() {
            override fun onAdClosed() {
                MaterialDialog.logger?.invoke(Log.DEBUG, "[$type] Ad closed...", null)
            }

            override fun onAdFailedToLoad(error: LoadAdError) {
                onAdLoadingError(error.message)
            }

            override fun onAdOpened() {
                MaterialDialog.logger?.invoke(Log.DEBUG, "[$type] Ad opened...", null)
            }

            override fun onAdLoaded() {
                MaterialDialog.logger?.invoke(Log.DEBUG, "[$type] Ad loaded...", null)
                this@AdsViewManager.onAdLoaded()
            }

            override fun onAdClicked() {
                MaterialDialog.logger?.invoke(Log.DEBUG, "[$type] Ad clicked...", null)
            }

            override fun onAdImpression() {
                MaterialDialog.logger?.invoke(Log.DEBUG, "[$type] Ad IMPRESSION...", null)
            }
        }
    }

    private fun createRewardedVideoAdListener(): RewardedAdLoadCallback {
        return object : RewardedAdLoadCallback() {
            override fun onAdLoaded(ad: RewardedAd) {
                super.onAdLoaded(ad)
                MaterialDialog.logger?.invoke(Log.DEBUG, "Rewarded ad loaded...", null)
                ad.fullScreenContentCallback = createFullScreenContentCallback("Rewarded")
                rewardedAd = ad
                this@AdsViewManager.onAdLoaded()
            }

            override fun onAdFailedToLoad(error: LoadAdError) {
                super.onAdFailedToLoad(error)
                onAdLoadingError(error.message)
            }
        }
    }

    private fun createInterstitialAdListener(): InterstitialAdLoadCallback {
        return object : InterstitialAdLoadCallback() {
            override fun onAdLoaded(ad: InterstitialAd) {
                super.onAdLoaded(ad)
                MaterialDialog.logger?.invoke(Log.DEBUG, "Interstitial ad loaded...", null)
                ad.fullScreenContentCallback = createFullScreenContentCallback("Interstitial")
                interstitialAd = ad
                this@AdsViewManager.onAdLoaded()
            }

            override fun onAdFailedToLoad(error: LoadAdError) {
                super.onAdFailedToLoad(error)
                onAdLoadingError(error.message)
            }
        }
    }

    private fun createFullScreenContentCallback(type: String): FullScreenContentCallback {
        return object : FullScreenContentCallback() {
            override fun onAdClicked() {
                MaterialDialog.logger?.invoke(Log.DEBUG, "[$type] FullScreen ad clicked", null)
            }

            override fun onAdDismissedFullScreenContent() {
                MaterialDialog.logger?.invoke(Log.DEBUG, "[$type] FullScreen ad dismissed", null)
                interstitialAd = null
                rewardedAd = null
            }

            override fun onAdFailedToShowFullScreenContent(error: AdError) {
                onAdLoadingError(error.message)
                interstitialAd = null
                rewardedAd = null
            }

            override fun onAdImpression() {
                MaterialDialog.logger?.invoke(Log.DEBUG, "[$type] FullScreen ad - IMPRESSION", null)
            }

            override fun onAdShowedFullScreenContent() {
                MaterialDialog.logger?.invoke(Log.DEBUG, "[$type] FullScreen ad showed...", null)
            }
        }
    }

    private fun onAdLoaded() {
        onAdLoadingStatesChanged()
    }

    private fun onAdLoadingError(error: String) {
        val ex =
            Exception("Loading ad of ${setup.setup.javaClass.simpleName} failed with error = ${error}!")
        MaterialDialog.logger?.invoke(Log.ERROR, "", ex)
        this.error = ex
        onAdLoadingStatesChanged()
    }

    private fun onAdLoadingStatesChanged() {
        if (error != null) {
            startTimer()
        } else {
            when (setup.setup) {
                is DialogAds.AdSetup.Banner -> startTimer()
                is DialogAds.AdSetup.Interstitial,
                is DialogAds.AdSetup.Reward -> {
                    binding.tvInfoPreparing.setText(R.string.mdf_dialogs_info_ad_ready)
                    binding.btShow.isEnabled = true
                    enableButtons(true)
                }
            }
        }
    }

    private fun startTimer() {
        if (viewState.timeLeft > 0) {
            handlerTimer.postDelayed(runnable, 1000)
        } else {
            checkBannerTimeout()
        }
    }

    private fun enableButtons(enabled: Boolean) {
        presenter.setButtonEnabled(MaterialDialogButton.Positive, enabled)
        presenter.setButtonEnabled(MaterialDialogButton.Negative, enabled)
        presenter.setButtonEnabled(MaterialDialogButton.Neutral, enabled)
    }

    private fun showBigAd() {
        when (setup.setup) {
            is DialogAds.AdSetup.Banner -> {
                // never happens!
            }
            is DialogAds.AdSetup.Interstitial -> {
                val activity = MaterialDialogUtil.getActivity(presenter.requireContext())!!

                if (interstitialAd == null) {
                    MaterialDialog.logger?.invoke(
                        Log.ERROR,
                        "Interstitial ad not ready!",
                        null
                    )
                } else {
                    interstitialAd?.show(activity)
                    MaterialDialog.logger?.invoke(Log.DEBUG, "Showing interstitial ad...", null)
                }
            }
            is DialogAds.AdSetup.Reward -> {
                val activity = presenter.requireContext() as AppCompatActivity
                rewardedAd?.show(activity) {
                    MaterialDialog.logger?.invoke(Log.DEBUG, "Rewarded: ${it.type} - ${it.amount}", null)
                    (setup.eventManager as AdsEventManager).sendEvent(
                        presenter,
                        DialogAds.Event.Result.Data.RewardShown(error, it.type, it.amount)
                    )
                    presenter.dismiss?.invoke()
                }
                if (rewardedAd == null) {
                    MaterialDialog.logger?.invoke(
                        Log.ERROR,
                        "Rewarded ad not ready!",
                        null
                    )
                } else {
                    MaterialDialog.logger?.invoke(Log.DEBUG, "Showing rewarded ad...", null)
                }
            }
        }
    }

    private fun checkBannerTimeout() {
        var timeLeft = viewState.timeLeft
        if (timeLeft > 0) {
            timeLeft--
            viewState = viewState.copy(timeLeft = timeLeft)
        }

        val (timeoutRes, timeoutOverRes) = if (error != null) {
            Pair(
                R.string.mdf_dialogs_info_error_ad_timeout,
                R.string.mdf_dialogs_info_error_ad_timeout_over
            )
        } else {
            Pair(
                R.string.mdf_dialogs_info_ad_ready_banner_timeout,
                R.string.mdf_dialogs_info_ad_ready_banner_timeout_over
            )
        }
        if (timeLeft > 0) {
            binding.tvInfoPreparing.text = context.getString(timeoutRes, timeLeft)
            handlerTimer.postDelayed(runnable, 1000)
        } else {
            binding.tvInfoPreparing.text = context.getString(timeoutOverRes)
            enableButtons(true)
        }
    }

    // -----------
    // State
    // -----------

    @Parcelize
    private data class ViewState(
        val timeLeft: Int
    ) : Parcelable
}