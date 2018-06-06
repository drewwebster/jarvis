package com.arunkumarsampath.jarvis.util.common

import io.reactivex.Single

abstract class UseCase<in Request, Response> {

    abstract fun buildSingle(request: Request): Single<Response>
}