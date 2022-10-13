package com.michaelflisar.dialogs

import android.os.Parcelable
import com.michaelflisar.dialogs.classes.*
import com.michaelflisar.dialogs.classes.DefaultListViewHolderFactory
import com.michaelflisar.dialogs.classes.SimpleListItem
import com.michaelflisar.dialogs.interfaces.*
import com.michaelflisar.dialogs.list.databinding.MdfContentListBinding
import com.michaelflisar.text.Text
import com.michaelflisar.text.asText
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
class DialogList(
    // Key
    override val id: Int? = null,
    // Header
    override val title: Text = Text.Empty,
    override val icon: Icon = Icon.None,
    // specific fields
    val itemsProvider: ItemProvider,
    val disabledIds: Set<Long> = emptySet(),
    val description: Text = Text.Empty,
    val selectionMode: SelectionMode = SelectionMode.SingleSelect(),
    val filter: IListFilter? = null,
    val viewFactory: IListviewHolderFactory = DefaultListViewHolderFactory,
    val infoFormatter: IListInfoFormatter? = null,
    // Buttons
    override val buttonPositive: Text = MaterialDialog.defaults.buttonPositive,
    override val buttonNegative: Text = MaterialDialog.defaults.buttonNegative,
    override val buttonNeutral: Text = MaterialDialog.defaults.buttonNeutral,
    // Style
    override val cancelable: Boolean = MaterialDialog.defaults.cancelable,
    // Attached Data
    override val extra: Parcelable? = null
) : MaterialDialogSetup<DialogList, MdfContentListBinding, DialogList.Event>() {

    companion object {

        private val DEFAULT_ICON_SIZE = MaterialDialogUtil.dpToPx(40)

        fun createItemProviderFromStringRes(
            items: List<String>,
            iconSize: Int = DEFAULT_ICON_SIZE
        ): ItemProvider.List {
            return createItemProviderFromItems(
                items
                    .mapIndexed { index, s -> SimpleListItem(index.toLong(), s.asText()) },
                iconSize
            )
        }

        fun createItemProviderFromStrings(
            items: List<String>,
            iconSize: Int = DEFAULT_ICON_SIZE
        ): ItemProvider.List {
            return createItemProviderFromItems(
                items
                    .mapIndexed { index, s -> SimpleListItem(index.toLong(), s.asText()) },
                iconSize
            )
        }

        fun createItemProviderFromTexts(
            items: List<Text>,
            iconSize: Int = DEFAULT_ICON_SIZE
        ): ItemProvider.List {
            return createItemProviderFromItems(
                items
                    .mapIndexed { index, s -> SimpleListItem(index.toLong(), s) },
                iconSize
            )
        }

        fun createItemProviderFromItems(
            items: List<IListItem>,
            iconSize: Int = DEFAULT_ICON_SIZE
        ): ItemProvider.List {
            return ItemProvider.List(ArrayList(items), iconSize)
        }

    }

    @IgnoredOnParcel
    override val viewManager: IMaterialViewManager<DialogList, MdfContentListBinding> =
        ListViewManager(this)

    @IgnoredOnParcel
    override val eventManager: IMaterialEventManager<DialogList, MdfContentListBinding> =
        ListEventManager(this)

    // -----------
    // Result Events
    // -----------

    sealed class Event : IMaterialDialogEvent {
        data class Result(
            override val id: Int?,
            override val extra: Parcelable?,
            val selectedItems: List<IListItem>,
            val button: MaterialDialogButton?
        ) : Event()

        data class Cancelled(override val id: Int?, override val extra: Parcelable?) : Event()
        data class LongPressed(
            override val id: Int?,
            override val extra: Parcelable?,
            val item: IListItem
        ) : Event()
    }

    // -----------
    // Enums
    // -----------

    sealed class SelectionMode : Parcelable {

        abstract fun getInitialSelection(): SortedSet<Long>

        @Parcelize
        class SingleSelect(
            private val initialSelection: Long? = null,
            val dismissOnSelection: Boolean = false
        ) : SelectionMode() {
            override fun getInitialSelection() =
                initialSelection?.let { sortedSetOf(initialSelection) } ?: sortedSetOf()
        }

        @Parcelize
        class MultiSelect(private val initialSelection: SortedSet<Long> = sortedSetOf()) :
            SelectionMode() {
            override fun getInitialSelection() = initialSelection
        }

        @Parcelize
        object SingleClick : SelectionMode() {
            override fun getInitialSelection() = sortedSetOf<Long>()
        }

        @Parcelize
        object MultiClick : SelectionMode() {
            override fun getInitialSelection() = sortedSetOf<Long>()
        }

    }
}
