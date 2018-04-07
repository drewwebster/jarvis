package com.arunkumarsampath.jarvis.util.executor

import android.os.Handler
import android.os.Looper
import dagger.Module
import dagger.Provides
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import javax.inject.Singleton

@Module
class ExecutorModule {
    @Provides
    @Singleton
    @IO
    fun ioExecutor(): Executor = Executors.newFixedThreadPool(5)

    @Provides
    @Singleton
    @UI
    fun uiExecutor(): Executor = MainThreadExecutor()

    inner class MainThreadExecutor : Executor {
        private val handler = Handler(Looper.getMainLooper())

        override fun execute(command: Runnable) {
            handler.post(command)
        }
    }
}