package com.michaelflisar.dialogs

import android.content.Context
import android.os.Parcelable
import android.view.Gravity
import com.michaelflisar.dialogs.classes.Icon
import com.michaelflisar.dialogs.classes.MaterialDialogAction
import com.michaelflisar.dialogs.classes.MaterialDialogButton
import com.michaelflisar.dialogs.interfaces.IMaterialDialogEvent
import com.michaelflisar.dialogs.interfaces.IMaterialEventManager
import com.michaelflisar.dialogs.interfaces.IMaterialViewManager
import com.michaelflisar.dialogs.interfaces.INumberFormatter
import com.michaelflisar.dialogs.number.databinding.MdfContentNumberBinding
import com.michaelflisar.text.Text
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Parcelize
class DialogNumber<T : Number>(
    // Key
    override val id: Int? = null,
    // Header
    override val title: Text = Text.Empty,
    override val icon: Icon = Icon.None,
    override val menu: Int? = null,
    // specific fields
    val description: Text = Text.Empty,
    val input: Input<T>,
    // Buttons
    override val buttonPositive: Text = MaterialDialog.defaults.buttonPositive,
    override val buttonNegative: Text = MaterialDialog.defaults.buttonNegative,
    override val buttonNeutral: Text = MaterialDialog.defaults.buttonNeutral,
    // Style
    override val cancelable: Boolean = MaterialDialog.defaults.cancelable,
    // Attached Data
    override val extra: Parcelable? = null
) : MaterialDialogSetup<DialogNumber<T>, MdfContentNumberBinding>() {

    companion object {

        @Suppress("UNCHECKED_CAST")
        fun <T : Number> getMin(value: T): T {
            return when (value::class) {
                Int::class -> Int.MIN_VALUE
                Long::class -> Long.MIN_VALUE
                Float::class -> Float.MIN_VALUE
                Double::class -> Double.MIN_VALUE
                else -> throw RuntimeException()
            } as T
        }

        @Suppress("UNCHECKED_CAST")
        fun <T : Number> getMax(value: T): T {
            return when (value::class) {
                Int::class -> Int.MAX_VALUE
                Long::class -> Long.MAX_VALUE
                Float::class -> Float.MAX_VALUE
                Double::class -> Double.MAX_VALUE
                else -> throw RuntimeException()
            } as T
        }

        @Suppress("UNCHECKED_CAST")
        fun <T : Number> getOne(value: T): T {
            return when (value::class) {
                Int::class -> 1
                Long::class -> 1L
                Float::class -> 1f
                Double::class -> 1.0
                else -> throw RuntimeException()
            } as T
        }
    }

    @IgnoredOnParcel
    override val viewManager: IMaterialViewManager<DialogNumber<T>, MdfContentNumberBinding> =
        NumberViewManager(this)

    @IgnoredOnParcel
    override val eventManager: IMaterialEventManager<DialogNumber<T>> =
        NumberEventManager(this)

    internal fun firstValue(): T = input.getSingles<T>().first().value

    init {
        val firstValue = input.getSingles<T>().first().value
        when (firstValue) {
            is Int,
            is Long,
            is Float,
            is Double -> {
                // OK
            }
            else -> throw RuntimeException("Class ${firstValue::class} not supported!")
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

        interface Result<T> : IMaterialDialogEvent{
            val values: List<T>
            val button: MaterialDialogButton
            val value: T
                get() = values.first()
        }

        interface Action<T> : IMaterialDialogEvent, IMaterialDialogEvent.Action{
            override val data: MaterialDialogAction
        }

    }

    sealed class EventInt : Event<Int> {

        data class Result(
            override val id: Int?,
            override val extra: Parcelable?,
            override val values: List<Int>,
            override val button: MaterialDialogButton
        ) : EventInt(), Event.Result<Int>

        data class Action(
            override val id: Int?,
            override val extra: Parcelable?,
            override val data: MaterialDialogAction
        ) : EventInt(), Event.Action<Int>
    }

    sealed class EventLong : Event<Long> {

        data class Result(
            override val id: Int?,
            override val extra: Parcelable?,
            override val values: List<Long>,
            override val button: MaterialDialogButton
        ) : EventLong(), Event.Result<Long>

        data class Action(
            override val id: Int?,
            override val extra: Parcelable?,
            override val data: MaterialDialogAction
        ) : EventLong(), Event.Action<Long>

    }

    sealed class EventFloat : Event<Float> {

        data class Result(
            override val id: Int?,
            override val extra: Parcelable?,
            override val values: List<Float>,
            override val button: MaterialDialogButton
        ) : EventFloat(), Event.Result<Float>

        data class Action(
            override val id: Int?,
            override val extra: Parcelable?,
            override val data: MaterialDialogAction
        ) : EventFloat(), Event.Action<Float>

    }

    sealed class EventDouble : Event<Double> {

        data class Result(
            override val id: Int?,
            override val extra: Parcelable?,
            override val values: List<Double>,
            override val button: MaterialDialogButton
        ) : EventDouble(), Event.Result<Double>

        data class Action(
            override val id: Int?,
            override val extra: Parcelable?,
            override val data: MaterialDialogAction
        ) : EventDouble(), Event.Action<Double>

    }

    // -----------
    // Enums/Classes
    // -----------

    sealed class Input<T : Number> : Parcelable {

        @Parcelize
        class Single<T : Number> constructor(
            val value: T,
            val min: T = getMin(value),
            val max: T = getMax(value),
            val step: T = getOne(value),
            val hint: Text = Text.Empty,
            val formatter: INumberFormatter<T>? = null,
            val gravity: Int = Gravity.CENTER_HORIZONTAL
        ) : Input<T>() {

            fun isValid(value: T): Boolean {
                return when (min) {
                    is Int -> value as Int >= min as Int && value as Int <= max as Int
                    is Long -> value as Long >= min as Long && value as Long <= max as Long
                    is Float -> value as Float >= min as Float && value as Float <= max as Float
                    is Double -> value as Double >= min as Double && value as Double <= max as Double
                    else -> throw RuntimeException()
                }
            }
        }

        @Parcelize
        class Multi<T : Number>(
            val singles: List<Single<T>>
        ) : Input<T>()

        internal fun <T : Number> getSingles(): List<Single<T>> {
            return when (this) {
                is Multi<*> -> singles
                is Single<*> -> listOf(this)
            } as List<Single<T>>
        }
    }

    @Parcelize
    class Formatter<T>(
        val res: Int
    ) : INumberFormatter<T> {

        override fun format(context: Context, value: T): String {
            return context.getString(res, value)
        }
    }
}