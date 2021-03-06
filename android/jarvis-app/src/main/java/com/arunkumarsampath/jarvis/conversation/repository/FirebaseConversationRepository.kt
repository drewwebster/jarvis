package com.arunkumarsampath.jarvis.conversation.repository

import android.arch.paging.PagedList
import android.arch.paging.RxPagedListBuilder
import com.arunkumarsampath.jarvis.conversation.Conversations
import com.arunkumarsampath.jarvis.home.conversation.ConversationItem
import com.arunkumarsampath.jarvis.util.firebase.SnapshotParser
import com.arunkumarsampath.jarvis.util.firebase.datasource.PagedFirebaseDatasourceFactory
import com.arunkumarsampath.jarvis.util.scheduler.SchedulerProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import io.reactivex.BackpressureStrategy.LATEST
import io.reactivex.Flowable
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseConversationRepository
@Inject
constructor(
        private val pagedFirebaseDatasourceFactory: PagedFirebaseDatasourceFactory<ConversationItem>,
        private val schedulerProvider: SchedulerProvider,
        @param:Conversations private val databaseReference: DatabaseReference,
        private val conversationParser: SnapshotParser<ConversationItem>
) : ConversationRepository {

    override fun pagedConversations(pageSize: Int): Flowable<PagedList<ConversationItem>> {
        val pagedListConfig = PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setInitialLoadSizeHint(pageSize * 2)
                .setPageSize(pageSize)
                .build()
        return RxPagedListBuilder(pagedFirebaseDatasourceFactory, pagedListConfig)
                .setFetchScheduler(schedulerProvider.io())
                .buildFlowable(LATEST)
    }

    override fun conversations(size: Int): Flowable<List<ConversationItem>> {
        return Flowable.create({ emitter ->
            val listener = object : ValueEventListener {
                override fun onCancelled(databaseError: DatabaseError) {
                    emitter.onError(databaseError.toException())
                }

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    emitter.onNext(dataSnapshot.children.map(conversationParser::parse))
                }

            }
            databaseReference
                    .orderByKey()
                    .limitToLast(size)
                    .addValueEventListener(listener)
            emitter.setCancellable { databaseReference.removeEventListener(listener) }
        }, LATEST)
    }
}