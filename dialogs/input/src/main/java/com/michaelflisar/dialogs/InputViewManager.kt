package com.michaelflisar.dialogs

import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.doOnNextLayout
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.LifecycleOwner
import com.google.android.material.textfield.TextInputEditText
import com.michaelflisar.dialogs.input.databinding.MdfContentInputBinding
import com.michaelflisar.dialogs.interfaces.IMaterialViewManager
import kotlinx.parcelize.Parcelize

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
        val state = MaterialDialogUtil.getViewState(savedInstanceState) ?: run {
            val initialValue = setup.initialValue.getString(binding.root.context)
            when (setup.initialState) {
                DialogInput.State.None -> ViewState(initialValue, -1, -1, false)
                DialogInput.State.SelectAll -> ViewState(initialValue, 0, initialValue.length, false)
                DialogInput.State.Focus -> ViewState(initialValue, initialValue.length, initialValue.length, true)
            }
        }

        setup.description.display(binding.mdfDescription)
        if (binding.mdfDescription.text.isEmpty()) {
            binding.mdfDescription.visibility = View.GONE
        }
        setup.hint.display(binding.mdfTextInputLayout) { view, text ->
            view.hint = text
        }
        binding.mdfTextInputEditText.inputType = setup.inputType
        binding.mdfTextInputEditText.setText(state.input)
        binding.mdfTextInputEditText.doAfterTextChanged {
            setError(binding, "")
        }
        if (state.hasSelection || state.focus) {
            binding.mdfTextInputEditText.doOnNextLayout {
                binding.mdfTextInputEditText.requestFocus()
                binding.mdfTextInputEditText.setSelection(state.selectionStart, state.selectionEnd)
                MaterialDialogUtil.showKeyboard(binding.mdfTextInputEditText)
            }
        }
    }

    override fun saveViewState(binding: MdfContentInputBinding, outState: Bundle) {
        MaterialDialogUtil.saveViewState(
            outState,
            ViewState(binding.mdfTextInputEditText)
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

    // -----------
    // State
    // -----------

    @Parcelize
    private class ViewState(
        val input: String,
        val selectionStart: Int,
        val selectionEnd: Int,
        val focus: Boolean
    ) : Parcelable {
        constructor(textInputEditText: TextInputEditText) : this(
            textInputEditText.text?.toString() ?: "",
            textInputEditText.selectionStart,
            textInputEditText.selectionEnd,
            false
        )

        val hasSelection = selectionStart != selectionEnd
    }
}