package com.michaelflisar.dialogs

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.michaelflisar.dialogs.classes.BaseMaterialViewManager
import com.michaelflisar.dialogs.classes.XMLPagerAdapter
import com.michaelflisar.dialogs.datetime.databinding.MdfContentDatetimeBinding
import com.michaelflisar.dialogs.interfaces.IMaterialDialogPresenter

internal class DateTimeViewManager<T : DateTimeData>(
    override val setup: DialogDateTime<T>
) : BaseMaterialViewManager<DialogDateTime<T>, MdfContentDatetimeBinding>() {

    override val wrapInScrollContainer = false

    override fun onCreateContentViewBinding(
        layoutInflater: LayoutInflater,
        parent: ViewGroup?,
        attachToParent: Boolean
    ) = MdfContentDatetimeBinding.inflate(layoutInflater, parent, attachToParent)

    override fun initBinding(
        presenter: IMaterialDialogPresenter<DialogDateTime<T>>,
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
            views.add(binding.mdfDateView)
        else
            binding.pager.removeView(binding.mdfDateView)
        if (hasTime)
            views.add(binding.mdfTimeView)
        else
            binding.pager.removeView(binding.mdfTimeView)

        val adapter = XMLPagerAdapter(views)
        binding.pager.adapter = adapter
        binding.dots.attachViewPager(binding.pager)
        binding.dots.setOnClickListener {
            val page = (binding.pager.currentItem + 1) % views.size
            binding.pager.currentItem = page
        }
        if (views.size <= 1) {
            binding.dots.visibility = View.GONE
        }

        if (time != null) {
            binding.mdfTimeView.is24Hours = setup.timeFormat == DialogDateTime.TimeFormat.H24
            binding.mdfTimeView.time = time
        }

        if (date != null) {
            binding.mdfDateView.init(date, setup.dateFormat)
        }

        binding.root.requestFocus()
    }

    fun getCurrentValue(): T {
        val date = binding.mdfDateView.date
        val time = binding.mdfTimeView.time
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