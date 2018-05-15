package com.arunkumarsampath.jarvis.home.conversation.delegates

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.arunkumarsampath.jarvis.R
import com.arunkumarsampath.jarvis.extensions.inflate
import com.arunkumarsampath.jarvis.home.conversation.ConversationItem
import com.arunkumarsampath.jarvis.util.glide.GlideApp
import com.google.firebase.auth.FirebaseUser
import com.hannesdorfmann.adapterdelegates3.AdapterDelegate
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.layout_conversation_item_user.*


class UserMessageDelegate(private val currentUser: FirebaseUser?) : AdapterDelegate<List<ConversationItem>>() {

    override fun isForViewType(items: List<ConversationItem>, position: Int): Boolean {
        return items[position].who == ConversationItem.USER
    }

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        return MessageViewHolder(parent.inflate(R.layout.layout_conversation_item_user), currentUser)
    }

    override fun onBindViewHolder(
            items: List<ConversationItem>,
            position: Int,
            holder: RecyclerView.ViewHolder,
            payloads: List<Any>
    ) {
        (holder as MessageViewHolder).bind(items[position])
    }

    internal class MessageViewHolder(
            override val containerView: View?,
            private val currentUser: FirebaseUser?
    ) : RecyclerView.ViewHolder(containerView), LayoutContainer {

        fun bind(conversationItem: ConversationItem) {
            conversationText.text = conversationItem.content
            currentUser?.let {
                GlideApp.with(conversationUserImage)
                        .load(currentUser.photoUrl)
                        .circleCrop()
                        .into(conversationUserImage)
            }
        }
    }
}
