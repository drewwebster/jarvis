package com.arunkumarsampath.jarvis.home

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.arunkumarsampath.jarvis.data.chat.ConversationItem
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.FlowableEmitter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import javax.inject.Inject


class HomeViewModel @Inject constructor() : ViewModel() {

    private val subs = CompositeDisposable()

    val conversationItemsLiveData = MutableLiveData<List<ConversationItem>>()

    private val conversationRef = FirebaseDatabase.getInstance()
            .getReference("/conversations/conversations_log")

    init {
        subs.add(Flowable.create(
                { emitter: FlowableEmitter<List<ConversationItem>> ->
                    val valueEventListener = object : ValueEventListener {
                        override fun onCancelled(error: DatabaseError?) {
                            Timber.e(error.toString())
                        }

                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            val conversations = dataSnapshot
                                    .children
                                    .map { child ->
                                        child.getValue(ConversationItem::class.java)
                                                .apply { this!!.key = child.key }
                                    }.toList()
                            @Suppress("UNCHECKED_CAST")
                            emitter.onNext(conversations as List<ConversationItem>)
                        }
                    }
                    conversationRef.addValueEventListener(valueEventListener)
                    emitter.setCancellable {
                        conversationRef.removeEventListener(valueEventListener)
                    }
                }, BackpressureStrategy.LATEST)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(conversationItemsLiveData::setValue, Timber::e))
    }

    override fun onCleared() {

    }
}
