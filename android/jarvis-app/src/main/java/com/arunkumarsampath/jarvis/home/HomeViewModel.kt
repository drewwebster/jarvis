package com.arunkumarsampath.jarvis.home

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.arch.paging.PagedList
import com.arunkumarsampath.jarvis.data.conversation.ConversationItem
import com.arunkumarsampath.jarvis.data.conversation.ConversationRepository
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

class HomeViewModel @Inject constructor(conversationRepository: ConversationRepository) : ViewModel() {
    private val subs = CompositeDisposable()

    val conversationItemsLiveData = MutableLiveData<PagedList<ConversationItem>>()

    init {
        conversationItemsLiveData.postValue(conversationRepository.conversations())
    }

    override fun onCleared() {
        subs.clear()
    }
}
