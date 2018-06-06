package com.arunkumarsampath.jarvis.home

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.arunkumarsampath.jarvis.conversation.repository.ConversationRepository
import com.arunkumarsampath.jarvis.conversation.usecases.ConversationQueryUseCase
import com.arunkumarsampath.jarvis.device.DeviceRepository
import com.arunkumarsampath.jarvis.home.conversation.ConversationItem
import com.arunkumarsampath.jarvis.util.scheduler.SchedulerProvider
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.processors.PublishProcessor
import javax.inject.Inject

class HomeViewModel
@Inject
constructor(
        private val conversationRepository: ConversationRepository,
        private val deviceRepository: DeviceRepository,
        private val queryUserCase: ConversationQueryUseCase,
        private val sp: SchedulerProvider
) : ViewModel() {
    private val subs = CompositeDisposable()

    val conversationItemsLiveData = MutableLiveData<List<ConversationItem>>()
    val conversationLoadingLiveData = MutableLiveData<Boolean>()

    private val conversationQueryProcessor = PublishProcessor.create<String>()
    private val conversationLoadProcessor = PublishProcessor.create<Int>()

    private var loading: Boolean = false
        set(value) = conversationLoadingLiveData.postValue(value)

    init {
        initPostMessageProcessor()
        initConversationProcessor()
    }

    var isDeviceDocked: Boolean
        get() = deviceRepository.deviceDocked
        set(value) {
            deviceRepository.deviceDocked = value
        }


    private fun initConversationProcessor() {
        subs.add(conversationLoadProcessor
                .onBackpressureBuffer()
                .doOnNext { loading = true }
                .observeOn(sp.io())
                .concatMap { conversationRepository.conversations(100).onErrorReturn { emptyList() } }
                .doOnNext { loading = true }
                .subscribe { conversations ->
                    conversationItemsLiveData.postValue(conversations)
                    loading = false
                })
    }


    private fun initPostMessageProcessor() {
        subs.add(conversationQueryProcessor
                .onBackpressureBuffer()
                .doOnNext { loading = true }
                .observeOn(sp.io())
                .concatMapSingle(queryUserCase::buildSingle)
                .doOnNext { loading = false }
                .subscribe())
    }

    fun loadConversations() {
        conversationLoadProcessor.onNext(0)
    }

    fun sendQuery(query: String) {
        conversationQueryProcessor.onNext(query)
    }

    override fun onCleared() {
        subs.clear()
    }
}
