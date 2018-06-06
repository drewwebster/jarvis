package com.arunkumarsampath.jarvis.conversation.repository

import android.arch.paging.PagedList
import com.arunkumarsampath.jarvis.home.conversation.ConversationItem
import io.reactivex.Flowable

interface ConversationRepository {
    fun pagedConversations(pageSize: Int): Flowable<PagedList<ConversationItem>>

    fun conversations(size: Int): Flowable<List<ConversationItem>>
}