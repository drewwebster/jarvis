package com.arunkumarsampath.jarvis.voice.hotword

import io.reactivex.subjects.Subject

interface HotwordDetector {
    sealed class HotwordEvent {
        class Detected : HotwordEvent()
        class Error : HotwordEvent()
    }

    var hotwordEvents: Subject<HotwordEvent>

    fun start()

    fun stop()

    fun cleanup()
}