package com.michaelflisar.dialogs

import android.os.Parcelable
import com.michaelflisar.dialogs.classes.DateTimeData
import com.michaelflisar.dialogs.classes.Icon
import com.michaelflisar.dialogs.datetime.databinding.MdfContentDatetimeBinding
import com.michaelflisar.dialogs.interfaces.IMaterialDialogEvent
import com.michaelflisar.dialogs.interfaces.IMaterialEventManager
import com.michaelflisar.dialogs.interfaces.IMaterialViewManager
import com.michaelflisar.text.Text
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Parcelize
class DialogDateTime<T : DateTimeData>(
    // Key
    override val id: Int?,
    // Header
    override val icon: Icon = Icon.None,
    override val title: Text = Text.Empty,
    // specific fields
    val value: T,
    //val setup: Setup,
    //val min: T? = null,
    //val max: T? = null,
    //val requireFutureDateTime: Boolean = false,
    val is24Hours: Boolean = true,
    // Buttons
    override val buttonPositive: Text = MaterialDialog.defaults.buttonPositive,
    override val buttonNegative: Text = MaterialDialog.defaults.buttonNegative,
    override val buttonNeutral: Text = MaterialDialog.defaults.buttonNeutral,
    // Style
    override val cancelable: Boolean = MaterialDialog.defaults.cancelable,
    // Attached Data
    override val extra: Parcelable? = null
) : MaterialDialogSetup<DialogDateTime<T>, MdfContentDatetimeBinding, DialogDateTime.Event<T>>() {

    @IgnoredOnParcel
    override val viewManager: IMaterialViewManager<DialogDateTime<T>, MdfContentDatetimeBinding> =
        DateTimeViewManager(this)

    @IgnoredOnParcel
    override val eventManager: IMaterialEventManager<DialogDateTime<T>, MdfContentDatetimeBinding> =
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
        interface Result<T> {
            val id: Int?
            val extra: Parcelable?
            val value: T
            //val button: MaterialDialogButton
        }

        interface Cancelled<T> {
            val id: Int?
            val extra: Parcelable?
        }
    }

    sealed class EventDateTime : IMaterialDialogEvent {
        data class Result(
            override val id: Int?,
            override val extra: Parcelable?,
            override val value: DateTimeData.DateTime
        ) : EventDateTime(), Event.Result<DateTimeData.DateTime>

        data class Cancelled(
            override val id: Int?,
            override val extra: Parcelable?
        ) : EventDateTime(), Event.Cancelled<DateTimeData.DateTime>
    }

    sealed class EventDate : IMaterialDialogEvent {
        data class Result(
            override val id: Int?,
            override val extra: Parcelable?,
            override val value: DateTimeData.Date
        ) : EventDate(), Event.Result<DateTimeData.Date>

        data class Cancelled(
            override val id: Int?,
            override val extra: Parcelable?
        ) : EventDate(), Event.Cancelled<DateTimeData.Date>
    }

    sealed class EventTime : IMaterialDialogEvent {
        data class Result(
            override val id: Int?,
            override val extra: Parcelable?,
            override val value: DateTimeData.Time
        ) : EventTime(), Event.Result<DateTimeData.Time>

        data class Cancelled(
            override val id: Int?,
            override val extra: Parcelable?
        ) : EventTime(), Event.Cancelled<DateTimeData.Time>
    }
}