package com.michaelflisar.dialogs

import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.doOnNextLayout
import androidx.core.widget.doAfterTextChanged
import com.michaelflisar.dialogs.classes.BaseMaterialViewManager
import com.michaelflisar.dialogs.input.databinding.MdfContentInputBinding
import com.michaelflisar.dialogs.input.databinding.MdfContentInputRowBinding
import com.michaelflisar.dialogs.interfaces.IMaterialDialogPresenter
import kotlinx.parcelize.Parcelize

internal class InputViewManager(
    override val setup: DialogInput
) : BaseMaterialViewManager<DialogInput, MdfContentInputBinding>() {

    override val wrapInScrollContainer = true

    private val rowBindings = ArrayList<MdfContentInputRowBinding>()

    override fun onCreateContentViewBinding(
        layoutInflater: LayoutInflater,
        parent: ViewGroup?,
        attachToParent: Boolean
    ) = MdfContentInputBinding.inflate(layoutInflater, parent, attachToParent)

    override fun initBinding(
        presenter: IMaterialDialogPresenter<DialogInput>,
        savedInstanceState: Bundle?
    ) {
        val inputs = setup.input.getSingles()
        val state = MaterialDialogUtil.getViewState(savedInstanceState) ?: run {
            val initialValues = inputs.map {
                it.value.getString(context)
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
            rowBinding.mdfTextInputLayout.prefixText = single.prefix.get(context)
            rowBinding.mdfTextInputLayout.suffixText = single.suffix.get(context)
            rowBinding.mdfTextInputEditText.gravity = single.gravity

            rowBinding.mdfTextInputEditText.setText(state.inputs[index])
            rowBinding.mdfTextInputEditText.doAfterTextChanged {
                setError(index, "")
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

    override fun saveViewState(outState: Bundle) {
        MaterialDialogUtil.saveViewState(
            outState,
            ViewState(rowBindings)
        )
    }

    // -----------
    // Functions
    // -----------

    internal fun getCurrentInputs(): List<String> {
        return rowBindings.map { it.mdfTextInputEditText.text.toString() }
    }

    internal fun setError(index: Int, error: String) {
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