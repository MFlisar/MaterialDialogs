package com.michaelflisar.dialogs.classes

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.*

sealed class DateTimeData : Parcelable {

    abstract fun asCalendar(): Calendar

    companion object {

        inline fun <reified T : DateTimeData> now(): T {
            val cal = Calendar.getInstance()
            return convert(cal)
        }

        inline fun <reified T : DateTimeData> convert(cal: Calendar): T {
            var time: Time? = null
            var date: Date? = null

            val isTime = T::class.java === Time::class.java
            val isDate = T::class.java === Date::class.java
            val isDateTime = T::class.java === DateTime::class.java

            if (isTime || isDateTime) {
                time = Time(cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE))
            }

            if (isDate || isDateTime) {
                date = Date(
                    cal.get(Calendar.DAY_OF_MONTH),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.YEAR)
                )
            }

            if (isTime)
                return time as T
            if (isDate)
                return date as T
            return DateTime(date!!, time!!) as T
        }
    }

    @Parcelize
    data class Time(val hour: Int, val min: Int) : DateTimeData() {
        companion object {
            fun now() = now<Time>()
        }

        override fun asCalendar(): Calendar {
            return Calendar.getInstance().apply {
                timeInMillis = 0L
                set(Calendar.HOUR_OF_DAY, hour)
                set(Calendar.MINUTE, min)
            }
        }
    }

    @Parcelize
    data class Date(
        val day: Int,
        val month: Int,
        val year: Int
    ) : DateTimeData() {
        companion object {
            fun now() = now<Date>()
        }

        override fun asCalendar(): Calendar {
            return Calendar.getInstance().apply {
                timeInMillis = 0L
                set(Calendar.YEAR, year)
                set(Calendar.MONTH, month)
                set(Calendar.DAY_OF_MONTH, day)
            }
        }
    }

    @Parcelize
    data class DateTime(
        val date: Date,
        val time: Time
    ) : DateTimeData() {
        companion object {
            fun now() = now<DateTime>()
        }

        override fun asCalendar(): Calendar {
            return Calendar.getInstance().apply {
                timeInMillis = 0L
                set(Calendar.YEAR, date.year)
                set(Calendar.MONTH, date.month)
                set(Calendar.DAY_OF_MONTH, date.day)
                set(Calendar.HOUR_OF_DAY, this@DateTime.time.hour)
                set(Calendar.MINUTE, this@DateTime.time.min)
            }
        }
    }
}