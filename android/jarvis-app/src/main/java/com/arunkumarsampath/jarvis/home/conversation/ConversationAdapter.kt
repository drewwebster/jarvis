package com.arunkumarsampath.jarvis.home.conversation

import android.arch.paging.AsyncPagedListDiffer
import android.arch.paging.PagedListAdapter
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.arunkumarsampath.jarvis.data.conversation.ConversationItem
import com.hannesdorfmann.adapterdelegates3.AdapterDelegatesManager

class ConversationAdapter : PagedListAdapter<ConversationItem, RecyclerView.ViewHolder>(ConversationItem.Companion.ConversationDiffCallback()) {

    private val delegatesManager = AdapterDelegatesManager<List<ConversationItem>>().apply {
        addDelegate(JarvisMessageDelegate())
        addDelegate(UserMessageDelegate())
    }

    private val delegatedPageList = DelegatedPageList()

    private val currentSafeList: List<ConversationItem>
        get() = currentList!!

    override fun getItemViewType(position: Int): Int {
        return delegatesManager.getItemViewType(delegatedPageList, position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return delegatesManager.onCreateViewHolder(parent, viewType)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        delegatesManager.onBindViewHolder(delegatedPageList, position, holder)
    }

    /**
     * Simple delegate list that forwards calls to [PagedListAdapter.getCurrentList]
     *
     *[PagedListAdapter] works with [AsyncPagedListDiffer] and delegates calls to the differ when
     * [PagedListAdapter.getItem], [PagedListAdapter.getItemCount]s are called. Since logic for loading
     * next page is written in [PagedListAdapter] as opposed to [PagedList] that we provide, we pass
     * this delegate to [AdapterDelegatesManager], so that relevant logic in [PagedListAdapter] is invoked.
     */
    inner class DelegatedPageList : List<ConversationItem> {
        override val size: Int
            get() = itemCount

        override fun contains(element: ConversationItem) = currentSafeList.contains(element)

        override fun containsAll(elements: Collection<ConversationItem>) = currentSafeList.containsAll(elements)

        override fun get(index: Int): ConversationItem = getItem(index)!!

        override fun indexOf(element: ConversationItem) = currentSafeList.indexOf(element)

        override fun isEmpty() = currentSafeList.isEmpty()

        override fun iterator() = currentSafeList.iterator()

        override fun lastIndexOf(element: ConversationItem) = currentSafeList.lastIndexOf(element)

        override fun listIterator() = currentSafeList.listIterator()

        override fun listIterator(index: Int) = currentSafeList.listIterator(index)

        override fun subList(fromIndex: Int, toIndex: Int) = currentSafeList.subList(fromIndex, toIndex)
    }
}
