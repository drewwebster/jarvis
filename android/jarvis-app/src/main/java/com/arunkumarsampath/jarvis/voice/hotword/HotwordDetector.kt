package com.arunkumarsampath.jarvis.voice.hotword

import io.reactivex.subjects.Subject

interface HotwordDetector : LifecycleBoundProcess {
    sealed class HotwordEvent {
        class Detected : HotwordEvent()
        class Error : HotwordEvent()
    }

    var hotwordEvents: Subject<HotwordEvent>
}