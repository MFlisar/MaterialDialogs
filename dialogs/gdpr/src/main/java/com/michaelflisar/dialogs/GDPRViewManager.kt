package com.michaelflisar.dialogs

import android.os.Bundle
import android.os.Parcelable
import android.text.Html
import android.text.SpannableStringBuilder
import android.text.method.LinkMovementMethod
import android.text.style.*
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.text.HtmlCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.snackbar.Snackbar
import com.michaelflisar.dialogs.classes.*
import com.michaelflisar.dialogs.gdpr.R
import com.michaelflisar.dialogs.gdpr.databinding.MdfContentGdprBinding
import com.michaelflisar.dialogs.utils.DataUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.parcelize.Parcelize
import java.util.*

internal class GDPRViewManager(
    override val setup: DialogGDPR
) : BaseMaterialViewManager<DialogGDPR, MdfContentGdprBinding>() {

    override val wrapInScrollContainer = false

    private lateinit var location: GDPRLocation
    private lateinit var viewState: ViewState
    private val pages: MutableList<LinearLayout> = ArrayList()
    private var snackbar: Snackbar? = null

    override fun onCreateContentViewBinding(
        layoutInflater: LayoutInflater,
        parent: ViewGroup?,
        attachToParent: Boolean
    ) = MdfContentGdprBinding.inflate(layoutInflater, parent, attachToParent)

    override fun initBinding(savedInstanceState: Bundle?) {

        viewState = MaterialDialogUtil.getViewState(savedInstanceState) ?: ViewState()

        presenter.requireLifecycleOwner()
            .lifecycleScope
            .launch(Dispatchers.IO) {
                presenter.requireLifecycleOwner().repeatOnLifecycle(Lifecycle.State.STARTED) {
                    val data = DataUtil.load(presenter.requireContext(), setup.setup, DataUtil.DataType.Location)
                    location = data.location
                    viewState = viewState.copy(currentStep = 0)
                    withContext(Dispatchers.Main) {
                        updateSelectedPage()
                    }
                }
            }

        // Pages List
        pages.add(binding.llPage0)
        pages.add(binding.llPage1)
        pages.add(binding.llPage2)
        pages.add(binding.llPageLoading)

        // general page
        initGeneralTexts()
        initButtons()

        // info page
        initInfoTexts()

        // selected page
        updateSelectedPage()

        // ------------------
        // Step 0 - general page
        // ------------------

        binding.dialogBottom.btAgree.setOnClickListener { view: View ->
            if (!isAgeValid(view, true) || !isAllConsentGiven(view, true)) {
                return@setOnClickListener
            }
            viewState = viewState.copy(selectedConsent = GDPRConsent.PERSONAL_CONSENT)
            onFinish()
        }
        binding.dialogBottom.btDisagree.setOnClickListener { view: View ->
            if (!isAgeValid(view, false) || !isAllConsentGiven(view, false)) {
                return@setOnClickListener
            }
            if (setup.setup.hasPaidVersion) {
                if (setup.setup.allowNonPersonalisedForPaidVersion) {
                    if (setup.setup.explicitNonPersonalisedConfirmation) {
                        viewState = viewState.copy(currentStep = 2)
                        updateSelectedPage()
                        return@setOnClickListener
                    }
                    viewState =
                        viewState.copy(selectedConsent = GDPRConsent.NON_PERSONAL_CONSENT_ONLY)
                    onFinish()
                } else {
                    viewState = viewState.copy(selectedConsent = GDPRConsent.NO_CONSENT)
                    onFinish()
                }
            } else {
                if (setup.setup.explicitNonPersonalisedConfirmation) {
                    viewState = viewState.copy(currentStep = 2)
                    updateSelectedPage()
                    return@setOnClickListener
                }
                viewState = viewState.copy(selectedConsent = GDPRConsent.NON_PERSONAL_CONSENT_ONLY)
                onFinish()
            }
        }
        if (!setup.setup.allowAnyNoConsent()) {
            binding.dialogBottom.btNoConsentAtAll.visibility = View.GONE
        } else {
            binding.dialogBottom.btNoConsentAtAll.setOnClickListener { view: View? ->
                viewState = viewState.copy(selectedConsent = GDPRConsent.NO_CONSENT)
                onFinish()
            }
        }

        // ------------------
        // Step 1 - info page
        // ------------------

        binding.btBack.setOnClickListener { view: View ->
            viewState = viewState.copy(currentStep = 0)
            updateSelectedPage()
        }

        // ------------------
        // Step 2 - explicit non personalised consent page
        // ------------------

        binding.btAgreeNonPersonalised.setOnClickListener { view: View ->
            viewState = viewState.copy(selectedConsent = GDPRConsent.NON_PERSONAL_CONSENT_ONLY)
            onFinish()
        }
    }

    override fun onDestroy() {
        snackbar?.dismiss()
        snackbar = null
        pages.clear()
        super.onDestroy()
    }

    override fun saveViewState(outState: Bundle) {
        MaterialDialogUtil.saveViewState(
            outState,
            viewState
        )
    }

    override fun onBackPress(): Boolean {
        if (handleBackPress()) {
            return true
        }
        if (setup.setup.forceSelection && viewState.selectedConsent == null) {
            return true
        }
        return false
    }

    private fun onFinish() {
        viewState.selectedConsent?.let {
            val consentState = GDPRConsentState(context, it, location)
            GDPR.setConsent(presenter.requireContext(), setup.setup, consentState)
            val eventManager = setup.eventManager as GDPREventManager
            eventManager.sendEvent(presenter, consentState)
        }
        presenter.dismiss?.invoke()
    }

    private fun initGeneralTexts() {

        val question = setup.setup.customTexts.question
        val mainMsg = setup.setup.customTexts.mainMsg
        val topMsg = setup.setup.customTexts.topMsg
        val ageMsg = setup.setup.customTexts.ageMsg

        // 1) Question
        if (question == null || question.isEmpty(presenter.requireContext())) {
            val question = presenter.requireContext().getString(
                R.string.gdpr_dialog_question,
                if (setup.setup.containsAdNetwork() && !setup.setup.shortQuestion) presenter.requireContext()
                    .getString(R.string.gdpr_dialog_question_ads_info) else ""
            )
            binding.tvQuestion.text =
                HtmlCompat.fromHtml(question, HtmlCompat.FROM_HTML_MODE_LEGACY)
        } else {
            binding.tvQuestion.text = question.get(presenter.requireContext())
        }

        // 2) Text Top
        if (topMsg == null || topMsg.isEmpty(presenter.requireContext())) {
            val cheapOrFree =
                presenter.requireContext()
                    .getString(if (setup.setup.hasPaidVersion) R.string.gdpr_cheap else R.string.gdpr_free)
            var text1 = presenter.requireContext().getString(R.string.gdpr_dialog_text1_part1)
            if (setup.setup.showPaidOrFreeInfoText) {
                text1 += " " + presenter.requireContext()
                    .getString(R.string.gdpr_dialog_text1_part2, cheapOrFree)
            }
            binding.tvText1.text = HtmlCompat.fromHtml(text1, HtmlCompat.FROM_HTML_MODE_LEGACY)
        } else {
            binding.tvText1.text = topMsg.get(presenter.requireContext())
        }
        binding.tvText1.movementMethod = LinkMovementMethod.getInstance()

        // 3) Text Main
        if (mainMsg == null || mainMsg.isEmpty(presenter.requireContext())) {
            val typesCount = setup.setup.getNetworkTypes(presenter.requireContext()).size
            val types = setup.setup.getNetworkTypesCommaSeperated(presenter.requireContext())
            val text2 = if (typesCount == 1) presenter.requireContext().getString(
                R.string.gdpr_dialog_text2_singular,
                types
            ) else presenter.requireContext().getString(R.string.gdpr_dialog_text2_plural, types)
            val sequence2: CharSequence = Html.fromHtml(text2)
            val strBuilder2 = SpannableStringBuilder(sequence2)
            val urls = strBuilder2.getSpans(0, sequence2.length, URLSpan::class.java)
            for (span in urls) {
                makeLinkClickable(strBuilder2, span) {
                    viewState = viewState.copy(currentStep = 1)
                    updateSelectedPage()
                }
            }
            binding.tvText2.text = strBuilder2
        } else {
            binding.tvText2.text = HtmlCompat.fromHtml(
                mainMsg.getString(presenter.requireContext()),
                HtmlCompat.FROM_HTML_MODE_LEGACY
            )
        }
        binding.tvText2.movementMethod = LinkMovementMethod.getInstance()

        // 4) Text Age
        if (ageMsg == null || ageMsg.isEmpty(presenter.requireContext())) {
            val text3 = presenter.requireContext().getString(R.string.gdpr_dialog_text3)
            binding.tvText3.text = HtmlCompat.fromHtml(text3, HtmlCompat.FROM_HTML_MODE_LEGACY)
        } else {
            binding.tvText3.text = ageMsg.get(presenter.requireContext())
        }
        binding.tvText3.movementMethod = LinkMovementMethod.getInstance()

        // 5) Age Confirmation
        if (!setup.setup.explicitAgeConfirmation) {
            binding.cbAge.visibility = View.GONE
        } else {
            binding.tvText3.visibility = View.GONE
            binding.cbAge.isChecked = viewState.ageConfirmed
            binding.cbAge.setOnCheckedChangeListener { buttonView: CompoundButton?, isChecked: Boolean ->
                viewState = viewState.copy(ageConfirmed = isChecked)
            }
        }

        //GDPRUtils.justify(tvText1);
        justifyText(binding.tvText2)
        //GDPRUtils.justify(tvText3);
        //GDPRUtils.justify(tvQuestion);
    }

    private fun initInfoTexts() {
        val textInfo2 = setup.setup.getNetworksCommaSeperated(presenter.requireContext(), true)
        binding.tvServiceInfo2.text = Html.fromHtml(textInfo2)
        binding.tvServiceInfo2.movementMethod = LinkMovementMethod.getInstance()
        val policyLink = setup.setup.getFullPolicyLink()
        val privacyPolicyPart =
            if (policyLink == null) "" else presenter.requireContext().getString(
                R.string.gdpr_dialog_text_info3_privacy_policy_part,
                policyLink
            )
        val textInfo3 =
            presenter.requireContext().getString(R.string.gdpr_dialog_text_info3, privacyPolicyPart)
        binding.tvServiceInfo3.text =
            HtmlCompat.fromHtml(textInfo3, HtmlCompat.FROM_HTML_MODE_LEGACY)
        binding.tvServiceInfo3.movementMethod = LinkMovementMethod.getInstance()
    }

    private fun initButtons() {
        if (setup.setup.hasPaidVersion) {
            if (!setup.setup.allowNonPersonalisedForPaidVersion) {
                binding.dialogBottom.btDisagree.setText(R.string.gdpr_dialog_disagree_buy_app)
            } else {
                binding.dialogBottom.btNoConsentAtAll.setText(R.string.gdpr_dialog_disagree_buy_app)
            }
        }
        var hideAdsInfo = !setup.setup.containsAdNetwork()
        if (setup.setup.hasPaidVersion) {
            if (!setup.setup.allowNonPersonalisedForPaidVersion) {
                binding.dialogBottom.btDisagree.setText(R.string.gdpr_dialog_disagree_buy_app)
                hideAdsInfo = true
            }
        }
        if (!hideAdsInfo) {
            val textButton =
                presenter.requireContext().getString(R.string.gdpr_dialog_disagree_no_thanks)
                    .uppercase(Locale.getDefault())
                    .trimIndent()
            val textInfo = presenter.requireContext().getString(R.string.gdpr_dialog_disagree_info)

            binding.dialogBottom.btDisagree.text = textButton
            binding.dialogBottom.btDisagreeSubText.visibility = View.VISIBLE
            binding.dialogBottom.btDisagreeSubText.text = textInfo
        } else {
            binding.dialogBottom.btDisagreeSubText.visibility = View.GONE
        }
    }

    private fun updateSelectedPage() {
        for (i in pages.indices) {
            pages[i].visibility =
                if (i == viewState.currentStep) View.VISIBLE else View.GONE
        }
        if (snackbar?.isShown == true) {
            snackbar?.dismiss()
            snackbar = null
        }
    }

    private fun isAgeValid(view: View, agree: Boolean): Boolean {
        if (setup.setup.explicitAgeConfirmation) {
            // we only need to check age for personalised ads
            if (agree && !viewState.ageConfirmed) { // || (!agree && mSetup.hasPaidVersion() && mSetup.allowNonPersonalisedForPaidVersion())) {
                showSnackbar(R.string.gdpr_age_not_confirmed, view)
                return false
            }
        }
        return true
    }

    private fun isAllConsentGiven(view: View, agree: Boolean): Boolean {
        return true
    }

    private fun showSnackbar(message: Int, view: View) {
        // Toast.makeText(view.context, message, Toast.LENGTH_LONG).show()
        snackbar = Snackbar
            .make(view, message, Snackbar.LENGTH_LONG)
        snackbar?.show()
    }

    private fun handleBackPress(): Boolean {
        return if (viewState.currentStep > 0) {
            viewState = viewState.copy(currentStep = 0)
            updateSelectedPage()
            true
        } else {
            false
        }
    }

    private fun makeLinkClickable(
        sb: SpannableStringBuilder,
        span: URLSpan,
        runnable: Runnable
    ) {
        val start = sb.getSpanStart(span)
        val end = sb.getSpanEnd(span)
        val flags = sb.getSpanFlags(span)
        val clickable: ClickableSpan = object : ClickableSpan() {
            override fun onClick(view: View) {
                runnable.run()
            }
        }
        sb.setSpan(clickable, start, end, flags)
        sb.removeSpan(span)
    }

    private fun justifyText(textView: TextView) {
        // does not work good enough, check out this: https://github.com/MFlisar/GDPRDialog/issues/21
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            textView.setJustificationMode(Layout.JUSTIFICATION_MODE_INTER_WORD);
//        } else
        // does not work good enough either, check out this: https://github.com/MFlisar/GDPRDialog/issues/43
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            // not perfect, but better than nothing
//            textView.setBreakStrategy(Layout.BREAK_STRATEGY_BALANCED);
//            // wrap content is not working with this strategy, so we wait for the layout
//            // and find the longest line and use it's width for the textview and then center the layout
//            textView.post(() -> {
//                LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams)textView.getLayoutParams();
//                lp.width = (int)getMaxLineWidth(textView); //LinearLayout.LayoutParams.WRAP_CONTENT;
//                lp.gravity = Gravity.CENTER_HORIZONTAL;
//                textView.setLayoutParams(lp);
//            });
//        } else {
//            // sorry, not supported...
//        }
    }

    private fun getMaxLineWidth(textView: TextView): Float {
        val layout = textView.layout
        var max_width = 0.0f
        val lines = layout.lineCount
        for (i in 0 until lines) {
            if (layout.getLineWidth(i) > max_width) {
                max_width = layout.getLineWidth(i)
            }
        }
        return max_width
    }

    // -----------
    // State
    // -----------

    @Parcelize
    data class ViewState(
        val currentStep: Int = 3,
        val selectedConsent: GDPRConsent? = null,
        val ageConfirmed: Boolean = false,
        val explicitlyConfirmedServices: List<Int> = emptyList()
    ) : Parcelable
}