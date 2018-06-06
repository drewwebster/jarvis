package com.arunkumarsampath.jarvis.util.common

import com.arunkumarsampath.jarvis.util.scheduler.SchedulerProvider
import io.reactivex.Single

abstract class UseCase<in Request, Response>(val schedulerProvider: SchedulerProvider) {

    abstract fun buildSingle(request: Request): Single<Response>
}