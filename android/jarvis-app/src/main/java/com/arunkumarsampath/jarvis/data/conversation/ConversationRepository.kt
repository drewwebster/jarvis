package com.arunkumarsampath.jarvis.data.conversation

import android.arch.paging.PagedList
import io.reactivex.Flowable

interface ConversationRepository {
    fun pagedConversations(pageSize: Int): Flowable<PagedList<ConversationItem>>

    fun conversations(size: Int): Flowable<List<ConversationItem>>
}