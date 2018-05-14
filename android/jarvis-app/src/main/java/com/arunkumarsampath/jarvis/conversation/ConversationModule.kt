package com.arunkumarsampath.jarvis.conversation

import com.arunkumarsampath.jarvis.util.firebase.PagedFirebaseDatasourceFactory
import com.arunkumarsampath.jarvis.util.firebase.SnapshotParser
import com.arunkumarsampath.jarvis.home.conversation.ConversationItem
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class ConversationModule {

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