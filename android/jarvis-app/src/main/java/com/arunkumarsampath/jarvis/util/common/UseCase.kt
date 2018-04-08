package com.arunkumarsampath.jarvis.util.common

import com.arunkumarsampath.jarvis.util.executor.IO
import com.arunkumarsampath.jarvis.util.executor.POOL
import com.arunkumarsampath.jarvis.util.executor.UI
import io.reactivex.Completable
import io.reactivex.Scheduler
import io.reactivex.Single

abstract class UseCase<in Request, Response>(
        @param:IO private val io: Scheduler,
        @param:UI private val ui: Scheduler,
        @param:POOL private val pool: Scheduler
) {

    abstract fun executeSingle(request: Request? = null): Single<Response>

    fun execute(request: Request? = null): Completable = executeSingle(request).toCompletable()
}