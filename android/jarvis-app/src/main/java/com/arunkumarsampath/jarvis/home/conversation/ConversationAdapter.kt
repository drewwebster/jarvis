package com.arunkumarsampath.jarvis.home.conversation

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

    override fun getItemViewType(position: Int): Int {
        return delegatesManager.getItemViewType(currentList!!, position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return delegatesManager.onCreateViewHolder(parent, viewType)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        delegatesManager.onBindViewHolder(currentList!!, position, holder)
    }
}
