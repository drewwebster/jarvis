package com.arunkumarsampath.jarvis.util.common

import com.arunkumarsampath.jarvis.util.scheduler.SchedulerProvider
import io.reactivex.Completable
import io.reactivex.Single

abstract class UseCase<in Request, Response>(val schedulerProvider: SchedulerProvider) {

    abstract fun executeSingle(request: Request? = null): Single<Response>

    fun execute(request: Request? = null): Completable = executeSingle(request).toCompletable()
}