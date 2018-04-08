package com.arunkumarsampath.jarvis.device

import com.arunkumarsampath.jarvis.BuildConfig
import com.arunkumarsampath.jarvis.device.api.JoinMessagingApi
import com.arunkumarsampath.jarvis.util.common.UseCase
import com.arunkumarsampath.jarvis.util.executor.IO
import com.arunkumarsampath.jarvis.util.executor.POOL
import com.arunkumarsampath.jarvis.util.executor.UI
import io.reactivex.Scheduler
import io.reactivex.Single
import javax.inject.Inject

class SendPushUseCase
@Inject
constructor(@param:IO private val io: Scheduler,
            @param:UI private val ui: Scheduler,
            @param:POOL private val pool: Scheduler,
            private val joinMessagingApi: JoinMessagingApi) : UseCase<String, String>(io, ui, pool) {

    override fun executeSingle(request: String?): Single<String> {
        return joinMessagingApi.sendPush(
                text = """pivoice=:=$request""",
                deviceId = BuildConfig.PICHROME_DEVICE_ID,
                apikey = BuildConfig.JOIN_API_KEY
        ).toSingle({ "success!" /*TODO*/ })
                .subscribeOn(io)
                .observeOn(ui)
    }
}