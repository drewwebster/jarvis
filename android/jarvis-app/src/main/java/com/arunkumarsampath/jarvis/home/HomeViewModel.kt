package com.arunkumarsampath.jarvis.home

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.arch.paging.PagedList
import com.arunkumarsampath.jarvis.data.conversation.ConversationItem
import com.arunkumarsampath.jarvis.data.conversation.ConversationRepository
import com.arunkumarsampath.jarvis.util.executor.UI
import io.reactivex.Scheduler
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

class HomeViewModel
@Inject
constructor(
        conversationRepository: ConversationRepository,
        @param:UI private val scheduler: Scheduler
) : ViewModel() {
    private val subs = CompositeDisposable()

    val conversationItemsLiveData = MutableLiveData<PagedList<ConversationItem>>()

    init {
        subs.add(conversationRepository
                .conversations(200)
                .subscribe(conversationItemsLiveData::postValue))
    }

    override fun onCleared() {
        subs.clear()
    }
}
