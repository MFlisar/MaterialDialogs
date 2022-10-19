package com.michaelflisar.dialogs

import android.content.Context
import android.graphics.Typeface
import android.os.Parcelable
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.widget.ImageView
import android.widget.TextView
import androidx.core.text.toSpannable
import com.michaelflisar.dialogs.classes.*
import com.michaelflisar.dialogs.interfaces.*
import com.michaelflisar.dialogs.list.R
import com.michaelflisar.dialogs.list.databinding.MdfContentListBinding
import com.michaelflisar.text.Text
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
    override val menu: Int? = null,
    // specific fields
    val items: Items,
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
) : MaterialDialogSetup<DialogList>() {

    companion object {

        private val DEFAULT_ICON_SIZE = MaterialDialogUtil.dpToPx(40)

        fun createList(
            items: List<Text>,
            iconSize: Int = DEFAULT_ICON_SIZE
        ): Items.List {
            return Items.List(
                items.mapIndexed { index, s -> SimpleListItem(index.toLong(), s) },
                iconSize
            )
        }
    }

    @IgnoredOnParcel
    override val viewManager: IMaterialViewManager<DialogList, MdfContentListBinding> =
        ListViewManager(this)

    @IgnoredOnParcel
    override val eventManager: IMaterialEventManager<DialogList> =
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

        data class Action(
            override val id: Int?,
            override val extra: Parcelable?,
            override val data: MaterialDialogAction
        ) : Event(), IMaterialDialogEvent.Action

        data class LongPressed(
            override val id: Int?,
            override val extra: Parcelable?,
            val item: IListItem
        ) : Event()
    }

    // -----------
    // Enums/Classes
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

    sealed class Items : Parcelable {

        abstract val iconSize: Int

        @Parcelize
        class List constructor(
            val items: kotlin.collections.List<IListItem>,
            override val iconSize: Int = MaterialDialogUtil.dpToPx(40)
        ) : Items()

        @Parcelize
        class Loader(
            val loader: IListItemsLoader,
            override val iconSize: Int = MaterialDialogUtil.dpToPx(40)
        ) : Items()
    }

    @Parcelize
    class Filter(
        private val searchInText: Boolean = true,
        private val searchInSubText: Boolean = true,
        private val highlight: Boolean = true,
        private val algorithm: Algorithm = Algorithm.String,
        private val ignoreCase: Boolean = true,
        override val unselectInvisibleItems: Boolean = true
    ) : IListFilter {

        @IgnoredOnParcel
        private val regexSplitWords = "\\s+".toRegex()

        override fun matches(context: Context, item: IListItem, filter: String): Boolean {
            if (filter.isEmpty())
                return true
            return when (algorithm) {
                Algorithm.String -> matchesWord(context, item, filter)
                Algorithm.Words -> {
                    // we highlight all words that we can find
                    val words = filter.split(regexSplitWords)
                    var matches = false
                    for (word in words) {
                        if (matchesWord(context, item, word)) {
                            matches = true
                            break
                        }
                    }
                    matches
                }
            }
        }

        override fun displayText(
            tv: TextView,
            item: IListItem,
            filter: String
        ): CharSequence {
            return if (filter.isEmpty() || !searchInText || !highlight)
                item.text.display(tv)
            else {
                val text = getHighlightedText(tv.context, item.text, filter)
                tv.text = text
                text
            }
        }

        override fun displaySubText(
            tv: TextView,
            item: IListItem,
            filter: String
        ): CharSequence {
            return if (filter.isEmpty() || !searchInSubText || !highlight)
                item.subText.display(tv)
            else {
                val text = getHighlightedText(tv.context, item.subText, filter)
                tv.text = text
                text
            }
        }

        private fun matchesWord(context: Context, item: IListItem, word: String): Boolean {
            return if (searchInText && item.text.get(context).toString().contains(word, ignoreCase))
                true
            else if (searchInSubText && item.subText.get(context).toString()
                    .contains(word, ignoreCase)
            )
                true
            else
                false
        }

        private fun getHighlightedText(context: Context, text: Text, filter: String): Spannable {
            var spannable = SpannableString(text.get(context)).toSpannable()
            return when (algorithm) {
                Algorithm.String -> {
                    // we highlight the full search term
                    highlightText(context, spannable, filter)
                }
                Algorithm.Words -> {
                    // we highlight all words that we can find
                    val words = filter.split(regexSplitWords)
                    for (word in words)
                        spannable = highlightText(context, spannable, word)
                    spannable
                }
            }
        }

        private fun highlightText(context: Context, spannable: Spannable, text: String): Spannable {
            var ofe: Int = spannable.indexOf(text, 0, ignoreCase)
            var ofs = 0
            while (ofs < spannable.length && ofe != -1) {
                ofe = spannable.indexOf(text, ofs, ignoreCase)
                if (ofe == -1) break else {
                    // bold span
                    spannable.setSpan(
                        StyleSpan(Typeface.BOLD),
                        ofe,
                        ofe + text.length,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                    // color span
                    spannable.setSpan(
                        ForegroundColorSpan(
                            MaterialDialogUtil.getThemeColorAttr(
                                context,
                                R.attr.colorAccent
                            )
                        ),
                        ofe,
                        ofe + text.length,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }
                ofs = ofe + 1
            }
            return spannable
        }

        enum class Algorithm {
            String,
            Words
        }
    }

    @Parcelize
    class Formatter(
        private val labelSelected: String
    ) : IListInfoFormatter {
        override fun formatInfo(itemsTotal: Int, itemsFiltered: Int, itemsSelected: Int): String {
            return (if (itemsFiltered == itemsTotal) {
                itemsFiltered.toString()
            } else "$itemsFiltered / $itemsTotal") + if (itemsSelected > 0) {
                " ($labelSelected: $itemsSelected)"
            } else ""
        }
    }

    @Parcelize
    data class SimpleListItem(
        override val listItemId: Long,
        override val text: Text,
        override val subText: Text = Text.Empty,
        val resIcon: Int? = null
    ) : IListItem {
        override fun displayIcon(imageView: ImageView): Boolean {
            resIcon?.let { imageView.setImageResource(it) }
            return resIcon != null
        }
    }
}
