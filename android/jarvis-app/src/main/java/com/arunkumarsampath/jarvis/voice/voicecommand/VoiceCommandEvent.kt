package com.arunkumarsampath.jarvis.voice.voicecommand

import com.arunkumarsampath.jarvis.voice.hotword.LifecycleBoundProcess
import io.reactivex.subjects.Subject

interface VoiceCommand : LifecycleBoundProcess {


    var voiceCommandsEvent: Subject<VoiceCommandEvent>
}

sealed class VoiceCommandEvent {
    data class Command(val string: String) : VoiceCommandEvent()
    class Error : VoiceCommandEvent()
}