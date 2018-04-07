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
    fun ioExecutor(): Scheduler = Schedulers.io()

    @Provides
    @UI
    fun uiExecutor(): Scheduler = AndroidSchedulers.mainThread()
}