package com.arunkumarsampath.jarvis.voice.hotword

import io.reactivex.Observable

interface HotwordDetector : LifecycleBoundProcess {
    sealed class HotwordEvent {
        class Detected : HotwordEvent()
        class Error : HotwordEvent()
    }

    var hotwordEvents: Observable<HotwordEvent>
}