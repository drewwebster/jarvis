package com.arunkumarsampath.jarvis.voice.hotword

import android.arch.lifecycle.LifecycleOwner
import io.reactivex.Observable

interface HotwordDetector {

    sealed class HotwordEvent {
        class Detected : HotwordEvent()
        class Error : HotwordEvent()
    }

    sealed class HotwordStatus {
        class Started : HotwordStatus()
        class Stopped : HotwordStatus()
    }

    var hotwordEvents: Observable<HotwordEvent>

    var hotwordStatus: Observable<HotwordStatus>

    fun start()

    fun stop()

    fun cleanup(lifecycleOwner: LifecycleOwner)
}
