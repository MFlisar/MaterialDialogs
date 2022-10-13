package com.michaelflisar.dialogs

import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.doOnNextLayout
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.LifecycleOwner
import com.michaelflisar.dialogs.input.databinding.MdfContentInputBinding
import com.michaelflisar.dialogs.input.databinding.MdfContentInputRowBinding
import com.michaelflisar.dialogs.interfaces.IMaterialViewManager
import kotlinx.parcelize.Parcelize

internal class InputViewManager(
    private val setup: DialogInput
) : IMaterialViewManager<DialogInput, MdfContentInputBinding> {

    override val wrapInScrollContainer = true

    private val rowBindings = ArrayList<MdfContentInputRowBinding>()

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
        val inputs = setup.input.getSingles()
        val state = MaterialDialogUtil.getViewState(savedInstanceState) ?: run {
            val initialValues = inputs.map {
                it.initialValue.getString(binding.root.context)
            }
            ViewState(initialValues, initialValues.map { Pair(-1, -1) }, -1)
        }

        setup.description.display(binding.mdfDescription)
        if (binding.mdfDescription.text.isEmpty()) {
            binding.mdfDescription.visibility = View.GONE
        }

        val layoutInflater = LayoutInflater.from(binding.mdfContainer.context)
        rowBindings.clear()
        inputs.forEachIndexed { index, single ->
            val rowBinding =
                MdfContentInputRowBinding.inflate(layoutInflater, binding.mdfContainer, true)
            rowBindings.add(rowBinding)
            single.hint.display(rowBinding.mdfTextInputLayout) { view, text ->
                view.hint = text
            }
            rowBinding.mdfTextInputEditText.setSelectAllOnFocus(setup.selectAllOnFocus)
            rowBinding.mdfTextInputEditText.inputType = single.inputType

            rowBinding.mdfTextInputEditText.setText(state.inputs[index])
            rowBinding.mdfTextInputEditText.doAfterTextChanged {
                setError(binding, index, "")
            }

            rowBinding.mdfTextInputEditText.doOnNextLayout {
                if (state.focused == index) {
                    rowBinding.mdfTextInputEditText.requestFocus()
                }
                val selection = state.selections[index]
                if (selection.first != -1 && selection.second != -1) {
                    rowBinding.mdfTextInputEditText.setSelection(selection.first, selection.second)
                }
                MaterialDialogUtil.showKeyboard(rowBinding.mdfTextInputEditText)
            }
        }
    }

    override fun saveViewState(binding: MdfContentInputBinding, outState: Bundle) {
        MaterialDialogUtil.saveViewState(
            outState,
            ViewState(rowBindings)
        )
    }

    // -----------
    // Functions
    // -----------

    internal fun getCurrentInputs(binding: MdfContentInputBinding): List<String> {
        return rowBindings.map { it.mdfTextInputEditText.text.toString() }
    }

    internal fun setError(binding: MdfContentInputBinding, index: Int, error: String) {
        rowBindings[index].mdfTextInputLayout.error = error.takeIf { it.isNotEmpty() }
    }

    // -----------
    // State
    // -----------

    @Parcelize
    private class ViewState(
        val inputs: List<String>,
        val selections: List<Pair<Int, Int>>,
        val focused: Int
    ) : Parcelable {
        constructor(
            rowBindings: List<MdfContentInputRowBinding>
        ) : this(
            rowBindings.map { it.mdfTextInputEditText.text?.toString() ?: "" },
            rowBindings.map {
                Pair(
                    it.mdfTextInputEditText.selectionStart,
                    it.mdfTextInputEditText.selectionEnd
                )
            },
            -1
        )
    }
}