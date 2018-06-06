package com.arunkumarsampath.jarvis.conversation.usecases

import com.arunkumarsampath.jarvis.conversation.ConversationApi
import com.arunkumarsampath.jarvis.conversation.model.ConversationQuery
import com.arunkumarsampath.jarvis.util.common.UseCase
import com.arunkumarsampath.jarvis.util.scheduler.SchedulerProvider
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Named

class ConversationQueryUseCase
@Inject
constructor(
        private val conversationApi: ConversationApi,
        @param:Named("Authorization") private val tokenTask: Single<String>,
        private val sp: SchedulerProvider
) : UseCase<String, Boolean>() {

    override fun buildSingle(request: String): Single<Boolean> {
        return tokenTask
                .subscribeOn(sp.io())
                .observeOn(sp.io())
                .flatMapCompletable { conversationApi.conversationQuery(it, ConversationQuery(request)) }
                .toSingleDefault(true)
                .onErrorReturn { false }
    }
}