package com.michaelflisar.dialogs.classes

import android.content.Context
import com.michaelflisar.dialogs.input.R
import com.michaelflisar.dialogs.interfaces.IInputValidator
import kotlinx.parcelize.Parcelize

@Parcelize
internal class SimpleInputValidator(
    private val minLength: Int? = null,
    private val maxLength: Int? = null,
) : IInputValidator {

    override fun isValid(input: String): Boolean {
        return (minLength == null || input.length >= minLength) &&
                (maxLength == null || input.length <= maxLength)
    }

    override fun getError(context: Context, input: String): String {
        return if (minLength != null && maxLength != null)
            context.getString(R.string.mdf_error_inputs_length_rule_violated, minLength, maxLength)
        else if (minLength != null) {
            context.getString(R.string.mdf_error_inputs_length_rule_min_violated, minLength)
        } else if (maxLength != null) {
            context.getString(R.string.mdf_error_inputs_length_rule_max_violated, maxLength!!)
        } else "" // can never happen, isValid would always return true in this case
    }
}