package com.arunkumarsampath.jarvis.data

import com.arunkumarsampath.jarvis.data.conversation.ConversationItem
import com.arunkumarsampath.jarvis.data.conversation.ConversationRepository
import com.arunkumarsampath.jarvis.data.conversation.FirebaseConversationRepository
import com.arunkumarsampath.jarvis.data.util.PagedFirebaseDatasourceFactory
import com.arunkumarsampath.jarvis.data.util.SnapshotParser
import com.google.firebase.database.DataSnapshot
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
    fun pagedFirebaseDatasourceFactory(firebaseDb: FirebaseDatabase): PagedFirebaseDatasourceFactory<ConversationItem> {
        return PagedFirebaseDatasourceFactory(
                conversationItemParser,
                firebaseDb.getReference(CONVERSATION_PATH)
        )
    }

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