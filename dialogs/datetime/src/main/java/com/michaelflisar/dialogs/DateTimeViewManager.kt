package com.michaelflisar.dialogs

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import com.michaelflisar.dialogs.classes.DateTimeData
import com.michaelflisar.dialogs.classes.XMLPagerAdapter
import com.michaelflisar.dialogs.datetime.databinding.MdfContentDatetimeBinding
import com.michaelflisar.dialogs.interfaces.IMaterialViewManager

internal class DateTimeViewManager<T : DateTimeData>(
    private val setup: DialogDateTime<T>
) : IMaterialViewManager<DialogDateTime<T>, MdfContentDatetimeBinding> {

    override val wrapInScrollContainer = false

    override fun createContentViewBinding(
        layoutInflater: LayoutInflater,
        parent: ViewGroup?,
        attachToParent: Boolean
    ) = MdfContentDatetimeBinding.inflate(layoutInflater, parent, attachToParent)

    override fun initBinding(
        lifecycleOwner: LifecycleOwner,
        binding: MdfContentDatetimeBinding,
        savedInstanceState: Bundle?
    ) {
        val value = setup.value

        Log.d("VALUE", "value = $value")

        var time: DateTimeData.Time? = null
        var date: DateTimeData.Date? = null
        when (value) {
            is DateTimeData.DateTime -> {
                time = value.time
                date = value.date
            }
            is DateTimeData.Date -> {
                date = value
            }
            is DateTimeData.Time -> {
                time = value
            }
        }

        val hasTime = time != null
        val hasDate = date != null
        val views = ArrayList<View>()
        if (hasDate)
            views.add(binding.mdfDatePicker)
        if (hasTime)
            views.add(binding.mdfTimePicker)
        val adapter = XMLPagerAdapter(views)
        binding.pager.adapter = adapter
        binding.dots.attachViewPager(binding.pager)
        if (views.size <= 1) {
            binding.dots.visibility = View.GONE
        }

        if (time != null) {
            binding.mdfTimePicker.setIs24HourView(setup.is24Hours)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                binding.mdfTimePicker.minute = time.min
                binding.mdfTimePicker.hour = time.hour
            } else {
                binding.mdfTimePicker.currentMinute = time.min
                binding.mdfTimePicker.currentHour = time.hour
            }
        }

        if (date != null) {
            //binding.mdfDatePicker.updateDate(date.year, date.month, date.day)
            //binding.mdfDatePicker.minDate = 0
            //binding.mdfDatePicker.maxDate = Long.MAX_VALUE
        }
    }

    fun getCurrentValue(binding: MdfContentDatetimeBinding): T {
        val date = DateTimeData.Date(
            binding.mdfDatePicker.year,
            binding.mdfDatePicker.month,
            binding.mdfDatePicker.dayOfMonth
        )
        val time = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            DateTimeData.Time(binding.mdfTimePicker.hour, binding.mdfTimePicker.minute)
        } else {
            DateTimeData.Time(
                binding.mdfTimePicker.currentHour,
                binding.mdfTimePicker.currentMinute
            )
        }
        return when (setup.value) {
            is DateTimeData.DateTime -> {
                DateTimeData.DateTime(date, time)
            }
            is DateTimeData.Date -> {
                date
            }
            is DateTimeData.Time -> {
                time
            }
            else -> {}
        } as T
    }
}