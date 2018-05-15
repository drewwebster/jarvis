package com.arunkumarsampath.jarvis.home.conversation

import android.support.v7.recyclerview.extensions.AsyncDifferConfig
import android.support.v7.recyclerview.extensions.AsyncListDiffer
import android.support.v7.util.AdapterListUpdateCallback
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.arunkumarsampath.jarvis.di.scopes.PerActivity
import com.arunkumarsampath.jarvis.home.conversation.ConversationItem.Companion.ConversationDiffCallback
import com.arunkumarsampath.jarvis.home.conversation.delegates.JarvisMessageDelegate
import com.arunkumarsampath.jarvis.home.conversation.delegates.UserMessageDelegate
import com.google.firebase.auth.FirebaseAuth
import com.hannesdorfmann.adapterdelegates3.AdapterDelegatesManager
import javax.inject.Inject

@PerActivity
class ConversationAdapter
@Inject
constructor(var firebaseAuth: FirebaseAuth) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val helper = AsyncListDiffer<ConversationItem>(
            AdapterListUpdateCallback(this),
            AsyncDifferConfig.Builder(ConversationDiffCallback()).build()
    )

    var currentList: List<ConversationItem>
        get() = helper.currentList
        set(value) = helper.submitList(value)

    private val delegatesManager = AdapterDelegatesManager<List<ConversationItem>>().apply {
        addDelegate(JarvisMessageDelegate())
        addDelegate(UserMessageDelegate(firebaseAuth.currentUser))
    }

    override fun getItemCount(): Int {
        return helper.currentList.size
    }

    override fun getItemViewType(position: Int): Int {
        return delegatesManager.getItemViewType(currentList, position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return delegatesManager.onCreateViewHolder(parent, viewType)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        delegatesManager.onBindViewHolder(currentList, position, holder)
    }
}
