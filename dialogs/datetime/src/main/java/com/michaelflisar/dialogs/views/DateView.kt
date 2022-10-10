package com.michaelflisar.dialogs.views

import android.content.Context
import android.graphics.ColorFilter
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.core.graphics.drawable.DrawableCompat
import com.google.android.material.textfield.TextInputLayout
import com.michaelflisar.dialogs.DialogDateTime
import com.michaelflisar.dialogs.MaterialDialogUtil
import com.michaelflisar.dialogs.classes.DateEditTextMask
import com.michaelflisar.dialogs.classes.DateTimeData
import com.michaelflisar.dialogs.datetime.R
import com.michaelflisar.dialogs.datetime.databinding.MdfViewDatePickerBinding

internal class DateView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : LinearLayout(context, attrs) {

    val binding: MdfViewDatePickerBinding

    var date: DateTimeData.Date = DateTimeData.Date.now()

    init {
        orientation = VERTICAL
        binding = MdfViewDatePickerBinding.inflate(LayoutInflater.from(context), this)
    }

    fun init(date: DateTimeData.Date, format: DialogDateTime.DateFormat) {
        val value = format.dateFormat.format(date.asCalendar().time)
        binding.mdfTiet.setText(value)
        binding.mdfLabel.text = value
        binding.mdfTil.hint = format.pattern
        DateEditTextMask(binding.mdfTiet, format)  {
            if (it != null) {
                this.date = DateTimeData.convert(it)
                val value = format.dateFormat.format(it)
                binding.mdfLabel.text = value
                //binding.mdfTiet.error = null
                binding.mdfTil.endIconMode = TextInputLayout.END_ICON_NONE
            } else {
                //binding.mdfTiet.error = ""
                binding.mdfTil.endIconMode = TextInputLayout.END_ICON_CUSTOM
                binding.mdfTil.setEndIconDrawable(R.drawable.ic_date_error)
                binding.mdfTil.endIconDrawable?.apply {
                    DrawableCompat.setTint(this, MaterialDialogUtil.getThemeColorAttr(context, R.attr.colorError))
                }
            }
        }.listen()
    }
}