package com.michaelflisar.dialogs

import android.graphics.Color
import android.os.Bundle
import android.os.Parcelable
import android.text.InputType
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.Gravity
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.michaelflisar.dialogs.animations.MaterialDialogRevealAnimation
import com.michaelflisar.dialogs.app.R
import com.michaelflisar.dialogs.app.databinding.ActivityMainBinding
import com.michaelflisar.dialogs.apps.AppsManager
import com.michaelflisar.dialogs.classes.*
import com.michaelflisar.dialogs.interfaces.IMaterialDialogAnimation
import com.michaelflisar.dialogs.interfaces.IMaterialDialogEvent
import com.michaelflisar.dialogs.items.DemoItem
import com.michaelflisar.dialogs.items.HeaderItem
import com.michaelflisar.dialogs.presenters.DialogStyle
import com.michaelflisar.dialogs.presenters.showAlertDialog
import com.michaelflisar.lumberjack.L
import com.michaelflisar.text.Text
import com.michaelflisar.text.asText
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.IItem
import com.mikepenz.fastadapter.adapters.ItemAdapter
import com.mikepenz.fastadapter.select.getSelectExtension
import kotlinx.coroutines.*
import kotlinx.parcelize.Parcelize

class MainActivity : AppCompatActivity() {

    companion object {
        private val STYLES =
            listOf("AlertDialog", "DialogFragment", "BottomSheetFragment", "FullscreenFragment")
    }

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 1) init views
        initViews(savedInstanceState)

        // 2) init adapter with demo items and add items
        val itemAdapter = initRecyclerView()
        addInfoDialogItems(itemAdapter)
        addInputDialogItems(itemAdapter)
        addListDialogItems(itemAdapter)
        addNumberDialogItems(itemAdapter)
        addProgressDialogItems(itemAdapter)
        //addFastAdapterDialogItems(itemAdapter)
        addColorDialogItems(itemAdapter)
        addDateTimeDialogItems(itemAdapter)
        //addFrequencyDialogItems(itemAdapter)
        addDebugDialogItems(itemAdapter)
        //addAdsDialogItems(itemAdapter)

        // 3) listen to dialog events
        addListeners()
    }


    private fun addListeners() {

        // -----------------
        // DEFAULT USAGE - register a listener for the desired event class
        // -----------------

        onMaterialDialogEvent<DialogInfo.Event> { event ->
            showToast(event)
        }
        onMaterialDialogEvent<DialogInput.Event> { event ->
            showToast(event)
        }
        onMaterialDialogEvent<DialogList.Event> { event ->
            showToast(event)
        }
        onMaterialDialogEvent<DialogNumber.EventInt> { event ->
            showToast(event)
        }
        onMaterialDialogEvent<DialogNumber.EventFloat> { event ->
            showToast(event)
        }
        onMaterialDialogEvent<DialogDateTime.EventDateTime> { event ->
            showToast(event)
        }
        onMaterialDialogEvent<DialogDateTime.EventDate> { event ->
            showToast(event)
        }
        onMaterialDialogEvent<DialogDateTime.EventTime> { event ->
            showToast(event)
        }
        onMaterialDialogEvent<DialogColor.Event> { event ->
            showToast(event)
        }
        onMaterialDialogEvent<DialogDebug.Event> { event ->
            //showToast(event)
            DebugDialogData.onDebugEvent(this, event)
        }
        onMaterialDialogEvent<DialogProgress.Event> { event ->
            showToast(event)
        }

        // if desired, you can directly pass in the ID of the event you want to observe
        onMaterialDialogEvent<DialogInfo.Event>(101) { event ->
            L.d { "DialogInfo.Event received [ID = 101]: $event" }
        }

        // -----------------
        // TESTS
        // -----------------

        // this one will receive ALL events, because [MaterialDialogEvent] is the base class for all events
        onMaterialDialogEvent<IMaterialDialogEvent> { event ->
            L.tag("ALL-EVENTS").d { "MaterialDialogEvent received: $event" }
        }
        // this one will receive all [DialogNumber] events of any type (Int, Long, Float, Double)...
        onMaterialDialogEvent<DialogNumber.Event<*>> { event ->
            // if desired, you can distinct the events easily here, because there's a sealed interface used
            when (event) {
                is DialogNumber.EventDouble.Action -> {}
                is DialogNumber.EventDouble.Result -> {}
                is DialogNumber.EventFloat.Action -> {}
                is DialogNumber.EventFloat.Result -> {}
                is DialogNumber.EventInt.Action -> {}
                is DialogNumber.EventInt.Result -> {}
                is DialogNumber.EventLong.Action -> {}
                is DialogNumber.EventLong.Result -> {}
            }
            L.d { "DialogNumber.Event received: $event" }
        }
    }

    // -------------------
    // demo activity
    // -------------------

    private fun initViews(savedInstanceState: Bundle?) {
        val state = savedInstanceState?.getParcelable<State>("viewState")

        // Style Spinner
        val arrayAdapter = NoFilterArrayAdapter(
            this,
            R.layout.support_simple_spinner_dropdown_item,
            STYLES
        )
        binding.actvStyle.setAdapter(arrayAdapter)
        binding.actvStyle.setText(state?.style ?: STYLES[1], false)

        // Style Spinner
        val arrayAdapter2 = NoFilterArrayAdapter(
            this,
            R.layout.support_simple_spinner_dropdown_item,
            MaterialDialogTitleStyle.values().map { it.name }
        )
        binding.actvTitleStyle.setAdapter(arrayAdapter2)
        binding.actvTitleStyle.setText(
            state?.titleStyle ?: MaterialDialogTitleStyle.LargeTextWithIconCentered.name, false
        )

        // Theme Toggle Group
        binding.mbTheme.addOnButtonCheckedListener { group, checkedId, isChecked ->
            if (isChecked) {
                when (checkedId) {
                    R.id.btThemeAuto -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                    R.id.btThemeDark -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    R.id.btThemeLight -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                }
            }
        }

        state?.let {
            binding.mbTheme.check(it.theme)
            binding.cbCustomAnimation.isChecked = it.customAnimation
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable(
            "viewState", State(
                binding.mbTheme.checkedButtonId,
                binding.actvStyle.text.toString(),
                binding.actvTitleStyle.text.toString(),
                binding.cbCustomAnimation.isChecked
            )
        )
    }

    // -------------------
    // helper functions for content setup
    // -------------------

    private fun initRecyclerView(): ItemAdapter<IItem<*>> {
        val itemAdapter = ItemAdapter<IItem<*>>()
        val adapter = FastAdapter.with(itemAdapter)

        val selectExtension = adapter.getSelectExtension()
        selectExtension.isSelectable = true

        adapter.onClickListener = { v, _, item, _ ->
            if (item is DemoItem) {
                item.function(v!!)
            }
            true
        }
        binding.rvDemoItems.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        binding.rvDemoItems.adapter = adapter
        return itemAdapter
    }

    // -------------------
    // helper functions for demo
    // -------------------

    private fun addInfoDialogItems(adapter: ItemAdapter<IItem<*>>) {
        adapter.add(
            HeaderItem("INFO DEMOS"),
            DemoItem("Short Info", "Show a simple short info dialog") {
                DialogInfo(
                    id = 101,
                    title = "Info Title".asText(),
                    icon = R.drawable.ic_baseline_info_24.asMaterialDialogIcon(),
                    text = "Some info text...".asText(),
                    cancelable = isCancelable(),
                    //buttonPositive = Text.Empty
                )
                    .showInCorrectMode(this, it)
            },
            DemoItem("Short Info", "Show a simple short info dialog with a spannable text") {
                DialogInfo(
                    id = 102,
                    title = "Info Title".asText(),
                    "https://cdn-icons-png.flaticon.com/512/134/134808.png".asMaterialDialogIcon(), // this only works with a custom image loader, but we do use one in this demo
                    text = SpannableString("Here is some important information:\n\nSpannable strings are supported as well!").also {
                        it.setSpan(
                            ForegroundColorSpan(Color.RED),
                            13,
                            22,
                            Spanned.SPAN_INCLUSIVE_INCLUSIVE
                        )
                    }.asText(),
                    cancelable = isCancelable()
                )
                    .showInCorrectMode(this, it)
            },
            DemoItem("Long Info", "Show a simple long info dialog") {
                DialogInfo(
                    id = 103,
                    title = "Info Title".asText(),
                    text = R.string.lorem_ipsum_long.asText(),
                    buttonPositive = "Accept".asText(),
                    buttonNegative = "Decline".asText(),
                    cancelable = isCancelable()
                )
                    .showInCorrectMode(this, it)
            },
            DemoItem("Short Info", "No title with icon only") {
                DialogInfo(
                    id = 104,
                    title = Text.Empty,
                    "https://cdn-icons-png.flaticon.com/512/134/134808.png".asMaterialDialogIcon(), // this only works with a custom image loader, but we do use one in this demo
                    text = "Here's some short information".asText(),
                    cancelable = isCancelable()
                )
                    .showInCorrectMode(this, it)
            },
            DemoItem("Info with menu", "Simple Dialog with a menu") {
                DialogInfo(
                    id = 105,
                    title = "Dialog Menu".asText(),
                    text = "Here's some short information".asText(),
                    menu = R.menu.menu_demo,
                    cancelable = isCancelable()
                )
                    .showInCorrectMode(this, it)
            },
            //DemoItem(
            //    "Info demo with timeout",
            //    "Show a info dialog with an ok button that will only be enabled after 10s and which shows a colored warning message"
            //) {
            //    DialogInfo(
            //        11,
            //        "Info Title".asText(),
            //        "Some info about a dangerous action + 10s timeout until the ok button can be clicked.\nRotate me and I'll remember the already past by time.".asText(),
            //        warning = "Attention: Dangerous action!".asText(),
            //        warningSeparator = "\n\n",
            //        cancelable = false,
            //        timerPosButton = 10,
            //        
            //    )
            //        .show(this)
            //},
        )
    }

    private fun addInputDialogItems(adapter: ItemAdapter<IItem<*>>) {
        adapter.add(
            HeaderItem("INPUT DEMOS"),
            DemoItem(
                "Input Demo 1",
                "Show a dialog with an input field and a hint and allow an empty input"
            ) {
                DialogInput(
                    id = 201,
                    title = "Insert your name".asText(),
                    icon = R.drawable.ic_baseline_text_snippet_24.asMaterialDialogIcon(),
                    input = DialogInput.Input.Single(
                        hint = "E.g. Max Musterman".asText(),
                        value = Text.Empty,
                    ),
                    cancelable = isCancelable()
                )
                    .showInCorrectMode(this, it)
            },
            DemoItem(
                "Input Demo 2",
                "Show a dialog with an input field and a hint AND some description AND disallow an empty input"
            ) {
                DialogInput(
                    id = 202,
                    title = "Insert your name".asText(),
                    description = "Please insert your full name here.".asText(),
                    input = DialogInput.Input.Single(
                        hint = "E.g. Max Musterman".asText(),
                        value = "Name".asText(),
                        validator = DialogInput.TextValidator(minLength = 1)
                    ),
                    selectAllOnFocus = true,
                    cancelable = isCancelable(),
                )
                    .showInCorrectMode(this, it)
            },
            DemoItem(
                "Input Demo 3",
                "Show a dialog with an input field and a hint AND input type is positive number and must be 1 digit at least - result will still be a STRING in this case!"
            ) {
                DialogInput(
                    id = 203,
                    title = "Number".asText(),
                    description = "Please insert a number".asText(),
                    input = DialogInput.Input.Single(
                        inputType = InputType.TYPE_CLASS_NUMBER, // should match the validator, everything else makes no sense...
                        value = Text.Empty,
                        validator = DialogInput.NumberValidator(
                            min = 1,
                            max = 100
                        )// DialogInput.createSimpleValidator(1),
                    ),
                    cancelable = isCancelable()
                )
                    .showInCorrectMode(this, it)
            },
            DemoItem(
                "Input Demo 4",
                "Show a dialog with an 3 input fields + selectAllOnFocus is"
            ) {
                DialogInput(
                    id = 204,
                    title = "Multiple Inputs".asText(),
                    description = "Please insert 3 random strings".asText(),
                    input = DialogInput.Input.Multi(
                        listOf(
                            DialogInput.Input.Single(hint = "Value 1".asText()),
                            DialogInput.Input.Single(hint = "Value 2".asText()),
                            DialogInput.Input.Single(hint = "Value 3".asText()),
                        )
                    ),
                    selectAllOnFocus = true,
                    cancelable = isCancelable()
                )
                    .showInCorrectMode(this, it)
            },
            DemoItem(
                "Input Demo 5",
                "Show a dialog with an input field with prefix and suffix (with number type and no hint)"
            ) {
                DialogInput(
                    id = 205,
                    title = "Weight Value".asText(),
                    input = DialogInput.Input.Single(
                        inputType = InputType.TYPE_CLASS_NUMBER,
                        value = "50".asText(),
                        prefix = "Value".asText(),
                        suffix = "kg".asText(),
                        gravity = Gravity.CENTER_HORIZONTAL
                    ),
                    selectAllOnFocus = true,
                    cancelable = isCancelable()
                )
                    .showInCorrectMode(this, it)
            }
        )
    }

    private fun addListDialogItems(adapter: ItemAdapter<IItem<*>>) {
        val listItems1 = DialogList.createList(
            List(50) { "Item ${it + 1}" }.map { it.asText() }
        )
        val listItems2 = DialogList.Items.List(
            List(50) { "Item ${it + 1}" }
                .mapIndexed { index, s ->
                    DialogList.SimpleListItem(
                        index.toLong(),
                        s.asText(),
                        resIcon = R.mipmap.ic_launcher
                    )
                }
        )
        val listItems3 = DialogList.Items.Loader(AppsManager)

        adapter.add(
            HeaderItem("LIST DEMOS"),
            DemoItem("List Demo 1", "Show a dialog with a list of items - SINGLE SELECT") {
                DialogList(
                    301,
                    "Single Select".asText(),
                    icon = R.drawable.ic_baseline_list_24.asMaterialDialogIcon(),
                    items = listItems1,
                    description = "Select a single item...".asText(),
                    selectionMode = DialogList.SelectionMode.SingleSelect(0),
                    cancelable = isCancelable()
                )
                    .showInCorrectMode(this, it)
            },
            DemoItem("List Demo 2", "Show a dialog with a list of items - MULTI SELECT") {
                DialogList(
                    302,
                    "Multi Select".asText(),
                    items = listItems1,
                    description = "Select multiple items...".asText(),
                    selectionMode = DialogList.SelectionMode.MultiSelect(),
                    cancelable = isCancelable()
                )
                    .showInCorrectMode(this, it)
            },
            DemoItem("List Demo 3", "Show a dialog with a list of items - SINGLE CLICK + Icons") {
                DialogList(
                    303,
                    "Single Click".asText(),
                    items = listItems2,
                    description = "Select a single items - the first click will emit a single event and close this dialog directly...".asText(),
                    selectionMode = DialogList.SelectionMode.SingleClick,
                    cancelable = isCancelable()
                )
                    .showInCorrectMode(this, it)
            },
            DemoItem("List Demo 4", "Show a dialog with a list of items - MULTI CLICK + Icons") {
                DialogList(
                    304,
                    "Multi Click".asText(),
                    items = listItems2,
                    description = "Select multiple items, each click will emit a single event...".asText(),
                    selectionMode = DialogList.SelectionMode.MultiClick,
                    cancelable = isCancelable()
                )
                    .showInCorrectMode(this, it)
            },
            DemoItem(
                "List Demo 5",
                "Show a dialog with a custom async item provider (installed apps) + filtering + unselect filtered items automatically + preselect first 3 items"
            ) {
                DialogList(
                    305,
                    "Multi Select".asText(),
                    items = listItems3,
                    selectionMode = DialogList.SelectionMode.MultiSelect(
                        initialSelection = sortedSetOf(0, 1, 2)
                    ),
                    filter = DialogList.Filter(
                        searchInText = true,
                        searchInSubText = true,
                        highlight = true, // highlights search term in items
                        algorithm = DialogList.Filter.Algorithm.String, // either search for items containing all words or the search term as a whole
                        ignoreCase = true,
                        unselectInvisibleItems = true // true means, items are unselected as soon as they are filtered out and get invisible for the user
                    ),
                    infoFormatter = DialogList.Formatter("Selected"),
                    cancelable = isCancelable()
                )
                    .showInCorrectMode(this, it)
            }
        )
    }

    private fun addNumberDialogItems(adapter: ItemAdapter<IItem<*>>) {
        adapter.add(
            HeaderItem("NUMBER DEMOS"),
            DemoItem(
                "Number Demo 1",
                "Show a dialog that allows selecting an integer value in the range [0, 100]"
            ) {
                DialogNumber<Int>(
                    401,
                    "Age".asText(),
                    description = "Select a value between 0 and 100".asText(),
                    input = DialogNumber.Input.Single<Int>(
                        18,
                        0,
                        100,
                        1
                    ),
                    cancelable = isCancelable()
                )
                    .showInCorrectMode(this, it)
            },
            DemoItem(
                "Number Demo 2",
                "Show a dialog that allows selecting an integer value in the range [0, 100] in steps of 5 + value formatter"
            ) {
                DialogNumber<Int>(
                    402,
                    "Value".asText(),
                    description = "Select a value between 0 and 100 in steps of 5".asText(),
                    input = DialogNumber.Input.Single<Int>(
                        value = 50,
                        min = 0,
                        max = 100,
                        step = 5,
                        formatter = DialogNumber.Formatter<Int>(R.string.custom_int_formatter)
                    ),
                    cancelable = isCancelable()
                )
                    .showInCorrectMode(this, it)
            },
            DemoItem(
                "Number Demo 3",
                "Show a dialog that allows selecting a float value in the range [0, 10] in steps of 0.5"
            ) {
                DialogNumber<Float>(
                    403,
                    "Value".asText(),
                    description = "Select a value between 0 and 10 in steps of 0.5".asText(),
                    input = DialogNumber.Input.Single<Float>(
                        value = 5f,
                        min = 0f,
                        max = 10f,
                        step = 0.5f
                    ),
                    cancelable = isCancelable()
                )
                    .showInCorrectMode(this, it)
            },
            DemoItem(
                "Number Demo 4",
                "Show a dialog with multiple numbers"
            ) {
                DialogNumber<Int>(
                    404,
                    "Value".asText(),
                    description = "Select 3 integer values".asText(),
                    input = DialogNumber.Input.Multi<Int>(
                        listOf(
                            DialogNumber.Input.Single<Int>(10, hint = "Value 1".asText()),
                            DialogNumber.Input.Single<Int>(20, hint = "Value 2".asText()),
                            DialogNumber.Input.Single<Int>(30, hint = "Value 3".asText())
                        )
                    ),
                    cancelable = isCancelable()
                )
                    .showInCorrectMode(this, it)
            }
        )
    }

    private fun addProgressDialogItems(adapter: ItemAdapter<IItem<*>>) {
        adapter.add(
            HeaderItem("PROGRESS DEMOS"),
            DemoItem("Progress demo", "Show a progress dialog for 5s") {
                DialogProgress(
                    50,
                    title = "Loading".asText(),
                    text = "Data is loading...".asText(),
                    buttonNegative = "Cancel".asText(),
                    dismissOnNegative = false,
                    horizontal = true
                )
                    .showInCorrectMode(this, it)

                // simple unsafe method to immitate some background process that runs 5 seconds and updates the progress every second...
                var c = 0
                GlobalScope.launch(Dispatchers.IO) {
                    while (c <= 5) {
                        delay(1000L)
                        withContext(Dispatchers.Main) {
                            DialogProgress.update(50, "Time left: ${5 - c}s".asText())
                        }
                        c++
                    }
                    DialogProgress.dismiss(50)
                }
            }
        )
    }

    /*
            private fun addFastAdapterDialogItems(adapter: ItemAdapter<IItem<*>>) {
                adapter.add(
                    HeaderItem("Fast adapter DEMOS"),
                    DemoItem(
                        "Installed apps",
                        "Show a list of all installed apps in a fast adapter list dialog + enable filtering via custom predicate"
                    ) {
                        DialogFastAdapter(
                            60,
                            AllAppsFastAdapterHelper.ItemProvider,
                            "Select an app".asText(),
                            selectionMode = DialogFastAdapter.SelectionMode.SingleClick,
                            filterPredicate = AllAppsFastAdapterHelper.FilterPredicate,

                        )
                            .show(this)
                    }
                )
            }
    */
    private fun addColorDialogItems(adapter: ItemAdapter<IItem<*>>) {
        adapter.add(
            HeaderItem("COLOR DEMOS"),
            DemoItem("Color Demo 1", "Show a color dialog") {
                DialogColor(
                    701,
                    "Select color".asText(),
                    color = Color.BLUE,
                    alphaAllowed = false
                )
                    .showInCorrectMode(this, it)
            },
            DemoItem(
                "Color Demo 2",
                "Show a color dialog - with possiblility to select an alpha value"
            ) {
                DialogColor(
                    702,
                    "Select color".asText(),
                    color = Color.RED,
                    alphaAllowed = true
                )
                    .showInCorrectMode(this, it)
            }
        )
    }

    private fun addDateTimeDialogItems(adapter: ItemAdapter<IItem<*>>) {
        adapter.add(
            HeaderItem("DATE/TIME DEMOS"),
            DemoItem("Date + Time Demo", "Show a date time dialog") {
                DialogDateTime(
                    801,
                    title = "Date + Time".asText(),
                    timeFormat = DialogDateTime.TimeFormat.H24,
                    dateFormat = DialogDateTime.DateFormat.DDMMYYYY,
                    value = DateTimeData.DateTime.now() // Type is directly deduced from this value class
                )
                    .showInCorrectMode(this, it)
            },
            DemoItem("Date Only Demo", "Show a date dialog") {
                DialogDateTime(
                    802,
                    title = "Date".asText(),
                    timeFormat = DialogDateTime.TimeFormat.H24,
                    dateFormat = DialogDateTime.DateFormat.DDMMYYYY,
                    value = DateTimeData.Date.now() // Type is directly deduced from this value class
                )
                    .showInCorrectMode(this, it)
            },
            DemoItem("Time Only Demo", "Show a time dialog") {
                DialogDateTime(
                    803,
                    title = "Time".asText(),
                    timeFormat = DialogDateTime.TimeFormat.H24,
                    value = DateTimeData.Time(
                        20,
                        0
                    ) // Type is directly deduced from this value class
                )
                    .showInCorrectMode(this, it)
            }
        )
    }

    /*
        private fun addFrequencyDialogItems(adapter: ItemAdapter<IItem<*>>) {
            adapter.add(
                HeaderItem("FREQUENCY DEMOS"),
                DemoItem("Frequency demo", "Show a frequency dialog") {
                    DialogFrequency(
                        90,
                        "Frequency".asText(),

                    )
                        .show(this)
                }
            )
        }
    */
    private fun addDebugDialogItems(adapter: ItemAdapter<IItem<*>>) {
        adapter.add(
            HeaderItem("DEBUGGING SETTINGS demos"),
            DemoItem("Debug settings demo", "Show a custom debug settings dialog") {
                val manager = DebugDialogData.getDebugDataManager()
                DialogDebug(
                    manager = manager,
                    withNumbering = true
                )
                    .showInCorrectMode(this, it)

                // Reading would work like following:
                val isDebugModeEnabled = manager.getBool(DebugDialogData.CHECKBOX_DEBUG_MODE)
                val color = manager.getInt(DebugDialogData.LIST_COLOR)
                val colorEntry = DebugDialogData.LIST_COLOR.getEntryByValue(color)
                val colorName = colorEntry.name
                L.d { "isDebugModeEnabled = $isDebugModeEnabled | color = $color | colorName = $colorName" }
            }
        )
    }
/*
    private fun addAdsDialogItems(adapter: ItemAdapter<IItem<*>>) {

        // this setup makes sure, that only test ads are shown!
        // This means we do not need valid ad ids in this demo either
        val TEST_SETUP = DialogAds.TestSetup()
        // this is the sample google ad mob app id for test ads - the same is defined in this apps manifest
        // should be your real app id in a real app of course
        val appId = R.string.sample_admob_app_id
        // we do not need a valid ad id for test ads - the TestSetup will provide the correct ad ids for test ads automatically in this example
        val emptyAdId = ""

        // the policy will automatically handle and update it's state if shouldShow is called
        // for the example we use the show always policy
        val policy = DialogAds.ShowPolicy.Always
        // following policies exist as well:
        // DialogAds.ShowPolicy.OnceDaily
        // DialogAds.ShowPolicy.EveryXTime(5)

        adapter.add(
            HeaderItem("Ad dialog demos"),
            DemoItem(
                "Banner dialog",
                "Shows a simple dialog with a banner - can be closed after 10s"
            ) {
                DialogAds(
                    100,
                    "Ad Banner Dialog".asText(),
                    info = "This dialog will not be shown if you buy the pro version!".asText(),
                    appId = appId.asText(),
                    bannerSetup = DialogAds.BannerSetup(
                        emptyAdId.asText() // this should be the banner ad id in a real app
                    ),
                    testSetup = TEST_SETUP,
                    
                )
                    // Import: Use the show method with a policy for this dialog!!!
                    .show(this, policy)
            },
            DemoItem(
                "Reward dialog",
                "Shows a simple dialog with a button to show a rewarded ad - can be closed after 10s in case the ad can not be loaded"
            ) {
                DialogAds(
                    101,
                    "Ad Reward Dialog".asText(),
                    info = "This dialog will not be shown if you buy the pro version!".asText(),
                    appId = appId.asText(),
                    bigAdSetup = DialogAds.BigAdSetup(
                        emptyAdId.asText(), // this should be the reward ad id in a real app
                        "Show me the ad".asText(),
                        DialogAds.BigAdType.Reward
                    ),
                    testSetup = TEST_SETUP,
                    
                )
                    // Import: Use the show method with a policy for this dialog!!!
                    .show(this, policy)
            },
            DemoItem(
                "Interstitial dialog",
                "Shows a simple dialog with a button to show an interstitial ad - can be closed after 10s in case the ad can not be loaded"
            ) {
                DialogAds(
                    102,
                    "Ad Interstitial Dialog".asText(),
                    info = "This dialog will not be shown if you buy the pro version!".asText(),
                    appId = appId.asText(),
                    bigAdSetup = DialogAds.BigAdSetup(
                        emptyAdId.asText(), // this should be the interstitial ad id in a real app
                        "Show me the ad".asText(),
                        DialogAds.BigAdType.Interstitial
                    ),
                    testSetup = TEST_SETUP,
                    
                )
                    // Import: Use the show method with a policy for this dialog!!!
                    .show(this, policy)
            }
        )

//
    }
*/

    private var toastCounter = 0
    private var toast: Toast? = null

    private fun showToast(event: IMaterialDialogEvent) {
        toastCounter++
        val dialogClass =
            event.javaClass.name.replace("com.michaelflisar.dialogs.", "").substringBefore("$")
        val msg = "Event #$toastCounter - $dialogClass\n$event"
        toast?.cancel()
        toast = LongToast.makeText(this@MainActivity, msg, Toast.LENGTH_LONG)
        toast?.show()
    }

    private fun isCancelable(): Boolean = true

    private fun getAnimation(view: View): IMaterialDialogAnimation? {
        // MaterialDialogRevealAnimation(250L)... reveal from dialog center
        // or following to reveal from a views center:
        // (same function and logic is valid for MaterialDialogFadeScaleAnimation)
        return if (binding.cbCustomAnimation.isChecked) MaterialDialogRevealAnimation.fromCenter(
            view,
            250L
        ) else null
        // return if (binding.cbCustomAnimation.isChecked) MaterialDialogRevealAnimation(250L) else null
        //return MaterialDialogFadeScaleAnimation.fromCenter(view, 1000L, alphaFrom = 1f)
    }

    private fun <S : MaterialDialogSetup<S>> MaterialDialogSetup<S>.showInCorrectMode(activity: MainActivity, view: View) {
        val index = STYLES.indexOf(binding.actvStyle.text.toString())
        val index2 = MaterialDialogTitleStyle.values().map { it.name }
            .indexOf(binding.actvTitleStyle.text.toString())
        val titleStyle = MaterialDialogTitleStyle.values()[index2]
        // setting a style is optional, there is always a default style applied!
        when (index) {
            0 -> showAlertDialog<S, IMaterialDialogEvent>(activity, DialogStyle(animation = getAnimation(view))) {
                // in AlertDialogs you can handle events directly here as well!
                L.d { "OPTIONAL direct AlertDialog Event Listener: $it" }
            }
            1 -> showDialogFragment(
                activity,
                style = DialogStyle(
                    title = titleStyle,
                    animation = getAnimation(view)
                )
            )
            2 -> showBottomSheetDialogFragment(
                activity,
                BottomSheetDialogStyle(
                    title = titleStyle
                )
            )
            3 -> showFullscreenFragment(
                activity,
                FullscreenDialogStyle()
            )
            else -> RuntimeException("Selected style not handled!")
        }
    }

    @Parcelize
    class State(
        val theme: Int,
        val style: String,
        val titleStyle: String,
        val customAnimation: Boolean
    ) : Parcelable
}
