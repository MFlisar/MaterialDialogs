package com.michaelflisar.dialogs

import android.os.Parcelable
import com.michaelflisar.dialogs.classes.Icon
import com.michaelflisar.dialogs.classes.MaterialDialogButton
import com.michaelflisar.dialogs.classes.NumberSetup
import com.michaelflisar.dialogs.interfaces.IMaterialDialogEvent
import com.michaelflisar.dialogs.interfaces.IMaterialEventManager
import com.michaelflisar.dialogs.interfaces.IMaterialViewManager
import com.michaelflisar.dialogs.number.databinding.MdfContentNumberBinding
import com.michaelflisar.text.Text
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Parcelize
class DialogNumber<T : Number>(
    // Key
    override val id: Int?,
    // Header
    override val title: Text,
    override val icon: Icon = Icon.None,
    // specific fields
    val value: T,
    val description: Text = Text.Empty,
    val setup: NumberSetup<T> = createDefaultSetup(value),
    //val pickerStyle: Style = Style.Buttons,
    // Buttons
    override val buttonPositive: Text = MaterialDialog.defaults.buttonPositive,
    override val buttonNegative: Text = MaterialDialog.defaults.buttonNegative,
    override val buttonNeutral: Text = MaterialDialog.defaults.buttonNeutral,
    // Style
    override val cancelable: Boolean = MaterialDialog.defaults.cancelable,
    // Attached Data
    override val extra: Parcelable? = null
) : MaterialDialogSetup<DialogNumber<T>, MdfContentNumberBinding, DialogNumber.Event<T>>() {

    companion object {
        fun <T : Number> createDefaultSetup(value: T): NumberSetup<T> {
            return when (value) {
                is Int -> NumberSetup(Int.MIN_VALUE, Int.MAX_VALUE, 1)
                is Long -> NumberSetup(Long.MIN_VALUE, Long.MAX_VALUE, 1L)
                is Float -> NumberSetup(Float.MIN_VALUE, Float.MAX_VALUE, 1f)
                is Double -> NumberSetup(Double.MIN_VALUE, Double.MAX_VALUE, 1.0)
                else -> throw RuntimeException()
            } as NumberSetup<T>
        }
    }

    @IgnoredOnParcel
    override val viewManager: IMaterialViewManager<DialogNumber<T>, MdfContentNumberBinding> =
        NumberViewManager(this)

    @IgnoredOnParcel
    override val eventManager: IMaterialEventManager<DialogNumber<T>, MdfContentNumberBinding> =
        NumberEventManager(this)

    init {
        when (value) {
            is Int,
            is Long,
            is Float,
            is Double -> {
                // OK
            }
            else -> throw RuntimeException("Class ${value::class} not supported!")
        }
    }

    // -----------
    // Result Events
    // -----------

    // Events
    // to allow observing events per Type, we do NOT use a generic class!
    // because of type erasion!
    // => simple solution: create 1 event class per type + a common interface
    // => if someone wants to observe ALL events, the interface can be used!

    sealed interface Event<T : Number> : IMaterialDialogEvent {
        interface Result<T> {
            val id: Int?
            val extra: Parcelable?
            val value: T
            val button: MaterialDialogButton
        }

        interface Cancelled<T> {
            val id: Int?
            val extra: Parcelable?
        }
    }

    sealed class EventInt : Event<Int> {
        data class Result(
            override val id: Int?,
            override val extra: Parcelable?,
            override val value: Int,
            override val button: MaterialDialogButton
        ) : EventInt(), Event.Result<Int>

        data class Cancelled(override val id: Int?, override val extra: Parcelable?) : EventInt(),
            Event.Cancelled<Int>
    }

    sealed class EventLong : Event<Long> {
        data class Result(
            override val id: Int?,
            override val extra: Parcelable?,
            override val value: Long,
            override val button: MaterialDialogButton
        ) : EventLong(), Event.Result<Long>

        data class Cancelled(override val id: Int?, override val extra: Parcelable?) : EventLong(),
            Event.Cancelled<Long>
    }

    sealed class EventFloat : Event<Float> {
        data class Result(
            override val id: Int?,
            override val extra: Parcelable?,
            override val value: Float,
            override val button: MaterialDialogButton
        ) : EventFloat(), Event.Result<Float>

        data class Cancelled(override val id: Int?, override val extra: Parcelable?) : EventFloat(),
            Event.Cancelled<Float>
    }

    sealed class EventDouble : Event<Double> {
        data class Result(
            override val id: Int?,
            override val extra: Parcelable?,
            override val value: Double,
            override val button: MaterialDialogButton
        ) : EventDouble(), Event.Result<Double>

        data class Cancelled(override val id: Int?, override val extra: Parcelable?) :
            EventDouble(), Event.Cancelled<Double>
    }
}