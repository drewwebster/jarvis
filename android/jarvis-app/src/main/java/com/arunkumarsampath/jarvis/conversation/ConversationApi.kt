package com.arunkumarsampath.jarvis.conversation

import com.arunkumarsampath.jarvis.conversation.model.ConversationQuery
import io.reactivex.Completable
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface ConversationApi {

    @POST("/conversation/conversations")
    fun conversationQuery(
            @Header("Authorization") authorization: String,
            @Body query: ConversationQuery
    ): Completable
}