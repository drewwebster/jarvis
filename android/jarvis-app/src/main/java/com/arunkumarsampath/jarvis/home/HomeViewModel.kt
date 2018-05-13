package com.arunkumarsampath.jarvis.home

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.arunkumarsampath.jarvis.serveraccess.SendPushUseCase
import com.arunkumarsampath.jarvis.home.conversation.ConversationItem
import com.arunkumarsampath.jarvis.home.conversation.data.ConversationRepository
import io.reactivex.disposables.CompositeDisposable
import timber.log.Timber
import javax.inject.Inject

class HomeViewModel
@Inject
constructor(
        private val conversationRepository: ConversationRepository,
        private val sendPushUseCase: SendPushUseCase
) : ViewModel() {
    private val subs = CompositeDisposable()

    val conversationItemsLiveData = MutableLiveData<List<ConversationItem>>()

    init {
    }

    fun loadConversations() {
        subs.add(conversationRepository
                .conversations(100)
                .subscribe(conversationItemsLiveData::postValue))
    }

    fun sendPush(message: String) {
        Timber.d("Jarvis command : $message")
        subs.add(sendPushUseCase.buildSingle(message).subscribe())
    }

    override fun onCleared() {
        subs.clear()
    }
}
