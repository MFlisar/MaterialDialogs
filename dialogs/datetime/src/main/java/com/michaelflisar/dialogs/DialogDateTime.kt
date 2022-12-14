package com.michaelflisar.dialogs

import android.os.Parcelable
import com.michaelflisar.dialogs.classes.Icon
import com.michaelflisar.dialogs.classes.MaterialDialogAction
import com.michaelflisar.dialogs.datetime.databinding.MdfContentDatetimeBinding
import com.michaelflisar.dialogs.interfaces.IMaterialDialogEvent
import com.michaelflisar.dialogs.interfaces.IMaterialEventManager
import com.michaelflisar.dialogs.interfaces.IMaterialViewManager
import com.michaelflisar.text.Text
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import java.text.SimpleDateFormat

@Parcelize
class DialogDateTime<T : DateTimeData>(
    // Key
    override val id: Int? = null,
    // Header
    override val title: Text = Text.Empty,
    override val icon: Icon = Icon.None,
    override val menu: Int? = null,
    // specific fields
    val value: T,
    //val setup: Setup,
    //val min: T? = null,
    //val max: T? = null,
    //val requireFutureDateTime: Boolean = false,
    val timeFormat: TimeFormat = TimeFormat.H24,
    val dateFormat: DateFormat = DateFormat.DDMMYYYY,
    // Buttons
    override val buttonPositive: Text = MaterialDialog.defaults.buttonPositive,
    override val buttonNegative: Text = MaterialDialog.defaults.buttonNegative,
    override val buttonNeutral: Text = MaterialDialog.defaults.buttonNeutral,
    // Style
    override val cancelable: Boolean = MaterialDialog.defaults.cancelable,
    // Attached Data
    override val extra: Parcelable? = null
) : MaterialDialogSetup<DialogDateTime<T>>() {

    @IgnoredOnParcel
    override val viewManager: IMaterialViewManager<DialogDateTime<T>, MdfContentDatetimeBinding> =
        DateTimeViewManager(this)

    @IgnoredOnParcel
    override val eventManager: IMaterialEventManager<DialogDateTime<T>> =
        DateTimeEventManager(this)

    // -----------
    // Result Events
    // -----------

    // Events
    // to allow observing events per Type, we do NOT use a generic class!
    // because of type erasion!
    // => simple solution: create 1 event class per type + a common interface
    // => if someone wants to observe ALL events, the interface can be used!

    sealed interface Event<T : DateTimeData> : IMaterialDialogEvent {
        interface Result<T> : IMaterialDialogEvent {
            val value: T
        }

        interface Action<T> : IMaterialDialogEvent.Action
    }

    sealed class EventDateTime : IMaterialDialogEvent {

        data class Result(
            override val id: Int?,
            override val extra: Parcelable?,
            override val value: DateTimeData.DateTime
        ) : EventDateTime(), Event.Result<DateTimeData.DateTime>

        data class Action(
            override val id: Int?,
            override val extra: Parcelable?,
            override val data: MaterialDialogAction
        ) : EventDateTime(), Event.Action<DateTimeData.DateTime>

    }

    sealed class EventDate : IMaterialDialogEvent {

        data class Result(
            override val id: Int?,
            override val extra: Parcelable?,
            override val value: DateTimeData.Date
        ) : EventDate(), Event.Result<DateTimeData.Date>

        data class Action(
            override val id: Int?,
            override val extra: Parcelable?,
            override val data: MaterialDialogAction
        ) : EventDateTime(), Event.Action<DateTimeData.Date>

    }

    sealed class EventTime : IMaterialDialogEvent {

        data class Result(
            override val id: Int?,
            override val extra: Parcelable?,
            override val value: DateTimeData.Time
        ) : EventTime(), Event.Result<DateTimeData.Time>

        data class Action(
            override val id: Int?,
            override val extra: Parcelable?,
            override val data: MaterialDialogAction
        ) : EventDateTime(), Event.Action<DateTimeData.Time>

    }

    // -----------
    // Enums/Classes
    // -----------

    enum class TimeFormat {
        H12,
        H24
    }

    @Parcelize
    class DateFormat(
        val format1: String,
        val format2: String,
        val format3: String,
        val sep: String,
    ) : Parcelable {

        companion object {
            val DDMMYYYY = DateFormat("dd", "MM", "yyyy", "-")
            val YYYYMMDD = DateFormat("yyyy", "MM", "dd", "-")

            val DDMMYYYY_SLASH = DateFormat("dd", "MM", "yyyy", "/")
            val YYYYMMDD_SLASH = DateFormat("yyyy", "MM", "dd", "/")
        }

        val pattern = "${format1}${sep}${format2}${sep}${format3}"
        val dateFormat = SimpleDateFormat(pattern)
    }

}