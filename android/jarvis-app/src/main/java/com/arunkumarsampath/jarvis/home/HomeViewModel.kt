package com.arunkumarsampath.jarvis.home

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.arunkumarsampath.jarvis.conversation.ConversationRepository
import com.arunkumarsampath.jarvis.device.DeviceRepository
import com.arunkumarsampath.jarvis.home.conversation.ConversationItem
import com.arunkumarsampath.jarvis.serveraccess.PostMessageUseCase
import io.reactivex.disposables.CompositeDisposable
import timber.log.Timber
import javax.inject.Inject

class HomeViewModel
@Inject
constructor(
        private val conversationRepository: ConversationRepository,
        private val deviceRepository: DeviceRepository,
        private val postMessageUseCase: PostMessageUseCase
) : ViewModel() {
    private val subs = CompositeDisposable()

    val conversationItemsLiveData = MutableLiveData<List<ConversationItem>>()

    var isDeviceDocked: Boolean
        get() = deviceRepository.deviceDocked
        set(value) {
            deviceRepository.deviceDocked = value
        }

    fun loadConversations() {
        subs.add(conversationRepository
                .conversations(100)
                .subscribe(conversationItemsLiveData::postValue))
    }

    fun sendPush(message: String) {
        Timber.d("Jarvis command : $message")
        subs.add(postMessageUseCase.buildSingle(message).subscribe())
    }

    override fun onCleared() {
        subs.clear()
    }
}
