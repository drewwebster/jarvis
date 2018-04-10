package com.arunkumarsampath.jarvis.voice.hotword

interface LifecycleBoundProcess {
    fun start()

    fun stop()

    fun cleanup()
}