package com.arunkumarsampath.jarvis.home

import com.airbnb.epoxy.Typed2EpoxyController
import com.arunkumarsampath.jarvis.data.chat.ConversationItem
import com.arunkumarsampath.jarvis.data.chat.conversationModelJarvis
import com.arunkumarsampath.jarvis.data.chat.conversationModelUser

class ConversationController : Typed2EpoxyController<List<ConversationItem>, Boolean>() {

    override fun buildModels(convesations: List<ConversationItem>, loadingMore: Boolean) {
        convesations.forEach { item ->
            if (item.who == ConversationItem.JARVIS) {
                conversationModelJarvis {
                    id(item.key)
                    message(item.content)
                }
            } else if (item.who == ConversationItem.USER) {
                conversationModelUser {
                    id(item.key)
                    message(item.content)
                }
            }
        }
    }
}