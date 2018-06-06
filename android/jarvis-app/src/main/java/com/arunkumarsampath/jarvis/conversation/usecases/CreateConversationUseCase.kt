package com.arunkumarsampath.jarvis.conversation.usecases

import com.arunkumarsampath.jarvis.device.DeviceRepository
import com.arunkumarsampath.jarvis.util.common.UseCase
import com.arunkumarsampath.jarvis.util.scheduler.SchedulerProvider
import io.reactivex.Single
import javax.inject.Inject

class CreateConversationUseCase
@Inject
constructor(private val deviceRepository: DeviceRepository,
            val sp: SchedulerProvider) : UseCase<String, Boolean>(sp) {

    override fun buildSingle(request: String): Single<Boolean> {
        return Single.just(true)
    }
}