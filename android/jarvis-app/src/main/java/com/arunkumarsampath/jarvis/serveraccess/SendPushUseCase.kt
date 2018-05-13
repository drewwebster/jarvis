package com.arunkumarsampath.jarvis.serveraccess

import com.arunkumarsampath.jarvis.BuildConfig
import com.arunkumarsampath.jarvis.serveraccess.api.JoinMessagingApi
import com.arunkumarsampath.jarvis.util.common.UseCase
import com.arunkumarsampath.jarvis.util.scheduler.SchedulerProvider
import io.reactivex.Single
import javax.inject.Inject

class SendPushUseCase
@Inject
constructor(val sp: SchedulerProvider,
            private val joinMessagingApi: JoinMessagingApi) : UseCase<String, String>(sp) {

    override fun buildSingle(request: String?): Single<String> {
        return joinMessagingApi.sendPush(
                text = "pivoice=:=$request",
                deviceId = BuildConfig.PICHROME_DEVICE_ID,
                apikey = BuildConfig.JOIN_API_KEY
        ).toSingle({ "success!" /*TODO*/ })
                .subscribeOn(sp.io())
                .observeOn(sp.ui())
    }
}