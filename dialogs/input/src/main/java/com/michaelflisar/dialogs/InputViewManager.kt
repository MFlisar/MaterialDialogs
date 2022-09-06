package com.michaelflisar.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.LifecycleOwner
import com.michaelflisar.dialogs.input.databinding.MdfContentInputBinding
import com.michaelflisar.dialogs.interfaces.IMaterialViewManager

internal class InputViewManager(
    private val setup: DialogInput
) : IMaterialViewManager<DialogInput, MdfContentInputBinding> {

    override val wrapInScrollContainer = true

    override fun createContentViewBinding(
        layoutInflater: LayoutInflater,
        parent: ViewGroup?,
        attachToParent: Boolean
    ) = MdfContentInputBinding.inflate(layoutInflater, parent, attachToParent)

    override fun initBinding(
        lifecycleOwner: LifecycleOwner,
        binding: MdfContentInputBinding,
        savedInstanceState: Bundle?
    ) {
        val state =
            MaterialDialogUtil.getViewState<DialogInput.ViewState>(savedInstanceState)
        val input = state?.input ?: setup.initialValue.getString(binding.root.context)
        setup.description.display(binding.mdfDescription)
        if (binding.mdfDescription.text.isEmpty()) {
            binding.mdfDescription.visibility = View.GONE
        }
        setup.hint.display(binding.mdfTextInputLayout) { view, text ->
            view.hint = text
        }
        binding.mdfTextInputEditText.inputType = setup.inputType
        binding.mdfTextInputEditText.setText(input)
        binding.mdfTextInputEditText.doAfterTextChanged {
            setError(binding, "")
        }
        state?.let {
            binding.mdfTextInputEditText.setSelection(it.selectionStart, it.selectionEnd)
        }
    }

    override fun saveViewState(binding: MdfContentInputBinding, outState: Bundle) {
        MaterialDialogUtil.saveViewState(
            outState,
            DialogInput.ViewState(binding.mdfTextInputEditText)
        )
    }

    // -----------
    // Functions
    // -----------

    internal fun getCurrentInput(binding: MdfContentInputBinding): String {
        return binding.mdfTextInputEditText.text.toString()
    }

    internal fun setError(binding: MdfContentInputBinding, error: String) {
        binding.mdfTextInputLayout.error = error.takeIf { it.isNotEmpty() }
    }
}