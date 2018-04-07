package com.arunkumarsampath.jarvis.data.conversation

import android.arch.paging.PagedList

interface ConversationRepository {
    fun conversations(): PagedList<ConversationItem>
}