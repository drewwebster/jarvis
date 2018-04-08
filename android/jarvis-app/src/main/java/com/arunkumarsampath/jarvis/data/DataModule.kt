package com.arunkumarsampath.jarvis.data

import com.arunkumarsampath.jarvis.data.conversation.ConversationItem
import com.arunkumarsampath.jarvis.data.conversation.ConversationRepository
import com.arunkumarsampath.jarvis.data.conversation.Conversations
import com.arunkumarsampath.jarvis.data.conversation.FirebaseConversationRepository
import com.arunkumarsampath.jarvis.data.util.PagedFirebaseDatasourceFactory
import com.arunkumarsampath.jarvis.data.util.SnapshotParser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DataModule {

    @Provides
    fun providesFb(): FirebaseDatabase {
        return FirebaseDatabase.getInstance().apply { setPersistenceEnabled(true) }
    }

    @Provides
    @Singleton
    fun conversationRepository(firebaseConversationRepository: FirebaseConversationRepository): ConversationRepository {
        return firebaseConversationRepository
    }

    @Provides
    @Singleton
    @Conversations
    fun provideConversationsReference(firebaseDb: FirebaseDatabase): DatabaseReference {
        return firebaseDb.getReference(CONVERSATION_PATH)
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

    companion object {
        const val CONVERSATION_PATH = "/conversations/conversations_log"

        val conversationItemParser = object : SnapshotParser<ConversationItem> {
            override fun parse(dataSnapshot: DataSnapshot): ConversationItem {
                return dataSnapshot.getValue(ConversationItem::class.java)!!
                        .apply { this.key = dataSnapshot.key }
            }
        }
    }
}