package com.arunkumarsampath.jarvis.data.conversation

import android.arch.paging.PagedList
import io.reactivex.Flowable

interface ConversationRepository {
    fun conversations(pageSize: Int): Flowable<PagedList<ConversationItem>>
}