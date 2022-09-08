package com.michaelflisar.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.michaelflisar.dialogs.classes.ListItemAdapter
import com.michaelflisar.dialogs.interfaces.IMaterialViewManager
import com.michaelflisar.dialogs.list.databinding.MdfContentListBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

internal class ListViewManager(
    private val setup: DialogList
) : IMaterialViewManager<DialogList, MdfContentListBinding> {

    private lateinit var adapter: ListItemAdapter

    // this dialog has a scrolling container itself!
    override val wrapInScrollContainer = false

    private val DISABLE_SEPARATORS = true // for now at least...

    override fun createContentViewBinding(
        layoutInflater: LayoutInflater,
        parent: ViewGroup?,
        attachToParent: Boolean
    ) = MdfContentListBinding.inflate(layoutInflater, parent, attachToParent)

    override fun initBinding(
        lifecycleOwner: LifecycleOwner,
        binding: MdfContentListBinding,
        savedInstanceState: Bundle?
    ) {
        val state =
            MaterialDialogUtil.getViewState<DialogList.ViewState>(savedInstanceState)
        adapter = ListItemAdapter(
            binding.root.context,
            setup,
            savedInstanceState
        ) {
            checkInfo(binding)
        }

        val hasDescription = setup.description.display(binding.mdfDescription).isNotEmpty()
        binding.mdfDescription.visibility = if (hasDescription) View.VISIBLE else View.GONE

        binding.mdfDividerTop.alpha = 0f
        binding.mdfDividerBottom.alpha = 0f

        when (val itemsProvider = setup.itemsProvider) {
            is DialogList.ItemProvider.ItemLoader -> {
                // load items
                lifecycleOwner.lifecycleScope.launch {
                    lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                        val items = itemsProvider.loader.load(binding.root.context)
                        withContext(Dispatchers.Main) {
                            updateItems(binding, items)
                        }
                    }
                }
            }
            is DialogList.ItemProvider.List -> {
                updateItems(binding, itemsProvider.items)
            }
        }

        binding.mdfRecyclerView.apply {
            layoutManager = LinearLayoutManager(binding.root.context, RecyclerView.VERTICAL, false)
            adapter = this@ListViewManager.adapter
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    checkDividers(binding)
                }
            })
        }

        listOf(binding.mdfContainerFilter, binding.mdfInfoFilter).forEach {
            it.visibility = if (setup.filter == null) View.GONE else View.VISIBLE
        }

        binding.mdfTextInputEditText.setText(state?.filter ?: "")
        binding.mdfTextInputEditText.doOnTextChanged { text, start, before, count ->
            adapter.updateFilter(text?.toString() ?: "") {
                onFilterChanged(binding)
            }
        }

        if (DISABLE_SEPARATORS) {
            listOf(binding.mdfDividerTop, binding.mdfDividerBottom).forEach {
                it.visibility = View.INVISIBLE
            }
        }
    }

    override fun saveViewState(binding: MdfContentListBinding, outState: Bundle) {
        MaterialDialogUtil.saveViewState(
            outState,
            DialogList.ViewState(
                getSelectedIds(),
                binding.mdfTextInputEditText.text?.toString() ?: ""
            )
        )
    }

    override fun onBeforeDismiss(binding: MdfContentListBinding) {
        super.onBeforeDismiss(binding)
        MaterialDialogUtil.ensureKeyboardCloses(binding.mdfTextInputEditText)
    }

    // -----------
    // Functions
    // -----------

    internal fun getSelectedItemsForResult(): List<DialogList.ListItem> {
        return adapter.getCheckedItemsForResult()
    }

    private fun updateItems(binding: MdfContentListBinding, items: List<DialogList.ListItem>) {
        binding.mdfLoading.visibility = View.GONE
        adapter.updateItems(items) {
            onFilterChanged(binding)
        }
    }

    private fun getSelectedIds(): SortedSet<Long> {
        return adapter.getCheckedIds()
    }

    private fun onFilterChanged(binding: MdfContentListBinding) {
        checkDividers(binding)
        checkIsEmptyView(binding)
        checkInfo(binding)
    }

    private fun checkDividers(binding: MdfContentListBinding) {
        if (DISABLE_SEPARATORS)
            return
        val alphaTop = if (!binding.mdfRecyclerView.canScrollVertically(-1)) 0f else 1f
        val alphaBottom = if (!binding.mdfRecyclerView.canScrollVertically(1)) 0f else 1f
        binding.mdfDividerTop.animate().cancel()
        binding.mdfDividerTop.animate().alpha(alphaTop).start()
        binding.mdfDividerBottom.animate().cancel()
        binding.mdfDividerBottom.animate().alpha(alphaBottom).start()
    }

    private fun checkIsEmptyView(binding: MdfContentListBinding) {
        binding.mdfEmpty.visibility = if (adapter.itemCount == 0) View.VISIBLE else View.GONE
    }

    private fun checkInfo(binding: MdfContentListBinding) {
        if (setup.infoFormatter == null) {
            binding.mdfInfoFilter.visibility = View.GONE
            return
        }
        val info = setup.infoFormatter.formatInfo(adapter.itemCountUnfiltered,  adapter.itemCount, adapter.getCheckedItemsForResult().size)
        binding.mdfInfoFilter.text = info
    }
}