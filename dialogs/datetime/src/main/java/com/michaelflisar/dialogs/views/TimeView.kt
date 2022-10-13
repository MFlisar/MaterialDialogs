package com.michaelflisar.dialogs.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import com.michaelflisar.dialogs.DateTimeData
import com.michaelflisar.dialogs.datetime.R
import com.michaelflisar.dialogs.datetime.databinding.MdfViewTimePickerBinding

internal class TimeView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : LinearLayout(context, attrs, defStyleAttr, defStyleRes) {

    private val binding: MdfViewTimePickerBinding
    private var _time = DateTimeData.Time.now()

    var time: DateTimeData.Time
        set(value) {
            _time = value
            update()
        }
        get() = _time

    var is24Hours: Boolean = true
        set(value) {
            field = value
            update()
        }

    private var isPM: Boolean = _time.hour > 12

    init {
        orientation = HORIZONTAL
        binding = MdfViewTimePickerBinding.inflate(LayoutInflater.from(context), this)

        binding.mdfInputHour.init(
            time.hour,
            0,
            if (is24Hours) 24 else 12,
            2,
            R.string.material_timepicker_hour
        ) {
            updateTime()
        }
        binding.mdfInputMin.init(time.min, 0, 59, 2, R.string.material_timepicker_minute) {
            updateTime()
        }
        binding.mdfButtongroupAmpm.addOnButtonCheckedListener { group, checkedId, isChecked ->
            if (isChecked) {
                isPM = checkedId == R.id.mdf_button_pm
                updateTime()
            }
        }

        update()
    }

    private fun update() {
        val h = if (is24Hours) time.hour else (time.hour % 12)
        binding.mdfInputHour.update(h, if (is24Hours) 24 else 12)
        binding.mdfInputMin.update(time.min)
        if (is24Hours) {
            binding.mdfSpacer2.visibility = View.GONE
            binding.mdfButtongroupAmpm.visibility = View.GONE
        } else {
            binding.mdfSpacer2.visibility = View.VISIBLE
            binding.mdfButtongroupAmpm.visibility = View.VISIBLE
            binding.mdfButtongroupAmpm.check(if (time.hour < 12) R.id.mdf_button_am else R.id.mdf_button_pm)
        }
    }

    private fun updateTime() {
        var h = binding.mdfInputHour.value
        var m = binding.mdfInputMin.value
        if (!is24Hours) {
            h += if (isPM) 12 else 0
        }
        //h = if (h < 0) 0 else if (h >= 24) 0 else if (h >= 23) 23 else h
        //m = if (m < 0) 0 else if (m >= 59) 59 else m
        _time = DateTimeData.Time(h, m)
    }
}