package com.michaelflisar.dialogs

import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.michaelflisar.dialogs.classes.BaseMaterialViewManager
import com.michaelflisar.dialogs.classes.ListItemAdapter
import com.michaelflisar.dialogs.interfaces.IListItem
import com.michaelflisar.dialogs.interfaces.IMaterialDialogPresenter
import com.michaelflisar.dialogs.list.databinding.MdfContentListBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.parcelize.Parcelize
import java.util.*

internal class ListViewManager(
    override val setup: DialogList
) : BaseMaterialViewManager<DialogList, MdfContentListBinding>() {

    private lateinit var adapter: ListItemAdapter

    // this dialog has a scrolling container itself!
    override val wrapInScrollContainer = false

    private val DISABLE_SEPARATORS = true // for now at least...

    override fun onCreateContentViewBinding(
        layoutInflater: LayoutInflater,
        parent: ViewGroup?,
        attachToParent: Boolean
    ) = MdfContentListBinding.inflate(layoutInflater, parent, attachToParent)

    override fun initBinding(savedInstanceState: Bundle?) {
        val state = MaterialDialogUtil.getViewState(savedInstanceState) ?: ViewState(
            setup.selectionMode.getInitialSelection(),
            ""
        )
        adapter = ListItemAdapter(
            presenter,
            context,
            setup,
            state.selectedIds,
            state.filter
        ) {
            checkInfo()
        }

        val hasDescription = setup.description.display(binding.mdfDescription).isNotEmpty()
        binding.mdfDescription.visibility = if (hasDescription) View.VISIBLE else View.GONE

        binding.mdfDividerTop.alpha = 0f
        binding.mdfDividerBottom.alpha = 0f

        when (val items = setup.items) {
            is DialogList.Items.Loader -> {
                // load items
                presenter.requireLifecycleOwner().lifecycleScope.launch {
                    presenter.requireLifecycleOwner().repeatOnLifecycle(Lifecycle.State.STARTED) {
                        val loadedItems = items.loader.load(context)
                        withContext(Dispatchers.Main) {
                            updateItems(loadedItems)
                        }
                    }
                }
            }
            is DialogList.Items.List -> {
                updateItems(items.items)
            }
        }

        binding.mdfRecyclerView.apply {
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            adapter = this@ListViewManager.adapter
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    checkDividers()
                }
            })
        }

        listOf(binding.mdfContainerFilter, binding.mdfInfoFilter).forEach {
            it.visibility = if (setup.filter == null) View.GONE else View.VISIBLE
        }

        binding.mdfTextInputEditText.setText(state.filter)
        binding.mdfTextInputEditText.doOnTextChanged { text, _, _, _ ->
            adapter.updateFilter(text?.toString() ?: "") {
                onFilterChanged()
            }
        }

        if (DISABLE_SEPARATORS) {
            listOf(binding.mdfDividerTop, binding.mdfDividerBottom).forEach {
                it.visibility = View.GONE
            }
        }
    }

    override fun saveViewState(outState: Bundle) {
        MaterialDialogUtil.saveViewState(
            outState,
            ViewState(
                getSelectedIds(),
                _binding?.mdfTextInputEditText?.text?.toString() ?: ""
            )
        )
    }

    override fun onBeforeDismiss() {
        super.onBeforeDismiss()
        MaterialDialogUtil.ensureKeyboardCloses(binding.mdfTextInputEditText)
    }

    // -----------
    // Functions
    // -----------

    internal fun getSelectedItemsForResult(): List<IListItem> {
        return adapter.getCheckedItemsForResult()
    }

    private fun updateItems(items: List<IListItem>) {
        binding.mdfLoading.visibility = View.GONE
        adapter.updateItems(items) {
            onFilterChanged()
        }
    }

    private fun getSelectedIds(): SortedSet<Long> {
        return adapter.getCheckedIds()
    }

    private fun onFilterChanged() {
        checkDividers()
        checkIsEmptyView()
        checkInfo()
    }

    private fun checkDividers() {
        if (DISABLE_SEPARATORS)
            return
        val alphaTop = if (!binding.mdfRecyclerView.canScrollVertically(-1)) 0f else 1f
        val alphaBottom = if (!binding.mdfRecyclerView.canScrollVertically(1)) 0f else 1f
        binding.mdfDividerTop.animate().cancel()
        binding.mdfDividerTop.animate().alpha(alphaTop).start()
        binding.mdfDividerBottom.animate().cancel()
        binding.mdfDividerBottom.animate().alpha(alphaBottom).start()
    }

    private fun checkIsEmptyView() {
        binding.mdfEmpty.visibility = if (adapter.itemCount == 0) View.VISIBLE else View.GONE
    }

    private fun checkInfo() {
        if (setup.infoFormatter == null) {
            binding.mdfInfoFilter.visibility = View.GONE
            return
        }
        val info = setup.infoFormatter.formatInfo(
            adapter.itemCountUnfiltered,
            adapter.itemCount,
            adapter.getCheckedItemsForResult().size
        )
        binding.mdfInfoFilter.text = info
    }

    // -----------
    // State
    // -----------

    @Parcelize
    class ViewState(
        val selectedIds: SortedSet<Long>,
        val filter: String
    ) : Parcelable
}