package com.arunkumarsampath.jarvis.voice.hotword

import io.reactivex.subjects.Subject

interface HotwordDetector {
    enum class HotwordEvent {
        HOTWORD_DETECTED,
        HOTWORD_ERROR
    }

    var hotwordEvents: Subject<HotwordEvent>

    fun start()

    fun stop()

    fun cleanup()
}