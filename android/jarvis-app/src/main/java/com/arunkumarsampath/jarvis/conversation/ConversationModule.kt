package com.arunkumarsampath.jarvis.conversation

import com.arunkumarsampath.jarvis.BuildConfig
import com.arunkumarsampath.jarvis.home.conversation.ConversationItem
import com.arunkumarsampath.jarvis.util.firebase.SnapshotParser
import com.arunkumarsampath.jarvis.util.firebase.datasource.PagedFirebaseDatasourceFactory
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
class ConversationModule {

    @Provides
    @Singleton
    @Conversations
    fun provideConversationsReference(firebaseDb: FirebaseDatabase): DatabaseReference {
        return firebaseDb.getReference(CONVERSATION_PATH)
    }

    @Provides
    @Singleton
    fun conversationRepository(firebaseConversationRepository: FirebaseConversationRepository): ConversationRepository {
        return firebaseConversationRepository
    }

    @Provides
    @Singleton
    fun pagedFirebaseDatasourceFactory(@Conversations databaseReference: DatabaseReference
    ): PagedFirebaseDatasourceFactory<ConversationItem> {
        return PagedFirebaseDatasourceFactory(conversationItemParser, databaseReference)
    }

    @Provides
    @Singleton
    fun conversationParser(): SnapshotParser<ConversationItem> = conversationItemParser


    @Provides
    @Singleton
    fun conversationApi(okHttpClient: OkHttpClient): ConversationApi {
        val retrofit = Retrofit.Builder()
                .baseUrl(BuildConfig.JARVIS_BASE_URL)
                .client(okHttpClient)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(MoshiConverterFactory.create())
                .build()
        return retrofit.create(ConversationApi::class.java)
    }

    companion object {
        const val CONVERSATION_PATH = "/conversations/conversationsLog"

        val conversationItemParser = object : SnapshotParser<ConversationItem> {
            override fun parse(dataSnapshot: DataSnapshot): ConversationItem {
                return dataSnapshot.getValue(ConversationItem::class.java)!!
                        .apply { this.key = dataSnapshot.key }
            }
        }
    }
}