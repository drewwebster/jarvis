package com.arunkumarsampath.jarvis.data.conversation

import android.arch.paging.PagedList
import com.arunkumarsampath.jarvis.data.util.PagedFirebaseDatasourceFactory
import com.arunkumarsampath.jarvis.util.executor.IO
import com.arunkumarsampath.jarvis.util.executor.UI
import java.util.concurrent.Executor
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseConversationRepository
@Inject
constructor(
        private val pagedFirebaseDatasourceFactory: PagedFirebaseDatasourceFactory<ConversationItem>,
        @param:IO private val ioExecutor: Executor,
        @param:UI private val uiExecutor: Executor
) : ConversationRepository {

    override fun conversations(): PagedList<ConversationItem> {
        val config = with(PagedList.Config.Builder()) {
            setEnablePlaceholders(false)
            setInitialLoadSizeHint(10)
            setPageSize(10)
            setPrefetchDistance(5)
            build()
        }
        return with(PagedList.Builder(pagedFirebaseDatasourceFactory.create(), config)) {
            setFetchExecutor(ioExecutor)
            setNotifyExecutor(uiExecutor)
            build()
        }
    }
}