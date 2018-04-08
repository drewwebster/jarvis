package com.arunkumarsampath.jarvis.util.executor

import dagger.Module
import dagger.Provides
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

@Module
class SchedulerModule {
    @Provides
    @IO
    fun io(): Scheduler = Schedulers.io()

    @Provides
    @UI
    fun ui(): Scheduler = AndroidSchedulers.mainThread()

    @Provides
    @POOL
    fun pool(): Scheduler = Schedulers.computation()
}