package com.arunkumarsampath.jarvis.home

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.arunkumarsampath.jarvis.conversation.ConversationRepository
import com.arunkumarsampath.jarvis.device.DeviceRepository
import com.arunkumarsampath.jarvis.home.conversation.ConversationItem
import com.arunkumarsampath.jarvis.serveraccess.PostMessageUseCase
import com.arunkumarsampath.jarvis.util.scheduler.SchedulerProvider
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.processors.PublishProcessor
import javax.inject.Inject

class HomeViewModel
@Inject
constructor(
        private val conversationRepository: ConversationRepository,
        private val deviceRepository: DeviceRepository,
        private val postMessageUseCase: PostMessageUseCase,
        private val sp: SchedulerProvider
) : ViewModel() {
    private val subs = CompositeDisposable()

    val conversationItemsLiveData = MutableLiveData<List<ConversationItem>>()
    val conversationLoadingLiveData = MutableLiveData<Boolean>()

    private val postMessageProcessor = PublishProcessor.create<String>()

    init {
        initPostMessageProcessor()
    }

    private fun initPostMessageProcessor() {
        subs.add(postMessageProcessor.onBackpressureBuffer()
                .doOnNext { conversationLoadingLiveData.postValue(true) }
                .concatMapSingle { message ->
                    postMessageUseCase.buildSingle(message).onErrorReturn { false }
                }
                .doOnNext { conversationLoadingLiveData.postValue(false) }
                .subscribe())
    }

    var isDeviceDocked: Boolean
        get() = deviceRepository.deviceDocked
        set(value) {
            deviceRepository.deviceDocked = value
        }

    fun loadConversations() {
        subs.add(conversationRepository
                .conversations(100)
                .doOnNext { conversationLoadingLiveData.postValue(true) }
                .subscribe { conversations ->
                    conversationItemsLiveData.postValue(conversations)
                    conversationLoadingLiveData.postValue(false)
                })
    }

    fun sendCommand(message: String) {
        postMessageProcessor.onNext(message)
    }

    override fun onCleared() {
        subs.clear()
    }
}
