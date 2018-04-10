package com.arunkumarsampath.jarvis.voice.speech

import com.arunkumarsampath.jarvis.voice.hotword.LifecycleBoundProcess
import io.reactivex.subjects.Subject

interface SpeechToTextConverter : LifecycleBoundProcess {
    sealed class SpeechEvents {
        data class Detected(val string: String) : SpeechEvents()
        class Error : SpeechEvents()
    }

    var speechEvents: Subject<SpeechEvents>
}