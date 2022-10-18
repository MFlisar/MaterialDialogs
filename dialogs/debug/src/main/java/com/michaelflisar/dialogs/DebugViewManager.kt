package com.michaelflisar.dialogs

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.michaelflisar.dialogs.classes.*
import com.michaelflisar.dialogs.classes.DebugAdapter
import com.michaelflisar.dialogs.debug.databinding.MdfContentDebugBinding
import com.michaelflisar.dialogs.interfaces.IMaterialDialogPresenter

internal class DebugViewManager(
    private val setup: DialogDebug
) : BaseMaterialViewManager<MdfContentDebugBinding>() {

    override val wrapInScrollContainer = false

    private lateinit var manager: DebugDataManager
    private lateinit var adapter: DebugAdapter

    override fun onCreateContentViewBinding(
        layoutInflater: LayoutInflater,
        parent: ViewGroup?,
        attachToParent: Boolean
    ): MdfContentDebugBinding =
        MdfContentDebugBinding.inflate(layoutInflater, parent, attachToParent)

    override fun initBinding(
        presenter: IMaterialDialogPresenter<*>,
        savedInstanceState: Bundle?
    ) {
        presenter.requireLifecycleOwner()
            .onMaterialDialogEvent<DialogDebug.Event> {
                onDebugEvent(presenter, it)
            }

        val context = binding.root.context
        manager = setup.manager
        adapter = DebugAdapter(
            setup.manager,
            setup.manager.items,
            context,
            setup.withNumbering
        ) { item, index ->
            DialogDebug.Event.Result(setup.id, setup.extra, item, index).send(presenter)
        }
        binding.mdfRecyclerview.layoutManager =
            LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        binding.mdfRecyclerview.adapter = adapter

        updateSubTitle(binding, null, null)
    }

    private fun onDebugEvent(
        presenter: IMaterialDialogPresenter<*>,
        event: DialogDebug.Event
    ) {
        if (event is DialogDebug.Event.Result) {
            if (event.button == MaterialDialogButton.Negative) {
                if (!goLevelUp(binding)) {
                    presenter.dismiss?.invoke()
                }
            } else if (event.button == null) {
                val item = event.item!!
                val index = event.index!!
                if (item is DebugItem.SubEntryHolder<*>) {
                    goLevelDown(binding, index)
                } else {
                    val clickResult = item.onClick(manager, presenter, setup)
                    if (clickResult.contains(DebugItem.ClickResult.Notify)) {
                        adapter.notifyItemChanged(index)
                    }
                    if (clickResult.contains(DebugItem.ClickResult.GoUp)) {
                        goLevelUp(binding)
                    }
                }
            }
        } else if (event is DialogDebug.Event.NotifyDataSetChanged) {
            adapter.notifyDataSetChanged()
        }
    }

    private fun goLevelUp(binding: MdfContentDebugBinding): Boolean {
        return adapter.goLevelUp { parentEntry, number ->
            updateSubTitle(binding, parentEntry, number)
        }
    }

    private fun goLevelDown(binding: MdfContentDebugBinding, index: Int) {
        adapter.goLevelDown(index) { parentEntry, number ->
            updateSubTitle(binding, parentEntry, number)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun updateSubTitle(
        binding: MdfContentDebugBinding,
        parentEntry: DebugItem<*>?,
        number: String?
    ) {
        if (parentEntry?.name != null) {
            binding.mdfTitle.text = (number?.plus(" ") ?: "") + parentEntry.name
            binding.mdfTitle.visibility = View.VISIBLE
        } else {
            binding.mdfTitle.visibility = View.GONE
        }
    }
}