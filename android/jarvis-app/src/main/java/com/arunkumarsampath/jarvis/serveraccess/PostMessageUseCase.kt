package com.arunkumarsampath.jarvis.serveraccess

import com.arunkumarsampath.jarvis.BuildConfig
import com.arunkumarsampath.jarvis.device.DeviceRepository
import com.arunkumarsampath.jarvis.serveraccess.join.api.JoinMessagingApi
import com.arunkumarsampath.jarvis.serveraccess.local.api.LocalHostApi
import com.arunkumarsampath.jarvis.util.common.UseCase
import com.arunkumarsampath.jarvis.util.scheduler.SchedulerProvider
import io.reactivex.Single
import javax.inject.Inject

class PostMessageUseCase
@Inject
constructor(private val joinMessagingApi: JoinMessagingApi,
            private val localHostApi: LocalHostApi,
            private val deviceRepository: DeviceRepository,
            val sp: SchedulerProvider) : UseCase<String, Boolean>(sp) {

    override fun buildSingle(request: String?): Single<Boolean> {
        return if (!deviceRepository.deviceDocked) {
            joinMessagingApi.sendCommand(
                    text = "pivoice=:=$request",
                    deviceId = BuildConfig.PICHROME_DEVICE_ID,
                    apikey = BuildConfig.JOIN_API_KEY
            ).toSingle { true }
                    .subscribeOn(sp.io())
                    .observeOn(sp.ui())
        } else {
            localHostApi.sendCommand(request!!)
                    .toSingle { true }
                    .subscribeOn(sp.io())
                    .observeOn(sp.ui())
        }
    }
}