package com.arunkumarsampath.jarvis.data.conversation

import android.arch.paging.PagedList
import android.arch.paging.RxPagedListBuilder
import com.arunkumarsampath.jarvis.data.util.PagedFirebaseDatasourceFactory
import com.arunkumarsampath.jarvis.util.executor.IO
import io.reactivex.BackpressureStrategy.LATEST
import io.reactivex.Flowable
import io.reactivex.Scheduler
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseConversationRepository
@Inject
constructor(
        private val pagedFirebaseDatasourceFactory: PagedFirebaseDatasourceFactory<ConversationItem>,
        @param:IO private val io: Scheduler
) : ConversationRepository {

    override fun conversations(pageSize: Int): Flowable<PagedList<ConversationItem>> {
        return RxPagedListBuilder(pagedFirebaseDatasourceFactory, pageSize)
                .setFetchScheduler(io)
                .buildFlowable(LATEST)
    }
}