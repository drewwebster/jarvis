package com.arunkumarsampath.jarvis.home

import com.airbnb.epoxy.Typed2EpoxyController
import com.arunkumarsampath.jarvis.data.chat.ConversationItem
import com.arunkumarsampath.jarvis.data.chat.conversation

class ConversationController : Typed2EpoxyController<List<ConversationItem>, Boolean>() {

    override fun buildModels(convesations: List<ConversationItem>, loadingMore: Boolean) {
        convesations.forEach { item ->
            conversation {
                id(item.key)
                message(item.content)
            }
        }
    }
}