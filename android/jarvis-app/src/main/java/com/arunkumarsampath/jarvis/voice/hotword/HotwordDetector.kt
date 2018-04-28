package com.arunkumarsampath.jarvis.voice.hotword

import android.arch.lifecycle.LifecycleOwner
import io.reactivex.Observable

interface HotwordDetector {

    sealed class HotwordEvent {
        class Detected : HotwordEvent()
        class Error : HotwordEvent()
    }

    var hotwordEvents: Observable<HotwordEvent>

    fun start()

    fun stop()

    fun cleanup(lifecycleOwner: LifecycleOwner)
}
