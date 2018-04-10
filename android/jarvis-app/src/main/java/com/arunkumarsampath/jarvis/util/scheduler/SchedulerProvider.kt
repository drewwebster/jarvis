package com.arunkumarsampath.jarvis.util.scheduler

import io.reactivex.Scheduler

interface SchedulerProvider {
    fun io(): Scheduler
    fun ui(): Scheduler
    fun pool(): Scheduler
}